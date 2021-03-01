import React, { useState } from 'react';
import { View, TouchableOpacity, Image, StyleSheet } from 'react-native';
import { isIphoneX } from 'react-native-iphone-x-helper';
import { RootSiblingParent } from 'react-native-root-siblings';
import ProgressHUD from '../components/basic/ProgressHUD';
import { MediaLibraryPhotoPreview } from '../components/NativeMediaLibraryPhotoPreview';
import { finishSelectMedia } from '../bridge/MediaLibraryBridge';
import BackArrow from '../images/back_arrow_white.png';
import { black, white } from '../utils/Colors';
import { showToast } from '../utils/Utils';
import { SourceType } from './MediaLibraryPage';
export const MediaLibraryPhotoPreviewPage = (props) => {
    const [showProgressHUD, setShowProgressHUD] = useState(false);
    const onFinishSelect = async () => {
        setShowProgressHUD(true);
        try {
            const res = await finishSelectMedia();
            props.navigator.setResult({ medias: res });
            if (props.from === SourceType.main) {
                props.navigator.push('SnackDetailEditorPage', { medias: res });
            }
            else if (props.from === SourceType.editor) {
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
    const progressHUD = () => React.createElement(ProgressHUD, { color: white });
    const onShowToast = (desc) => {
        showToast(desc, isIphoneX() ? 119 : 98);
    };
    return (React.createElement(RootSiblingParent, null,
        React.createElement(View, { style: { flex: 1, backgroundColor: black } },
            React.createElement(MediaLibraryPhotoPreview, { onFinishSelect: onFinishSelect, onShowToast: (desc) => onShowToast(desc), style: { position: 'absolute', width: '100%', height: '100%' } }),
            React.createElement(View, { style: styles.navigationBar },
                React.createElement(TouchableOpacity, { onPress: () => props.navigator.pop(), style: styles.backButton },
                    React.createElement(Image, { source: BackArrow }))),
            showProgressHUD ? progressHUD() : null)));
};
const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: white,
    },
    navigationBar: {
        backgroundColor: 'rgba(26, 26, 26, 0.5)',
        width: '100%',
        height: isIphoneX() ? 88 : 64,
    },
    backButton: {
        width: 24,
        height: 24,
        position: 'absolute',
        left: 16,
        bottom: 15,
    },
});
MediaLibraryPhotoPreviewPage.navigationItem = {
    hideNavigationBar: true,
};
