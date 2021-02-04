import React from 'react'
import {TouchableOpacity, Text, StyleSheet, StatusBar, View} from 'react-native'
import {white, black} from '../common/Colors'
import {SourceType} from '../screen/MediaLib'
import type {BaseProps} from '../common/BaseProps'
import type {ParamList} from '../common/models'

type Props = BaseProps<ParamList, 'MediaSelector'>

const MediaSelectorPage = (props: Props) => {
  const navigation = props.navigation

  const onPressImage = async () => {
    navigation.navigate('MediaLib', {
      maxSelectedMediaCount: 9,
      isVideoOnly: false,
      from: SourceType.main,
    })
  }

  const onPressVideo = async () => {
    navigation.navigate('MediaLib', {
      maxSelectedMediaCount: 1,
      isVideoOnly: true,
      from: SourceType.main,
    })
  }

  return (
    <>
      <StatusBar backgroundColor={black} barStyle="light-content" />
      <View style={styles.rootContainer}>
        <TouchableOpacity onPress={onPressVideo} style={[styles.buttonContainer, {marginTop: 500}]}>
          <Text style={styles.textStyle}>视频</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={onPressImage} style={[styles.buttonContainer, {top: 35}]}>
          <Text style={[styles.textStyle]}>图文</Text>
        </TouchableOpacity>
      </View>
    </>
  )
}

export default MediaSelectorPage

const styles = StyleSheet.create({
  rootContainer: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#000000',
    opacity: 0.8,
  },
  buttonContainer: {
    width: 145,
    height: 52,
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#999999',
    borderRadius: 9,
  },
  textStyle: {
    flex: 1,
    color: white,
    fontSize: 25,
    fontWeight: '600',
    justifyContent: 'center',
    includeFontPadding: false,
    textAlignVertical: 'center',
  },
})
