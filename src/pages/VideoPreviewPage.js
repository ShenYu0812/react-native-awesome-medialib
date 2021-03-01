import React, { useState } from 'react';
import { View, TouchableOpacity, Text, StyleSheet, StatusBar } from 'react-native';
import Video from 'react-native-video';
import { isIphoneX } from 'react-native-iphone-x-helper';
import CameraNavigationBar from '../components/basic/CameraHeader';
import ProgressHUD from '../components/basic/ProgressHUD';
import { black, white } from '../utils/Colors';
import { showToast } from '../utils/Utils';
// import {compressVideo} from '../bridge/MediaLibraryBridge'
import { windowWidth } from '../components/video_player/styles';
export const VideoPreviewPage = (props) => {
    console.warn(`video preview page:${JSON.stringify(props)}`);
    const [progress, setProgress] = useState(0);
    const [compressing, setCompressing] = useState(false);
    const onCompress = async () => {
        try {
            setCompressing(true);
            // const resp = await compressVideo(props.url)
            // TODO
            // props.navigator.push('SnackDetailEditorPage', {medias: resp})
        }
        catch (e) {
            showToast('上传失败');
        }
        finally {
            setCompressing(false);
        }
    };
    return (React.createElement(React.Fragment, null,
        React.createElement(View, { style: styles.container },
            React.createElement(StatusBar, { backgroundColor: black, barStyle: "light-content" }),
            React.createElement(CameraNavigationBar, { onPress: () => props.navigator.pop() }),
            React.createElement(View, { style: [styles.progressBar, { width: windowWidth * progress }] }),
            React.createElement(Video, { onProgress: data => setProgress(data.currentTime / data.seekableDuration), resizeMode: "contain", source: { uri: props.url }, style: { flex: 1 } }),
            React.createElement(View, { style: styles.bottomContainer },
                React.createElement(TouchableOpacity, { onPress: onCompress, style: styles.buttonIcon },
                    React.createElement(Text, { style: { color: white, fontSize: 18 } }, "\u4E0B\u4E00\u6B65")))),
        compressing ? React.createElement(ProgressHUD, null) : null));
};
VideoPreviewPage.navigationItem = {
    hideNavigationBar: true,
};
const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: black, justifyContent: 'space-between' },
    header: {
        width: '100%',
        height: isIphoneX() ? 88 : 64,
        justifyContent: 'flex-end',
    },
    progressBar: { height: 1, backgroundColor: white, marginTop: 10 },
    backIconContainer: {
        justifyContent: 'center',
        height: 28,
        width: 28,
        marginLeft: 16,
    },
    backIcon: { width: 28, height: 28, tintColor: white },
    bottomContainer: {
        flexDirection: 'row',
        justifyContent: 'center',
        alignItems: 'center',
        height: 72,
        width: '100%',
        marginBottom: 10,
    },
    buttonIcon: {
        justifyContent: 'center',
        alignItems: 'center',
        height: 40,
    },
});
