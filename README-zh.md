# react-native-awesome-medialib

react-native-awesome-medialib 是一款基于封装原生组件的实用媒体选择器库。它以开箱即用的方式提供, 包含图片选择、照片拍摄、视频选择、预览等页面. 我们强烈推荐在实用此库的同时，以 reac-native-awesome-navigation 作为其路由导航组件。react-native-awesome-medialib内部页面跳转采用的正是此路由导航组件。

## 安装

```命令
npm install react-native-awesome-medialib
```或者```
yarn add react-native-awesome-medialib

```您应当同时安装react-native-awesome-navigation```
[github链接]:(https://github.com/Project5E/react-native-awesome-navigation) 
```

## 实用方法
# 1. 首先，确保`react-native-awesome-medialib`, `react-native-awesome-navigation` 都被安装。
# 2. 其次，使用{#Register.registerComponent} 让如下所有页面被注册, 在媒体选择器入口页面或者按钮使用导航方法进入MediaSelectorPage
  当然，使用诸如`react-navigation`等其他导航库是允许的，按照您使用的导航库的使用方式导航进入MediaSelectorPage页面。但是，我们推荐使用`react-native-awesome-navigation`，即使使用其他导航库，`react-native-awesome-navigation`也需要被安装，因为我们的内部页面跳转是由其实现的。

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
# 3. 最后，享受吧
<br/>

## 贡献者

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
