import { NativeModules } from 'react-native';
const cameraModule = NativeModules.CameraModule;
export const takePhoto = cameraModule.takePhoto;
export const switchCamera = cameraModule.switchCamera;
export const deletePhoto = cameraModule.deletePhoto;
export const cropPhotoToSquare = cameraModule.cropPhotoToSquare;
export const startRunning = cameraModule.startRunning;
export const stopRunning = cameraModule.stopRunning;
export const saveImage = cameraModule.saveImage;
