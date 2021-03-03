# react-native-awesome-medialib

A useful media selector module base on native component. It will offer a meida selector in batteries-included way, including picture selection, photo shooting, video selection, album switch, preview and so on. We recommend strongly that u should install reac-native-awesome-navigation as navigation component in project when using this lib.

## Installation

```sh
npm install react-native-awesome-medialib
```or```
yarn add react-native-awesome-medialib

```u should install react-native-awesome-navigation concurrently.```
[github link]:(https://github.com/Project5E/react-native-navigation-5e) 
```

## Usage
### 1. First, ensure `react-native-awesome-medialib`, `react-native-awesome-navigation` have been installed.
### 2. Second, use {#Register.registerComponent} make all page had been registed. 
  u also can use other navigation lib such as `react-navigation`, and register by it. but we recommond use `react-native-awesome-navigation`, because media lib internal page's navigate is used it.

```typescript
// registing.tsx
import {
  MediaSelectorPage,
  MediaLibraryPage,
  CameraPage,
  MediaLibraryPhotoPreviewPage,
  PhotoPreviewPage,
  VideoPreviewPage,
} from 'react-native-awesome-medialib'
import {Register} from 'react-native-awesome-navigation'

// ...

export const registing = async () => {
  Register.beforeRegister()

  Register.registerComponent('Your MediaLib enter page name', `Your MediaLib enter page`)
  Register.registerComponent('MediaSelectorPage', MediaSelectorPage)
  Register.registerComponent('CameraPage', CameraPage)
  Register.registerComponent('PhotoPreviewPage', PhotoPreviewPage)
  Register.registerComponent('MediaLibraryPage', MediaLibraryPage)
  Register.registerComponent('VideoPreviewPage', VideoPreviewPage)
  Register.registerComponent('MediaLibraryPhotoPreviewPage', MediaLibraryPhotoPreviewPage)
}

// index.tsx
import {registing} from '...'

registing()
```
### 3. Last, Enjoy it!
<br/>
## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
