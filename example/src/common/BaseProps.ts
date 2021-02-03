import type {ParamListBase} from '@react-navigation/native'
import type {StackNavigationProp} from '@react-navigation/stack'

export interface BaseProps<T extends ParamListBase> {
  navigation: StackNavigationProp<T>
}
