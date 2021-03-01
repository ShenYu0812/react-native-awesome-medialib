import React from 'react';
import { StyleSheet, TouchableOpacity, View, Text, Image } from 'react-native';
import { black1A, white, grayB2 } from '../../utils/Colors';
export const MediaLibraryAlbumItem = (props) => {
    return (React.createElement(TouchableOpacity, { activeOpacity: 1, onPress: () => props.onItemPress() },
        React.createElement(View, { style: albumItemStyle.container },
            React.createElement(Image, { source: { uri: props.albumCover }, style: albumItemStyle.cover }),
            React.createElement(View, { style: albumItemStyle.textContainer },
                React.createElement(Text, { style: albumItemStyle.name }, props.albumName),
                React.createElement(Text, { style: albumItemStyle.count }, props.albumCount)))));
};
export const albumItemStyle = StyleSheet.create({
    container: {
        flex: 1,
        height: 112,
        backgroundColor: black1A,
        flexDirection: 'row',
        alignItems: 'center',
    },
    cover: { marginLeft: 16, width: 80, height: 80 },
    textContainer: { flexDirection: 'column', marginLeft: 16 },
    name: { fontSize: 16, fontWeight: '600', color: white },
    count: { fontSize: 12, fontWeight: '400', color: grayB2, marginTop: 4 },
});
