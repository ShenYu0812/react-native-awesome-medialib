import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import withIPX from './NavigationBar';
const ThreeStageNavigationBar = (props) => {
    const middleView = () => {
        return props.title && props.title.length > 0 ? (React.createElement(Text, { style: { fontSize: 18 } }, props.title)) : (props.middleItem && props.middleItem());
    };
    return (React.createElement(View, { style: [styles.navigation, props.style] },
        React.createElement(View, { style: styles.itemContainer }, props.leftItem && props.leftItem()),
        middleView(),
        React.createElement(View, { style: styles.itemContainer }, props.rightItem && props.rightItem())));
};
export default withIPX(ThreeStageNavigationBar);
const styles = StyleSheet.create({
    navigation: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
    },
    itemContainer: {
        height: '100%',
        width: 80,
        justifyContent: 'center',
    },
});
