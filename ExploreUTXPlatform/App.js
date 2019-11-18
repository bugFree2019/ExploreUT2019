import React, {Component} from 'react';
import {StyleSheet, View, Text} from 'react-native';

import { createAppContainer } from 'react-navigation';
import { createDrawerNavigator } from 'react-navigation-drawer';
import SearchScreen from './screens/SearchScreen';

var styles = StyleSheet.create({
  icon: {
    width: 24,
    height: 24,
  },
});

class MyHomeScreen extends Component {
  static navigationOptions = {
    title: 'Home',
    // drawerLabel: 'Home'
    // drawerIcon: ({ tintColor }) => (
    //   <Image
    //     source={require('./chats-icon.png')}
    //     style={[styles.icon, { tintColor: tintColor }]}
    //   />
    // ),
  };

  render() {
    return (
      <View><Text>This is the home page</Text></View>
    );
  }
}

const MyDrawerNavigator = createDrawerNavigator({
  Home: {
    screen: MyHomeScreen,
  },
  Search: {
    screen: SearchScreen,
  },
});

export default createAppContainer(MyDrawerNavigator);