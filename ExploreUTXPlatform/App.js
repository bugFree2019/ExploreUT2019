import React, {Component} from 'react';
import { SafeAreaView, View, ScrollView, Text, Image } from 'react-native';
import { createAppContainer } from 'react-navigation';
import { createDrawerNavigator, DrawerItems } from 'react-navigation-drawer';
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
  drawerOpenRoute: 'DrawerOpen',
  drawerCloseRoute: 'DrawerClose',
  drawerToggleRoute: 'DrawerToggle',
  contentComponent: (props) => (
    <SafeAreaView style={{flex: 1}}>
        <View style={{height: 100, alignItems: 'flex-start', justifyContent: 'flex-start', backgroundColor: '#BF5700'}}>
          <Text style={{color: '#fff', marginLeft: 20}}>Explore UT</Text>
          <View style={{alignItems: 'flex-start'}}>
            <Image source={require("./android/app/src/main/res/mipmap-xhdpi/ic_launcher_round.png")} style={{ marginTop: 10, height: 50, resizeMode: 'contain'}} />
          </View>
          <Text style={{color: '#ddd', marginLeft: 20}}>android.studio@android.com</Text>
        </View>
      <ScrollView>
        <DrawerItems {...props} />
      </ScrollView>
    </SafeAreaView>
   )
});

export default createAppContainer(MyDrawerNavigator);