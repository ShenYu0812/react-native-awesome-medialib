import type {ParamListBase, RouteProp} from '@react-navigation/native'
import type {StackNavigationProp} from '@react-navigation/stack'

export interface BaseProps<T extends ParamListBase, R extends keyof T> {
  navigation: StackNavigationProp<T>
  route: RouteProp<T, R>
}
