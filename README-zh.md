# react-native-awesome-medialib

react-native-awesome-medialib 是一款基于封装原生组件的实用媒体选择器库。它以开箱即用的方式提供, 包含图片选择、照片拍摄、视频选择、预览等页面. 我们强烈推荐在实用此库的同时，以 reac-native-awesome-navigation 作为其路由导航组件。react-native-awesome-medialib内部页面跳转采用的正是此路由导航组件。

## 安装

```sh
npm install react-native-awesome-medialib
```
或者
```sh
yarn add react-native-awesome-medialib
```

您应当同时安装react-native-awesome-navigation, react-native-fast-image, react-native-gesture-handler, react-native-iphone-x-helper, react-native-root-toast, 
@types/react-native-video and react-native-video,
[github链接]:(https://github.com/Project5E/react-native-awesome-navigation) 

当`react-native` < 0.59.0, 和其他RN库一样，你需要link或者手动配置
### 1. link
```sh
react-native link react-native-awesome-media
``` 
### 2. 手动配置
<1> 手动引入
```kotlin
// settings.gradle
include ':react-native-awesome-medialib'
project(':react-native-awesome-medialib').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-awesome-medialib/android')
```
<2> 添加依赖
```kotlin
// app/build.gradle
dependency {
  ...
  implementation project(':react-native-awesome-medialib')
  ...
}
```
<3> Application中添加ReactPackage
```kotlin
class MainApplication : Application(), ReactApplication {
    private val mReactNativeHost: ReactNativeHost = object : ReactNativeHost(this) {
        ...

        override fun getPackages(): List<ReactPackage> {
            val packages: MutableList<ReactPackage> = PackageList(this).packages
            packages.add(MediaLibPackage())
            return packages
        }
        ...
```

## 使用方法
### 1. 首先，确保`react-native-awesome-medialib` 和上述所有其他的包都被安装。
  如果遇到安装其他的必须包的问题，请访问相应的github查看, 判断是否需要手动link 或 setting.gradle/build.gradle/Application中配置。
### 2. 其次，使用{#Register.registerComponent} 让如下所有页面被注册, 在媒体选择器入口页面或者按钮使用导航方法进入MediaSelectorPage
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

### 3. 然后，在你的媒体选择器入口出导航到 `MediaSelectorPage` 页面
```typescript
// some place
const onPress() = () => {
  // react-native-awesome-navigation (u should refer to their documentation!)
  props.navigator.push('MediaSelectorPage')
  // react-navigation and others (u should refer to their documentation!)
  navigation.push...
}

return (
  <>
    ...
    <SomeView onPress={onPress} ...>...</SomeView>
    ...
  </>
)
```

### 4. 然后， 你可以在媒体选择器入口所在的页面的`useEffect hook`中监听事件: OnNextStepNotification 来获取媒体选择器的结果，(rxEventBus/OnNextStepNotification可以从`react-native-awesome-medialib`中导入)。
此时你可以按照业务需求将结果推送到一个新的页面 以展示结果或者完成其他操作。
```typescript
  // Ur entrance of medialib
  useEffect(() => {
    const subs = rxEventBus.listen(OnNextStepNotification).subscribe(value => {
      // e.g. push this value to new page do something or other operation
      props.navigator.push('Your results display page', value)
    })
    return () => {
      subs.unsubscribe()
    }
  }, [props])
```

结果的数据类型如下(Result中所有的类型都是export的，意味着都可以引用到)，包含一个媒体数据的列表(多个图片的list或者一个视频的list)，
媒体数据包含id(android 中是MediaStore查询而来的id，)，url(android 10及以上返回的是`content:\\..`格式的字符串，以下则是`storage/0/..`这样的绝对路径)，
此外还有宽、高、宽高比、类型(图片或视频)等信息。
```typescript
  // ResultModel.ts
  export interface LocalMedia {
  id?: number
  url: string
  width?: number
  height?: number
  scale?: number
  type?: SourceType
}

export enum InvokeType {
  main = 'main',
  editor = 'editor',
  avatar = 'avatar',
}

export enum SourceType {
  image = 'image',
  video = 'video',
}

export interface Result {
  dataList: LocalMedia[]
  from?: InvokeType
}
```

### 5. 最后，enjoy it.
<br/>

## 贡献者

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
