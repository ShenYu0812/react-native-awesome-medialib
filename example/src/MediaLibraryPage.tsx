import React, {useState, useEffect} from 'react'
import {View, StyleSheet, Platform, PermissionsAndroid, StatusBar, FlatList} from 'react-native'
import {black1A, white, black} from './uitls/Color'
import {
  MediaLibrary,
  fetchAllAssets,
  fetchAllAlbums,
  requestLibraryAuthorization,
  requestCameraAuthorization,
  clear,
  startCameraPreview,
  onSelectAlbumAtIndex,
  finishSelectMedia,
  MediaLibraryBottomToolBar,
  MediaLibraryAlbumItem,
} from 'react-native-awesome-medialib'
import {AlbumModel, processAlbumModel} from './models'
import {requestSinglePermission} from './global/PermissionChecker'
import ProgressHUD from './ProgressHUD'
import {isIphoneX} from 'react-native-iphone-x-helper'
import {albumListStyle, showToast} from './uitls/Utils'
import {RootSiblingParent} from 'react-native-root-siblings'

const MediaLibraryPage = () => {
  const [selectedMediaCount, setSelectedMediaCount] = useState<number>(0)
  const [showProgressHUD, setShowProgressHUD] = useState<boolean>(false)
  const [, setCurrentAlbum] = useState<AlbumModel>()
  const [albumListVisable, setAlbumListVisable] = useState<boolean>(false)
  const [albumDataModel, setAlbumDataModel] = useState<AlbumModel[]>([])

  const initalLibrary = async (callback: (...parmas: any) => void) => {
    if (Platform.OS === 'ios') {
      const libraryAuthGranted = await requestLibraryAuthorization()
      if (libraryAuthGranted) fetchMediaResource()

      const cameraAuthGranted = await requestCameraAuthorization()
      if (cameraAuthGranted) startCameraPreview()
    } else {
      const isAuthorized = await PermissionsAndroid.check(
        PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE
      )
      if (!isAuthorized) {
        requestSinglePermission(PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE, callback)
      } else {
        fetchMediaResource()
      }
    }
  }

  const readPermissionAndroidCallback = (granted: string) => {
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      fetchMediaResource()
    } else {
      console.warn(`to do when deny permission`)
    }
  }

  const fetchMediaResource = async () => {
    fetchAllAssets(false)
    const res = await fetchAllAlbums()
    if (res && res.length > 0) {
      const models = processAlbumModel(res)
      setAlbumDataModel(models)
      // iOS首次进入默认展示第一个相册
      setCurrentAlbum(models[0])
    }
  }

  useEffect(() => {
    initalLibrary(readPermissionAndroidCallback)
    return () => {
      if (Platform.OS === 'ios') clear()
    }
  }, [])

  const bottomToolBar = () => {
    return albumListVisable ? null : (
      <MediaLibraryBottomToolBar
        onDoneButtonPress={onFinishSelect}
        selectedMediaCount={selectedMediaCount}
      />
    )
  }

  const progressHUD = () => <ProgressHUD color={white} />

  const renderItem = ({item}: {item: AlbumModel}) => {
    return (
      <MediaLibraryAlbumItem
        albumCount={item.count}
        albumCover={item.cover}
        albumName={item.name}
        onItemPress={() => onSelectAlbum(item)}
      />
    )
  }

  const onSelectAlbum = (item: AlbumModel) => {
    onSelectAlbumAtIndex(item.index)
    setAlbumListVisable(false)
    setCurrentAlbum(item)
  }

  const onFinishSelect = async () => {
    setShowProgressHUD(true)
    try {
      const res = await finishSelectMedia()
      console.warn(`finish select:${JSON.stringify(res)}`)
      // if (props.from === SourceType.main) {
      // props.navigator.push('SnackDetailEditorPage', {medias: res})
      // } else if (props.from === SourceType.editor) {
      // props.navigator.setResult({medias: res})
      // props.navigator.dismiss()
      // } else if (props.from === SourceType.avatar) {
      // const photo = res[0]
      // props.navigator.push('PhotoCropperPage', {
      //  url: photo.url,
      //  scale: photo.height / photo.width,
      // })
      // } else {
      // props.navigator.setResult({medias: res})
      // props.navigator.dismiss()
      // }
    } catch (error) {
      onShowToast('导出失败')
    } finally {
      setShowProgressHUD(false)
    }
  }

  const onPushCameraPage = () => {
    // props.navigator.push('CameraPage')
  }

  const onPushPreviewPage = async () => {
    /* if (isVideoOnly) {
      try {
        const res = await fetchVideoURL()
        if (res) {
          props.navigator.push('VideoPreviewPage', {url: res.url, scale: res.scale})
        }
      } catch (error) {
        onShowToast(error.message)
      }
    } else {
      props.navigator.push('MediaLibraryPhotoPreviewPage', {from: props.from})
    }*/
  }

  const onShowToast = (desc: string) => {
    showToast(desc, isIphoneX() ? 119 : 98)
  }

  const onMediaItemSelect = (e: any) => {
    setSelectedMediaCount(e.nativeEvent.selectedMediaCount)
  }

  const onAlbumUpdate = (e: any) => {
    const newAlbums = e.nativeEvent.newAlbums
    const models = processAlbumModel(newAlbums)
    setAlbumDataModel(models)
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
        {bottomToolBar()}
        {albumListVisable ? (
          <FlatList
            data={albumDataModel}
            keyExtractor={item => item.index.toString()}
            renderItem={renderItem}
            style={albumListStyle.list}
          />
        ) : null}
        {showProgressHUD ? progressHUD() : null}
      </View>
    </RootSiblingParent>
  )
}

export default MediaLibraryPage

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    backgroundColor: black1A,
  },
})
