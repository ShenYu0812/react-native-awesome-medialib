import { requireNativeComponent } from 'react-native';
import React from 'react';
const CameraView = requireNativeComponent('CameraView');
export const CameraPreviewView = (props) => {
    return React.createElement(CameraView, { style: props.style });
};
