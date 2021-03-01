import React from 'react';
import { View, TouchableOpacity, Text, StyleSheet, StatusBar, Dimensions } from 'react-native';
import FastImage from 'react-native-fast-image';
import { saveImage, deletePhoto } from '../bridge/NativeCameraBridge';
import CameraNavigationBar from '../components/basic/CameraHeader';
import { black, white } from '../utils/Colors';
const windowWidth = Dimensions.get('window').width;
export const PhotoPreviewPage = (props) => {
    return (React.createElement(View, { style: styles.container },
        React.createElement(StatusBar, { backgroundColor: black, barStyle: "light-content" }),
        React.createElement(CameraNavigationBar, { onPress: () => props.navigator.pop() }),
        React.createElement(FastImage, { source: { uri: props.url }, style: { width: windowWidth, height: windowWidth * props.scale } }),
        React.createElement(View, { style: styles.bottomContainer },
            React.createElement(TouchableOpacity, { onPress: () => {
                    deletePhoto(props.url);
                    props.navigator.pop();
                }, style: [styles.buttonIcon, styles.buttonLeft] },
                React.createElement(Text, { style: styles.photobuttonText }, "\u91CD\u62CD")),
            React.createElement(TouchableOpacity, { onPress: async () => {
                    await saveImage(props.url); // 保存图片到本地相册
                    props.navigator.popPages(2);
                }, style: [styles.buttonIcon, styles.buttonRight] },
                React.createElement(Text, { style: styles.photobuttonText }, "\u786E\u5B9A")))));
};
PhotoPreviewPage.navigationItem = {
    hideNavigationBar: true,
};
const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: black, justifyContent: 'space-between' },
    bottomContainer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        height: 72,
        width: '100%',
        marginBottom: 20,
    },
    buttonIcon: {
        justifyContent: 'space-between',
        alignItems: 'center',
        height: 40,
        width: 40,
    },
    buttonLeft: {
        marginLeft: 40,
    },
    buttonRight: {
        marginRight: 40,
    },
    takePhotoIcon: {
        height: 72,
        width: 72,
    },
    photobuttonText: {
        color: white,
        fontSize: 18,
    },
});
