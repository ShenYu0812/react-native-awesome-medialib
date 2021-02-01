import {requireNativeComponent, ViewStyle} from 'react-native'
import React from 'react'

const MediaLibraryPhotoPreviewView = requireNativeComponent('MediaLibraryPhotoPreview') as any

export interface Props {
  style: ViewStyle
  onFinishSelect(): void
  onShowToast(desc: string): void
}

export const MediaLibraryPhotoPreview = (props: Props) => {
  return (
    <MediaLibraryPhotoPreviewView
      onFinishSelect={() => props.onFinishSelect()}
      onShowToast={(e: any) => props.onShowToast(e.nativeEvent.desc)}
      style={props.style}
    />
  )
}
