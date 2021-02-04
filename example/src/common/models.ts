import type {SourceType} from '../screen/MediaLib'

export interface AlbumModel {
  index: number
  cover: string
  count: number
  name: string
}

export const processAlbumModel = (source: []) =>
  source.map(
    (item: any) =>
      ({
        index: item.index,
        count: item.count,
        cover: item.cover,
        name: item.name,
      } as AlbumModel)
  )

export type ParamList = {
  MediaSelector: undefined
  MediaLib: {
    // 最大选择数量
    maxSelectedMediaCount?: number
    // 是否只展示视频
    isVideoOnly?: boolean
    // 从哪里调用
    from: SourceType
  }
  PicturePreview: {
    from: SourceType
  }
}

export type RouteName = keyof ParamList
