import React, { Component } from 'react';
import { StyleSheet, Dimensions, PermissionsAndroid } from 'react-native';

import { createStackNavigator } from 'react-navigation-stack';
import { createAppContainer } from 'react-navigation';
import MapView, { PROVIDER_GOOGLE } from 'react-native-maps';
import Geolocation from '@react-native-community/geolocation';
import { createMaterialBottomTabNavigator } from 'react-navigation-material-bottom-tabs';

import ViewPlaceScreen from './ViewPlaceScreen';


const { width, height } = Dimensions.get('window');
const ASPECT_RATIO = width / height;
const LATITUDE = 30.289017;
const LONGITUDE = -97.736480;
const LATITUDE_DELTA = 0.0222;
const LONGITUDE_DELTA = LATITUDE_DELTA * ASPECT_RATIO;

const GEOLOCATION_OPTIONS = {
  enableHighAccuracy: true,
  timeout: 20000,
  maximumAge: 1000,
};

class MapScreen extends Component {
  static navigationOptions = {
    title: 'Map',
    headerTintColor: '#fff',
    headerStyle: {
      backgroundColor: '#BF5700',
    },
  };

  constructor() {
    super();
    this.state = {
      region: {
        latitude: LATITUDE,
        longitude: LONGITUDE,
        latitudeDelta: LATITUDE_DELTA,
        longitudeDelta: LONGITUDE_DELTA,
      },
      marginBottom : 1,
      isLoading: true,
    };
    this.baseURL = 'https://explore-ut.appspot.com/';
  }

  async viewAllPlaceAsync() {
    this.setState({isLoading: true})
    try {
      let response = await fetch(
        this.baseURL + 'view_places',
        {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
            'User-Agent': 'Android'
          }
        }
      );
      let responseJson = await response.json();
      console.log(responseJson)
      this.setState({
        isLoading: false,
        dataSource: responseJson,
      });
    }
    catch (error) {
      console.error(error);
    };
  }

  GetLocation() {
    Geolocation.getCurrentPosition(
      position => {
        this.setState({
          region: {
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
            latitudeDelta: LATITUDE_DELTA,
            longitudeDelta: LONGITUDE_DELTA,
          }
        });
      },
    (error) => console.log(error.message),
    {GEOLOCATION_OPTIONS},
    );
  }

  WatchLocation() {
    this.watchID = Geolocation.watchPosition(
      position => {
        this.setState({
          region: {
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
            latitudeDelta: LATITUDE_DELTA,
            longitudeDelta: LONGITUDE_DELTA,
          }
        });
      }
    );
  }

  componentDidMount() {
    this.viewAllPlaceAsync();

    if (Platform.OS === 'android') {
      PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION
      ).then(granted => {
        if (granted) {
          this.GetLocation();
          this.WatchLocation();
        }
      });
    } else {
      this.GetLocation();
      this.WatchLocation();
    }
  }
  componentWillUnmount() {
    Geolocation.clearWatch(this.watchID);
  }

  // onRegionChange = region => {
  //   this.setState({ region });
  // }

  onRegionChangeComplete = region => {
    this.setState({ region });
  }

  onMapReady = () => {
    this.setState({marginBottom: 0})
  }


  render() {
    return (
      <MapView
        ref={map => {
          this.map = map;
        }}
        provider={ PROVIDER_GOOGLE }
        style={ {...styles.map, marginBottom: this.state.marginBottom} }
        // style={ {...StyleSheet.absoluteFill, marginBottom: this.state.marginBottom} }
        onMapReady={this.onMapReady}
        showsUserLocation={ true }
        showsMyLocationButton={ true }
        rotateEnabled={ true }
        region={ this.state.region }
        // onRegionChange={ region => this.setState({region}) }
        // onRegionChangeComplete={ region => this.setState({region}) }
        onRegionChangeComplete={ this.onRegionChangeComplete }
      >
        {/* <MapView.Marker
          coordinate={ this.state.region }
        /> */}
      </MapView>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    ...StyleSheet.absoluteFill,
  },
  map: {
    ...StyleSheet.absoluteFill,
  },
});

const stackNavigator = createStackNavigator({
  Map: MapScreen,
  ViewPlace: ViewPlaceScreen,
});

// const buttomTabNavigator = createMaterialBottomTabNavigator({
//   Album: { screen: Map },
//   // Library: { screen: Library },
//   // History: { screen: History },
//   // Cart: { screen: Cart },
// }, 
// {
//   initialRouteName: 'Album',
//   activeColor: '#F44336',
// });

export default createAppContainer(stackNavigator);  