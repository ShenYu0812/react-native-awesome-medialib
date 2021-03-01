import { StyleSheet } from 'react-native';
import Toast from 'react-native-root-toast';
import { isIphoneX } from 'react-native-iphone-x-helper';
import { black1A, black444, grayB2, white } from '../utils/Colors';
export const showToast = (title, position = 120) => {
    Toast.show(title, {
        containerStyle: { borderRadius: 19, width: 251 },
        textStyle: { fontSize: 14, fontWeight: 'bold' },
        position,
        backgroundColor: black444,
        shadow: false,
        opacity: 1,
    });
};
export const processAlbumModel = (source) => source.map((item) => ({
    index: item.index,
    count: item.count,
    cover: item.cover,
    name: item.name,
}));
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
export const albumListStyle = StyleSheet.create({
    list: {
        position: 'absolute',
        top: isIphoneX() ? 88 : 64,
        bottom: 0,
        width: '100%',
        backgroundColor: black1A,
    },
});
