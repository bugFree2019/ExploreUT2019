import React, { Component } from 'react';
import { StyleSheet, Dimensions, PermissionsAndroid, View } from 'react-native';
import { createStackNavigator } from 'react-navigation-stack';
import { createAppContainer } from 'react-navigation';
import MapView, { PROVIDER_GOOGLE } from 'react-native-maps';
import Geolocation from '@react-native-community/geolocation';

// import View One Place to enable the screen forward to it.
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

class SubMapScreen extends Component {
  
    constructor() {
      super();
      this.state = {
        region: {
          latitude: LATITUDE,
          longitude: LONGITUDE,
          latitudeDelta: LATITUDE_DELTA,
          longitudeDelta: LONGITUDE_DELTA,
        },
        // this marginBottom is here to ensure the get my current location button
        // appear on the screen.
        marginBottom : 1,
        // initialize the places from our database.
        myPlaces: [],
  
        theme: "Buildings",
      };
      this.baseURL = 'https://explore-ut.appspot.com/';
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
      {GEOLOCATION_OPTIONS}
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
      if (this.watchID) {
        Geolocation.clearWatch(this.watchID);
      }
    }
  
    // onRegionChange = region => {
    //   this.setState({ region });
    // }
  
    onRegionChangeComplete = region => {
      this.setState({ region });
    }
  
    onMapReady = () => {
      this.setState({marginBottom: 0});
    }
  
    createMarkers() {
      return this.state.myPlaces.map(place =>
        (<MapView.Marker.Animated
         coordinate={place} 
         key={place.key} 
         placeId={place.placeId}
         title={place.name}
         // if the marker gets pressed, forward to view one place page.
         onPress={() => this.props.navigation.push('ViewPlace', 
         {placeId: place.placeId, title: place.name})}
         />));
    }
  
    render() {  
      return (
        <View style={styles.container}>
        <MapView
          ref={map => {
            this.map = map;
          }}
          style={ {...styles.map, marginBottom: this.state.marginBottom } }
          provider={ PROVIDER_GOOGLE }
          onMapReady={ this.onMapReady }
          showsUserLocation={ true }
          showsMyLocationButton={ true }
          rotateEnabled={ true }
          initialRegion={{
            latitude: 30.2852,
            longitude: -97.7340,
            latitudeDelta: LATITUDE_DELTA,
            longitudeDelta: LONGITUDE_DELTA,
          }}
          // region={ this.state.region }
        //   mapPadding={{
        //     top: 0,
        //     right: 0,
        //     bottom: 50,
        //     left: 0
        //   }}
          // onRegionChange={ region => this.setState({region}) }
          // onRegionChangeComplete={ region => this.setState({region}) }
          onRegionChangeComplete={ this.onRegionChangeComplete }
        >
          { this.createMarkers() } 
        </MapView>
    </View>
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
  
  export default SubMapScreen; 