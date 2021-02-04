import React, {useState} from 'react'
import {View, TouchableOpacity, Image, StyleSheet} from 'react-native'
import {MediaLibraryPhotoPreview, finishSelectMedia} from 'react-native-awesome-medialib'
import {RootSiblingParent} from 'react-native-root-siblings'
import {isIphoneX} from 'react-native-iphone-x-helper'
import type {BaseProps} from '../common/BaseProps'
import {black, white} from '../common/Colors'
import BackArrow from '../images/back_arrow_white.png'
import {showToast} from '../utils/Utils'
import ProgressHUD from '../components/ProgressHUD'
import {SourceType} from './MediaLib'
import type {ParamList} from '../common/models'

type Props = BaseProps<ParamList, 'PicturePreview'>

const PicturePreviewPage = (props: Props) => {
  const {navigation, route} = props
  const param = route.params
  const [showProgressHUD, setShowProgressHUD] = useState<boolean>(false)

  const onFinishSelect = async () => {
    setShowProgressHUD(true)
    try {
      const res = await finishSelectMedia()
      console.warn(`res:${JSON.stringify(res)}`)
      // navigation.setResult({medias: res})
      if (param?.from === SourceType.main) {
        // navigation.push('SnackDetailEditorPage', {medias: res})
      } else if (param?.from === SourceType.editor) {
        // navigation.dismiss()
      } else if (param?.from === SourceType.avatar) {
        // const photo = res[0]
        // navigation.push('PhotoCropperPage', {
        //  url: photo.url,
        //  scale: photo.height / photo.width,
        // })
      } else {
        navigation.goBack()
      }
    } catch (error) {
      onShowToast('导出失败')
    } finally {
      setShowProgressHUD(false)
    }
  }

  const progressHUD = () => <ProgressHUD color={white} />

  const onShowToast = (desc: string) => {
    showToast(desc, isIphoneX() ? 119 : 98)
  }

  return (
    <RootSiblingParent>
      <View style={{flex: 1, backgroundColor: black}}>
        <MediaLibraryPhotoPreview
          onFinishSelect={onFinishSelect}
          onShowToast={(desc: string) => onShowToast(desc)}
          style={{position: 'absolute', width: '100%', height: '100%'}}
        />
        <View style={styles.navigationBar}>
          <TouchableOpacity onPress={() => navigation.pop()} style={styles.backButton}>
            <Image source={BackArrow} />
          </TouchableOpacity>
        </View>
        {showProgressHUD ? progressHUD() : null}
      </View>
    </RootSiblingParent>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: white,
  },
  navigationBar: {
    backgroundColor: 'rgba(26, 26, 26, 0.5)',
    width: '100%',
    height: isIphoneX() ? 88 : 64,
  },
  backButton: {
    width: 24,
    height: 24,
    position: 'absolute',
    left: 16,
    bottom: 15,
  },
})

export default PicturePreviewPage
