import React from 'react';
import { View, StyleSheet, ActivityIndicator } from 'react-native';
import { black333 } from '../../utils/Colors';
const ProgressHUD = (props) => {
    return (React.createElement(View, { style: style.container },
        React.createElement(ActivityIndicator, { color: props.color ?? black333, size: "large" })));
};
const style = StyleSheet.create({
    container: {
        position: 'absolute',
        width: '100%',
        height: '100%',
        justifyContent: 'center',
        alignItems: 'center',
    },
});
export default ProgressHUD;
