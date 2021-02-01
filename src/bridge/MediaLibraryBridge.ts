import {NativeModules} from 'react-native'

const MediaLibrarayModule = NativeModules.MediaLibraryModule

export const libraryAuthorized: () => boolean = MediaLibrarayModule.libraryAuthorized
export const requestLibraryAuthorization: () => Promise<boolean> =
  MediaLibrarayModule.requestLibraryAuthorization
export const cameraAuthorized: () => boolean = MediaLibrarayModule.cameraAuthorized
export const requestCameraAuthorization: () => Promise<boolean> =
  MediaLibrarayModule.requestCameraAuthorization
export const startCameraPreview: () => void = MediaLibrarayModule.startCameraPreview
export const stopCameraPreview: () => void = MediaLibrarayModule.stopCameraPreview
export const fetchAllAssets: (isVideoType: boolean) => void = MediaLibrarayModule.fetchAllAssets
export const fetchAllAlbums: () => Promise<any> = MediaLibrarayModule.fetchAllAlbums
export const clear: () => void = MediaLibrarayModule.clear
export const finishSelectMedia: () => Promise<any> = MediaLibrarayModule.finishSelectMedia
export const fetchVideoURL: () => Promise<any> = MediaLibrarayModule.fetchVideoURL
export const onSelectAlbumAtIndex: (index: any) => void = MediaLibrarayModule.onSelectAlbumAtIndex

// 压缩视频
export interface NativeMediaModel {
  key: string // 表唯一
  type: string // image or video
  width: number
  height: number
  url: string
}

export const compressVideo: (url: string) => Promise<NativeMediaModel> =
  MediaLibrarayModule.compressVideo
