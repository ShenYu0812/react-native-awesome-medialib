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
