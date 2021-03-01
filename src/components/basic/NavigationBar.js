import React from 'react';
import { StyleSheet, View } from 'react-native';
import { isIphoneX } from 'react-native-iphone-x-helper';
import { white } from '../../utils/Colors';
const withIPX = (Component) => {
    return (props) => {
        return (React.createElement(View, { style: styles(props).container },
            React.createElement(Component, Object.assign({}, props))));
    };
};
export default withIPX;
const styles = (props) => StyleSheet.create({
    container: {
        backgroundColor: props.style?.backgroundColor ?? white,
        overflow: 'hidden',
        height: isIphoneX() ? 88 : 64,
        justifyContent: 'flex-end',
    },
});
