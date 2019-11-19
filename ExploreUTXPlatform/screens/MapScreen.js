import React, { Component } from 'react';
import { StyleSheet, Dimensions, PermissionsAndroid } from 'react-native';

import { createStackNavigator } from 'react-navigation-stack';
import { createAppContainer } from 'react-navigation';
import MapView, { PROVIDER_GOOGLE } from 'react-native-maps';
import Geolocation from '@react-native-community/geolocation';

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
  };

  constructor() {
    super();
    this.state = {
      region: {
        latitude: LATITUDE,
        longitude: LONGITUDE,
        latitudeDelta: LATITUDE_DELTA,
        longitudeDelta: LONGITUDE_DELTA,
      }
    };
  }

  watchLocation() {
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
    // { enableHighAccuracy: true, timeout: 20000, maximumAge: 1000 },
    {GEOLOCATION_OPTIONS},
    );

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
    // this.mounted = true;
    if (Platform.OS === 'android') {
      PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION
      ).then(granted => {
        if (granted) {
          this.watchLocation();
        }
      });
    } else {
      this.watchLocation();
    }
  }
  componentWillUnmount() {
    // this.mounted = false;
    Geolocation.clearWatch(this.watchID);
  }

  getInitialState() {
    return {
      region: {
        latitude: 30.289017,
        longitude: -97.736480,
        latitudeDelta: 0.0222,
        longitudeDelta: 0.0111,
      },
    };
  }

  // onRegionChange(region) {
  //   this.setState({ region });
  // }

  // onRegionChangeComplete(region) {
  //   this.setState({ region });
  // }

  render() {
    return (
      <MapView

        provider={ PROVIDER_GOOGLE }
        style={ styles.container }
        showsUserLocation={ true }
        rotateEnabled={ true }
        region={ this.state.region }
        onRegionChange={ region => this.setState({region}) }
        // onRegionChangeComplete={ region => this.setState({region}) }
        // onRegionChange={this.onRegionChange}
        // onRegionChangeComplete={this.onRegionChangeComplete}
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
    height: '100%',
    width: '100%',
    justifyContent: 'flex-end',
    alignItems: 'center',
  },
  map: {
    ...StyleSheet.absoluteFill,
  },
});

const stackNavigator = createStackNavigator({
  Map: MapScreen,
  ViewPlace: ViewPlaceScreen,
});

export default createAppContainer(stackNavigator);