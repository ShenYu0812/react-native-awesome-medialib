import React from 'react';
import { requireNativeComponent } from 'react-native';
const MediaLibraryPhotoPreviewView = requireNativeComponent('MediaLibraryPhotoPreview');
export const MediaLibraryPhotoPreview = (props) => {
    return (React.createElement(MediaLibraryPhotoPreviewView, { onFinishSelect: () => props.onFinishSelect(), onShowToast: (e) => props.onShowToast(e.nativeEvent.desc), style: props.style }));
};
