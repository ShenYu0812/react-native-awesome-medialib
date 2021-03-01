import React, { useState, useCallback, useEffect } from 'react';
import { View, TouchableOpacity, Text, StyleSheet, Image, Platform, PermissionsAndroid, StatusBar, } from 'react-native';
import { FlatList } from 'react-native-gesture-handler';
import { isIphoneX } from 'react-native-iphone-x-helper';
import { RootSiblingParent } from 'react-native-root-siblings';
import { useVisibleEffect } from 'react-native-awesome-navigation';
import ProgressHUD from '../components/basic/ProgressHUD';
import ThreeStageNavigationBar from '../components/basic/ThreeStageNavigationBar';
import { MediaLibrary } from '../components/NativeMediaLibraryView';
import { MediaLibraryBottomToolBar } from '../components/basic/MediaLibraryBottomToolBar';
import { MediaLibraryAlbumItem } from '../components/basic/MediaLibraryAlbumItem';
import { fetchAllAssets, fetchAllAlbums, requestLibraryAuthorization, requestCameraAuthorization, clear, startCameraPreview, stopCameraPreview, onSelectAlbumAtIndex, finishSelectMedia, fetchVideoURL, } from '../bridge/MediaLibraryBridge';
import { black1A, white, black } from '../utils/Colors';
import { requestSinglePermission } from '../utils/PermissionChecker';
import { albumListStyle, processAlbumModel, showToast } from '../utils/Utils';
import DismissButton from '../images/dismiss_white_button.png';
import DownArrow from '../images/down_white_arrow.png';
export var SourceType;
(function (SourceType) {
    SourceType["main"] = "main";
    SourceType["editor"] = "editor";
    SourceType["avatar"] = "avatar";
})(SourceType || (SourceType = {}));
export const MediaLibraryPage = (props) => {
    const [maxSelectedMediaCount] = useState(props.maxSelectedMediaCount ?? 9);
    const [isVideoOnly] = useState(props.isVideoOnly ?? false);
    const [selectedMediaCount, setSelectedMediaCount] = useState(0);
    const [showProgressHUD, setShowProgressHUD] = useState(false);
    const [currentAlbum, setCurrentAlbum] = useState();
    const [albumListVisable, setAlbumListVisable] = useState(false);
    const [albumDataModel, setAlbumDataModel] = useState([]);
    const initalLibrary = async (callback) => {
        if (Platform.OS === 'ios') {
            const libraryAuthGranted = await requestLibraryAuthorization();
            if (libraryAuthGranted)
                fetchMediaResource();
            if (!isVideoOnly) {
                const cameraAuthGranted = await requestCameraAuthorization();
                if (cameraAuthGranted)
                    startCameraPreview();
            }
        }
        else {
            const isAuthorized = await PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE);
            if (!isAuthorized) {
                requestSinglePermission(PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE, callback);
            }
            else {
                fetchMediaResource();
            }
        }
    };
    const readPermissionAndroidCallback = (granted) => {
        if (granted === PermissionsAndroid.RESULTS.GRANTED) {
            fetchMediaResource();
        }
        else {
            console.warn(`to do when deny permission`);
        }
    };
    const fetchMediaResource = async () => {
        fetchAllAssets(isVideoOnly);
        const res = await fetchAllAlbums();
        if (res && res.length > 0) {
            const models = processAlbumModel(res);
            setAlbumDataModel(models);
            // iOS首次进入默认展示第一个相册
            setCurrentAlbum(models[0]);
        }
    };
    useVisibleEffect(props.screenID, useCallback(() => {
        startCameraPreview();
        return () => {
            stopCameraPreview();
        };
    }, []));
    useEffect(() => {
        initalLibrary(readPermissionAndroidCallback);
        return () => {
            clear();
        };
    }, []);
    const bottomToolBar = () => {
        return props.isVideoOnly || albumListVisable ? null : (React.createElement(MediaLibraryBottomToolBar, { onDoneButtonPress: onFinishSelect, selectedMediaCount: selectedMediaCount }));
    };
    const progressHUD = () => React.createElement(ProgressHUD, { color: white });
    const renderItem = ({ item }) => {
        return (React.createElement(MediaLibraryAlbumItem, { albumCount: item.count, albumCover: item.cover, albumName: item.name, onItemPress: () => onSelectAlbum(item) }));
    };
    const onSelectAlbum = (item) => {
        onSelectAlbumAtIndex(item.index);
        setAlbumListVisable(false);
        setCurrentAlbum(item);
    };
    const showAlbumList = async () => {
        if (albumListVisable) {
            setAlbumListVisable(false);
            return;
        }
        if (albumDataModel.length > 0) {
            setAlbumListVisable(true);
            return;
        }
        setShowProgressHUD(true);
        try {
            const res = await fetchAllAlbums();
            if (res && res.length > 0) {
                const models = processAlbumModel(res);
                setAlbumDataModel(models);
                setShowProgressHUD(false);
                setAlbumListVisable(true);
            }
        }
        catch (error) {
            setShowProgressHUD(false);
        }
    };
    const navigationLeft = () => (React.createElement(View, { style: styles.navigationBarLeftItem },
        React.createElement(TouchableOpacity, { onPress: () => props.navigator.dismiss() },
            React.createElement(Image, { source: DismissButton }))));
    const navigationMiddle = () => (React.createElement(View, null,
        React.createElement(TouchableOpacity, { activeOpacity: 1, onPress: () => showAlbumList(), style: { flexDirection: 'row', alignItems: 'center' } },
            React.createElement(Text, { style: { fontSize: 16, fontWeight: '600', color: white } }, currentAlbum?.name),
            currentAlbum ? React.createElement(Image, { source: DownArrow }) : null)));
    const onFinishSelect = async () => {
        setShowProgressHUD(true);
        try {
            const res = await finishSelectMedia();
            if (props.from === SourceType.main) {
                props.navigator.push('SnackDetailEditorPage', { medias: res });
            }
            else if (props.from === SourceType.editor) {
                props.navigator.setResult({ medias: res });
                props.navigator.dismiss();
            }
            else if (props.from === SourceType.avatar) {
                const photo = res[0];
                props.navigator.push('PhotoCropperPage', {
                    url: photo.url,
                    scale: photo.height / photo.width,
                });
            }
            else {
                props.navigator.setResult({ medias: res });
                props.navigator.dismiss();
            }
        }
        catch (error) {
            onShowToast('导出失败');
        }
        finally {
            setShowProgressHUD(false);
        }
    };
    const onPushCameraPage = () => {
        props.navigator.push('CameraPage');
    };
    const onPushPreviewPage = async () => {
        if (isVideoOnly) {
            try {
                const res = await fetchVideoURL();
                if (res) {
                    props.navigator.push('VideoPreviewPage', { url: res.url, scale: res.scale });
                }
            }
            catch (error) {
                onShowToast(error.message);
            }
        }
        else {
            props.navigator.push('MediaLibraryPhotoPreviewPage', { from: props.from });
        }
    };
    const onShowToast = (desc) => {
        showToast(desc, isIphoneX() ? 119 : 98);
    };
    const onMediaItemSelect = (e) => {
        setSelectedMediaCount(e.nativeEvent.selectedMediaCount);
    };
    const onAlbumUpdate = (e) => {
        const newAlbums = e.nativeEvent.newAlbums;
        const models = processAlbumModel(newAlbums);
        setAlbumDataModel(models);
    };
    return (React.createElement(RootSiblingParent, null,
        React.createElement(StatusBar, { backgroundColor: black, barStyle: "light-content" }),
        React.createElement(View, { style: styles.container },
            React.createElement(ThreeStageNavigationBar, { leftItem: () => navigationLeft(), middleItem: () => navigationMiddle(), style: { backgroundColor: black1A } }),
            React.createElement(MediaLibrary, { maxSelectedMediaCount: maxSelectedMediaCount, onAlbumUpdate: onAlbumUpdate, onMediaItemSelect: onMediaItemSelect, onPushCameraPage: onPushCameraPage, onPushPreviewPage: onPushPreviewPage, onShowToast: onShowToast, style: { flex: 1, width: '100%', height: '100%', backgroundColor: black1A } }),
            bottomToolBar(),
            albumListVisable ? (React.createElement(FlatList, { data: albumDataModel, keyExtractor: item => item.index.toString(), renderItem: renderItem, style: albumListStyle.list })) : null,
            showProgressHUD ? progressHUD() : null)));
};
const styles = StyleSheet.create({
    container: {
        flex: 1,
        width: '100%',
        height: '100%',
        flexDirection: 'column',
        backgroundColor: black1A,
    },
    navigationBarLeftItem: { marginLeft: 16 },
});
MediaLibraryPage.navigationItem = {
    hideNavigationBar: true,
};
