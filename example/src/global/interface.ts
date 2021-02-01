import type {NativeScrollEvent, NativeSyntheticEvent} from 'react-native'
// import type {Navigator} from 'react-native-navigation-5e/lib/typescript/navigator'

// 放置公共申明的地方
export interface BaseMaping {
  [p: string]: string | number | string[] | number[]
}

// next Params
export interface NextReqParams {
  path?: string
  params?: BaseMaping
}

export type ScrollTargetProps = NativeSyntheticEvent<NativeScrollEvent>

export interface BaseRef {
  [m: string]: (...s: any) => void
}

export interface BaseProps {
  screenID: string
  // navigator: any // Navigator
}
