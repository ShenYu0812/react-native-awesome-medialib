import React from 'react'
import {createStackNavigator} from '@react-navigation/stack'
import {NavigationContainer} from '@react-navigation/native'
import MediaSelectorPage from './screen/MediaSelector'
import MediaLibPage from './screen/MediaLib'
import PicturePreviewPage from './screen/PicturePreview'

const Stack = createStackNavigator()

const App = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen component={MediaSelectorPage} name="MediaSelector" />
        <Stack.Screen component={MediaLibPage} name="MediaLib" options={{headerShown: false}} />
        <Stack.Screen
          component={PicturePreviewPage}
          name="PicturePreview"
          options={{headerShown: false}}
        />
      </Stack.Navigator>
    </NavigationContainer>
  )
}

export default App
