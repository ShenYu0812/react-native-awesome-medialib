import React from 'react';
import { TouchableOpacity, Text, Dimensions, StyleSheet, StatusBar, View } from 'react-native';
import { black333, white, black } from '../utils/Colors';
// /Users/shenyu/projects/my-npm/react-native-awesome-medialib/src/utils/Utils.ts
// const windowWidth = Dimensions.get('window').width
const windowHeight = Dimensions.get('window').height;
export const MediaSelectorPage = (props) => {
    const close = () => {
        props.navigator.dismiss(false);
    };
    return (React.createElement(React.Fragment, null,
        React.createElement(StatusBar, { backgroundColor: black, barStyle: "light-content" }),
        React.createElement(View, { style: [styles.transparentContainer] },
            React.createElement(View, { style: [styles.textContainer] },
                React.createElement(TouchableOpacity, { onPress: async () => {
                        props.navigator.setResult({ type: 'video' });
                        props.navigator.dismiss();
                    } },
                    React.createElement(Text, { style: styles.textStyle }, "\u89C6\u9891")),
                React.createElement(TouchableOpacity, { onPress: async () => {
                        props.navigator.setResult({ type: 'image' });
                        props.navigator.dismiss();
                    } },
                    React.createElement(Text, { style: styles.textStyle }, "\u56FE\u6587"))),
            React.createElement(View, { style: styles.cancelButton },
                React.createElement(TouchableOpacity, { onPress: close },
                    React.createElement(Text, { style: styles.textStyle }, "\u53D6\u6D88"))))));
};
const styles = StyleSheet.create({
    transparentContainer: {
        flex: 1,
        alignItems: 'center',
        backgroundColor: 'black',
    },
    textContainer: {
        width: 135,
        height: 68,
        top: windowHeight - 200,
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    textStyle: { color: white, fontSize: 18, fontWeight: '600' },
    cancelButton: {
        backgroundColor: black333,
        width: 135,
        height: 44,
        justifyContent: 'center',
        alignItems: 'center',
        top: windowHeight - 150,
        borderRadius: 8,
    },
});
MediaSelectorPage.navigationItem = {
    hideNavigationBar: true,
};
