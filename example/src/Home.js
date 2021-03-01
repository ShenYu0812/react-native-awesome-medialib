import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Dimensions } from 'react-native';
import { SourceType } from 'react-native-awesome-medialib';
const windowWidth = Dimensions.get('window').width;
const windowHeight = Dimensions.get('window').height;
export const Home = (props) => {
    const onPress = async () => {
        const resp = await props.navigator.present('MediaSelectorPage', {}, { isFullScreen: true, isTransparency: true, animated: true, isTabBarPresented: true });
        if (!resp) {
            return;
        }
        let params;
        if (resp.type === 'video') {
            params = {
                maxSelectedMediaCount: 1,
                isVideoOnly: true,
                from: SourceType.main,
            };
        }
        else {
            params = {
                maxSelectedMediaCount: 9,
                isVideoOnly: false,
                from: SourceType.main,
            };
        }
        props.navigator.present('MediaLibraryPage', params, {
            isFullScreen: true,
            isTabBarPresented: true,
        });
    };
    return (React.createElement(View, { style: styles.container },
        React.createElement(TouchableOpacity, { onPress: onPress, style: styles.buttonStyle },
            React.createElement(Text, { style: styles.textStyle }, "\u6253\u5F00\u56FE\u7247\u9009\u62E9\u5668"))));
};
Home.navigationItem = {
    hideNavigationBar: true,
};
const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#FFFFFF',
        justifyContent: 'space-between',
    },
    buttonStyle: {
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#333333',
        height: 50,
        width: 150,
        left: (windowWidth - 150) / 2,
        top: (windowHeight - 50) / 2,
        marginTop: 8,
        marginBottom: 8,
        paddingLeft: 8,
        paddingRight: 8,
        borderRadius: 8,
    },
    textStyle: {
        color: '#FFFFFF',
    },
});
