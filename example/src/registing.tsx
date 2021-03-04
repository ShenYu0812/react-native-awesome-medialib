import {Register} from 'react-native-awesome-navigation'
import {
  MediaSelectorPage,
  MediaLibraryPage,
  CameraPage,
  MediaLibraryPhotoPreviewPage,
  PhotoPreviewPage,
  VideoPreviewPage,
} from 'react-native-awesome-medialib'
import {Home} from './Home'

export const registing = async () => {
  Register.beforeRegister()

  Register.registerComponent('Home', Home)
  Register.registerComponent('MediaSelectorPage', MediaSelectorPage)
  Register.registerComponent('CameraPage', CameraPage)
  Register.registerComponent('PhotoPreviewPage', PhotoPreviewPage)
  Register.registerComponent('MediaLibraryPage', MediaLibraryPage)
  Register.registerComponent('VideoPreviewPage', VideoPreviewPage)
  Register.registerComponent('MediaLibraryPhotoPreviewPage', MediaLibraryPhotoPreviewPage)
  // Register.registerComponent('PhotoCropperPage', PhotoCropperPage)

  console.warn(`invoke register set root:`)
  Register.setRoot({
    root: {
      stack: {
        root: {
          screen: {
            moduleName: 'Home',
          },
        },
      },
    },
  })
}
