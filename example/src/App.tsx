import React from 'react'
import {createStackNavigator} from '@react-navigation/stack'
import {NavigationContainer} from '@react-navigation/native'
import MediaSelectorPage from './MediaSelector'
import MediaLibraryPage from './MediaLib'

const Stack = createStackNavigator()

const App = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="MediaSelectorPage">
        <Stack.Screen component={MediaSelectorPage} name="MediaSelector" />
        <Stack.Screen component={MediaLibraryPage} name="MediaLib" options={{headerShown: false}} />
      </Stack.Navigator>
    </NavigationContainer>
  )
}

export default App
