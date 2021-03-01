import { PermissionsAndroid } from 'react-native';
export const requestSinglePermission = async (permission, callback) => {
    try {
        const granted = await PermissionsAndroid.request(permission);
        callback(granted);
    }
    catch (err) {
        console.error(err);
    }
};
export const requestMultiplePermission = async (permissions, callback) => {
    try {
        const shouldAuthorize = permissions.filter(p => !PermissionsAndroid.check(p));
        const resultMap = await PermissionsAndroid.requestMultiple(shouldAuthorize);
        callback(shouldAuthorize, resultMap);
    }
    catch (err) {
        console.error(err);
    }
};
