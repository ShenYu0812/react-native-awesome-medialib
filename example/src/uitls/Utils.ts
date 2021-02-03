import {StyleSheet} from 'react-native'
import {isIphoneX} from 'react-native-iphone-x-helper'
import Toast from 'react-native-root-toast'
import {black444, black1A} from '../common/Colors'

export function showToast(title: string, position = 120) {
  Toast.show(title, {
    containerStyle: {borderRadius: 19, width: 251},
    textStyle: {fontSize: 14, fontWeight: 'bold'},
    position,
    backgroundColor: black444,
    shadow: false,
    opacity: 1,
  })
}

export const albumListStyle = StyleSheet.create({
  list: {
    position: 'absolute',
    top: isIphoneX() ? 88 : 64,
    bottom: 0,
    width: '100%',
    backgroundColor: black1A,
  },
})
