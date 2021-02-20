import React, {useState, useEffect} from 'react'
import {
  View,
  StyleSheet,
  Platform,
  PermissionsAndroid,
  StatusBar,
  FlatList,
  TouchableOpacity,
  Image,
  Text,
} from 'react-native'
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
  fetchVideoURL,
} from 'react-native-awesome-medialib'
import {isIphoneX} from 'react-native-iphone-x-helper'
import {RootSiblingParent} from 'react-native-root-siblings'
import {AlbumModel, ParamList, processAlbumModel} from '../common/models'
import {black1A, white, black} from '../common/Colors'
import {albumListStyle, showToast} from '../utils/Utils'
import {requestSinglePermission} from '../utils/PermissionChecker'
import ThreeStageNavigationBar from '../components/ThreeStageNavigationBar'
import ProgressHUD from '../components/ProgressHUD'
import DismissButton from '../images/dismiss_white_button.png'
import DownArrow from '../images/down_white_arrow.png'
import type {BaseProps} from '../common/BaseProps'

export enum SourceType {
  main = 'main',
  editor = 'editor',
  avatar = 'avatar',
}

export type Props = BaseProps<ParamList, 'MediaLib'>

const MediaLibPage = (props: Props) => {
  const navigation = props.navigation
  const param = props.route.params
  const [maxSelectedMediaCount] = useState<number>(param?.maxSelectedMediaCount ?? 9)
  const [isVideoOnly] = useState<boolean>(param?.isVideoOnly ?? false)
  const [selectedMediaCount, setSelectedMediaCount] = useState<number>(0)
  const [showProgressHUD, setShowProgressHUD] = useState<boolean>(false)
  const [currentAlbum, setCurrentAlbum] = useState<AlbumModel>()
  const [albumListVisable, setAlbumListVisable] = useState<boolean>(false)
  const [albumDataModel, setAlbumDataModel] = useState<AlbumModel[]>([])

  const initalLibrary = async (callback: (...parmas: any) => void) => {
    if (Platform.OS === 'ios') {
      const libraryAuthGranted = await requestLibraryAuthorization()
      if (libraryAuthGranted) fetchMediaResource()
      if (!isVideoOnly) {
        const cameraAuthGranted = await requestCameraAuthorization()
        if (cameraAuthGranted) startCameraPreview()
      }
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
    fetchAllAssets(isVideoOnly)
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
      clear()
    }
  }, [])

  const bottomToolBar = () => {
    return isVideoOnly || albumListVisable ? null : (
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

  const showAlbumList = async () => {
    if (albumListVisable) {
      setAlbumListVisable(false)
      return
    }

    if (albumDataModel.length > 0) {
      setAlbumListVisable(true)
      return
    }
    setShowProgressHUD(true)
    try {
      const res = await fetchAllAlbums()
      if (res && res.length > 0) {
        const models = processAlbumModel(res)
        setAlbumDataModel(models)
        setShowProgressHUD(false)
        setAlbumListVisable(true)
      }
    } catch (error) {
      setShowProgressHUD(false)
    }
  }

  const onSelectAlbum = (item: AlbumModel) => {
    onSelectAlbumAtIndex(item.index)
    setAlbumListVisable(false)
    setCurrentAlbum(item)
  }

  const navigationLeft = () => (
    <View style={styles.navigationBarLeftItem}>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Image source={DismissButton} />
      </TouchableOpacity>
    </View>
  )

  const navigationMiddle = () => (
    <View>
      <TouchableOpacity
        activeOpacity={1}
        onPress={() => showAlbumList()}
        style={{flexDirection: 'row', alignItems: 'center'}}>
        <Text style={{fontSize: 16, fontWeight: '600', color: white}}>{currentAlbum?.name}</Text>
        {currentAlbum ? <Image source={DownArrow} /> : null}
      </TouchableOpacity>
    </View>
  )

  const onFinishSelect = async () => {
    setShowProgressHUD(true)
    try {
      const res = await finishSelectMedia()
      console.warn(`finish select:${JSON.stringify(res)}`)
      if (param?.from === SourceType.main) {
        // navigation.push('SnackDetailEditorPage', {medias: res})
      } else if (param?.from === SourceType.editor) {
        // navigation.setResult({medias: res})
        // navigation.dismiss()
      } else if (param?.from === SourceType.avatar) {
        // const photo = res[0]
        // navigation.push('PhotoCropperPage', {
        //  url: photo.url,
        //  scale: photo.height / photo.width,
        // })
      } else {
        // navigation.setResult({medias: res})
        navigation.goBack()
      }
    } catch (error) {
      onShowToast('导出失败')
    } finally {
      setShowProgressHUD(false)
    }
  }

  const onPushCameraPage = () => {
    // navigation.navigate('CameraPage')
  }

  const onPushPreviewPage = async () => {
    if (isVideoOnly) {
      try {
        const res = await fetchVideoURL()
        if (res) {
          // navigation.navigate('VideoPreviewPage', {url: res.url, scale: res.scale})
        }
      } catch (error) {
        onShowToast(error.message)
      }
    } else {
      navigation.push('PicturePreview', {from: param?.from})
    }
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
        <ThreeStageNavigationBar
          leftItem={() => navigationLeft()}
          middleItem={() => navigationMiddle()}
          style={{backgroundColor: black1A}}
        />
        <MediaLibrary
          maxSelectedMediaCount={maxSelectedMediaCount}
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

export default MediaLibPage

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    backgroundColor: black1A,
  },
  navigationBarLeftItem: {marginLeft: 16},
})
