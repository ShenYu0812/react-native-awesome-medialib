import React from 'react';
import { StyleSheet, TouchableOpacity, View, Text } from 'react-native';
import { isIphoneX } from 'react-native-iphone-x-helper';
import { black1A, gray73, white } from '../../utils/Colors';
export const MediaLibraryBottomToolBar = (props) => {
    return (React.createElement(View, { style: bottomToolBarStyle().container },
        React.createElement(View, { style: bottomToolBarStyle().background },
            React.createElement(Text, { style: bottomToolBarStyle().selectCountText },
                "\u5DF2\u9009 ",
                props.selectedMediaCount,
                " \u5F20"),
            React.createElement(TouchableOpacity, { disabled: props.selectedMediaCount === 0, onPress: () => props.onDoneButtonPress(), style: bottomToolBarStyle().doneButton },
                React.createElement(Text, { style: bottomToolBarStyle(props.selectedMediaCount).doneButtonText }, "\u4E0B\u4E00\u6B65")))));
};
export const bottomToolBarStyle = (selectCount) => StyleSheet.create({
    container: { height: isIphoneX() ? 84 : 50, backgroundColor: black1A },
    background: {
        height: 50,
        backgroundColor: black1A,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
    },
    selectCountText: {
        color: white,
        fontSize: 14,
        left: 16,
        fontWeight: '600',
    },
    doneButton: {
        width: 56,
        height: 25,
        right: 16,
        justifyContent: 'center',
    },
    doneButtonText: {
        fontSize: 18,
        fontWeight: '600',
        color: selectCount && selectCount > 0 ? white : gray73,
    },
});
