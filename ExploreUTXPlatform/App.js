import React, {Component} from 'react';
import {View} from 'react-native';
import { createAppContainer } from 'react-navigation';
import { createDrawerNavigator } from 'react-navigation-drawer';
import Icon from "react-native-vector-icons/Ionicons";

import ViewAllScreen from './screens/ViewAllScreen';
import ManageScreen from './screens/ManageScreen';
import MapScreen from './screens/MapScreen';
import CreateNewPlaceScreen from './screens/CreateNewPlaceScreen';

const MyDrawerNavigator = createDrawerNavigator({
  ViewAll: {
    navigationOptions: {
      drawerIcon: ({ tintColor }) => (
          <Icon name="md-photos" style={{ color: tintColor }} />
      ),
      drawerLabel: "View All Places",

    },
    screen: ViewAllScreen, 
  },
  Map: {
    navigationOptions: {
      drawerIcon: ({ tintColor }) => (
          <Icon name="md-map" style={{ color: tintColor }} />
      ),
      drawerLabel: "Map"
    },
    screen: MapScreen,
  },
  CreateNewPlace:{
    navigationOptions: {
      drawerIcon: ({ tintColor }) => (
          <Icon name="md-camera" style={{ color: tintColor }} />
      ),
      drawerLabel: "Add New Places"
    },
    screen: CreateNewPlaceScreen,
  },
  Manage: {
    navigationOptions: {
      drawerIcon: ({ tintColor }) => (
          <Icon name="md-build" style={{ color: tintColor }} />
      ),
      drawerLabel: "Manage"
    },
  	screen: ManageScreen,
  },
}, {
  contentOptions: {
    activeTintColor: '#BF5700',
    activeBackgroundColor: '#E2E2E2',
    inactiveTintColor: 'black',
    inactiveBackgroundColor: 'transparent',
    labelStyle: {
      fontSize: 15,
      marginLeft: 10,
    },
  },
  drawerPosition: 'left',
  // contentComponent: CustomDrawerNavigation,
  drawerOpenRoute: 'DrawerOpen',
  drawerCloseRoute: 'DrawerClose',
  drawerToggleRoute: 'DrawerToggle',
});

export default createAppContainer(MyDrawerNavigator);