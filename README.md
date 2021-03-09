# Get Started

A useful media selector module base on native component. It will offer a meida selector in batteries-included way, including picture selection, photo shooting, video selection, album switch, preview and so on. We recommend strongly that u should install reac-native-awesome-navigation as navigation component in project when using this lib.

## Installation

```sh
npm install react-native-awesome-medialib
```
or
```sh
yarn add react-native-awesome-medialib
```

u should install react-native-awesome-navigation[github link]:(https://github.com/Project5E/react-native-awesome-navigation),<br/> 
react-native-fast-image, react-native-gesture-handler, react-native-iphone-x-helper, react-native-root-toast, @types/react-native-video and react-native-video, concurrently.<br/>

when `react-native` < 0.59.0, u should link library or maunully config.
### 1. link
```sh
react-native link react-native-awesome-media
``` 
### 2. maunully config
#### <1> include:
```kotlin
// settings.gradle
include ':react-native-awesome-medialib'
project(':react-native-awesome-medialib').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-awesome-medialib/android')
```
#### <2> dependencies:
```kotlin
// app/build.gradle
dependency {
  ...
  implementation project(':react-native-awesome-medialib')
  ...
}
```
#### <3> add ReactPackage in Application:
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
<br/>

## Usage
### 1. First, ensure `react-native-awesome-medialib`, all other library have been installed.
### 2. Second, use {#Register.registerComponent} make all page had been registed. 
  u also can use other navigation lib such as `react-navigation`, and register by it. but we recommond use `react-native-awesome-navigation`, 
  because media lib internal page's navigate is used it.

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
### 3. then, navigate to 'MediaSelectorPage' in ur media library entrance. 
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

### 4. Then, get result by listening event in ur entrance of medialib, and the event value is type of `Result` from this library.
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

The type of result: ur can get a list of choosen images or a video, whitch is include id(android media strore id,), url(above android 10 is 'content:\\\\...', else absolute path),
width, height, type, scale and so on.
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

### 5. Last, Enjoy it!
<br/>
## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
