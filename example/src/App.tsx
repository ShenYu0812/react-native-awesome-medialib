import React from 'react'
import {StatusBar, StyleSheet, View} from 'react-native'
import {MediaLibrary} from 'react-native-awesome-medialib'
import {RootSiblingParent} from 'react-native-root-siblings'
import {black, black1A} from './uitls/Color'

export const App = () => {
  const onAlbumUpdate = (e: any) => {
    console.warn(`onAlbumUpdate:${JSON.stringify(e)}`)
  }

  const onMediaItemSelect = (e: any) => {
    console.warn(`onMediaItemSelect:${JSON.stringify(e)}`)
  }

  const onPushCameraPage = () => {
    console.warn(`onPushCameraPage`)
  }

  const onPushPreviewPage = async () => {
    console.warn(`onPushPreviewPage`)
  }

  const onShowToast = (desc: string) => {
    console.warn(`onShowToast:${desc}`)
  }

  return (
    <RootSiblingParent>
      <StatusBar backgroundColor={black} barStyle="light-content" />
      <View style={styles.container}>
        <MediaLibrary
          maxSelectedMediaCount={9}
          onAlbumUpdate={onAlbumUpdate}
          onMediaItemSelect={onMediaItemSelect}
          onPushCameraPage={onPushCameraPage}
          onPushPreviewPage={onPushPreviewPage}
          onShowToast={onShowToast}
          style={{flex: 1, backgroundColor: black1A}}
        />
      </View>
    </RootSiblingParent>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
})
