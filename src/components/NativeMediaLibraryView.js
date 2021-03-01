import React from 'react';
import { requireNativeComponent } from 'react-native';
const MediaLibraryView = requireNativeComponent('MediaLibraryView');
export const MediaLibrary = (props) => {
    return (React.createElement(MediaLibraryView, { maxSelectedMediaCount: props.maxSelectedMediaCount, onAlbumUpdate: (e) => props.onAlbumUpdate(e), onMediaItemSelect: (e) => props.onMediaItemSelect(e), onPushCameraPage: () => props.onPushCameraPage(), onPushPreviewPage: () => props.onPushPreviewPage(), onShowToast: (e) => props.onShowToast(e.nativeEvent.desc), style: props.style }));
};
