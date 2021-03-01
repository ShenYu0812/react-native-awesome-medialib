import React, {useState} from 'react'
import {View, TouchableOpacity, Text, StyleSheet, StatusBar} from 'react-native'
import type {NavigationProps} from 'react-native-awesome-navigation'
import Video from 'react-native-video'
import {isIphoneX} from 'react-native-iphone-x-helper'
import CameraNavigationBar from '../components/basic/CameraHeader'
import ProgressHUD from '../components/basic/ProgressHUD'
import {black, white} from '../utils/Colors'
import {showToast} from '../utils/Utils'
// import {compressVideo} from '../bridge/MediaLibraryBridge'
import {windowWidth} from '../components/video_player/styles'

interface Props extends NavigationProps {
  url: string
  scale: number
}

export const VideoPreviewPage = (props: Props) => {
  console.warn(`video preview page:${JSON.stringify(props)}`)
  const [progress, setProgress] = useState(0)
  const [compressing, setCompressing] = useState(false)
  const onCompress = async () => {
    try {
      setCompressing(true)
      // const resp = await compressVideo(props.url)
      // TODO
      // props.navigator.push('SnackDetailEditorPage', {medias: resp})
    } catch (e) {
      showToast('上传失败')
    } finally {
      setCompressing(false)
    }
  }
  return (
    <>
      <View style={styles.container}>
        <StatusBar backgroundColor={black} barStyle="light-content" />
        <CameraNavigationBar onPress={() => props.navigator.pop()} />
        <View style={[styles.progressBar, {width: windowWidth * progress}]} />
        <Video
          onProgress={data => setProgress(data.currentTime / data.seekableDuration)}
          resizeMode="contain"
          source={{uri: props.url}}
          style={{flex: 1}}
        />
        <View style={styles.bottomContainer}>
          <TouchableOpacity onPress={onCompress} style={styles.buttonIcon}>
            <Text style={{color: white, fontSize: 18}}>下一步</Text>
          </TouchableOpacity>
        </View>
      </View>
      {compressing ? <ProgressHUD /> : null}
    </>
  )
}

VideoPreviewPage.navigationItem = {
  hideNavigationBar: true,
}

const styles = StyleSheet.create({
  container: {flex: 1, backgroundColor: black, justifyContent: 'space-between'},
  header: {
    width: '100%',
    height: isIphoneX() ? 88 : 64,
    justifyContent: 'flex-end',
  },
  progressBar: {height: 1, backgroundColor: white, marginTop: 10},
  backIconContainer: {
    justifyContent: 'center',
    height: 28,
    width: 28,
    marginLeft: 16,
  },
  backIcon: {width: 28, height: 28, tintColor: white},
  bottomContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    height: 72,
    width: '100%',
    marginBottom: 10,
  },
  buttonIcon: {
    justifyContent: 'center',
    alignItems: 'center',
    height: 40,
  },
})
