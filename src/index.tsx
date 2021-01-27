import { NativeModules } from 'react-native';

type AwesomeMedialibType = {
  multiply(a: number, b: number): Promise<number>;
};

const { AwesomeMedialib } = NativeModules;

export default AwesomeMedialib as AwesomeMedialibType;
