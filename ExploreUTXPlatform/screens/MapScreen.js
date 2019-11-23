import React, { Component } from 'react';
import { StyleSheet, Dimensions, PermissionsAndroid, View } from 'react-native';
import { createStackNavigator } from 'react-navigation-stack';
import { createAppContainer } from 'react-navigation';
import MapView, { PROVIDER_GOOGLE } from 'react-native-maps';
import Geolocation from '@react-native-community/geolocation';
import { createMaterialBottomTabNavigator } 
from 'react-navigation-material-bottom-tabs';

// import View One Place to enable the screen forward to it.
import ViewPlaceScreen from './ViewPlaceScreen';
import BottomNavigator from '../layouts/BottomNavigator';


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
      // this marginBottom is here to ensure the get my current location button
      // appear on the screen.
      marginBottom : 1,
      // initialize the places from our database.
      myPlaces: [],
    };
    this.baseURL = 'https://explore-ut.appspot.com/';
  }

  // get places from database and save only name, id, location, theme,
  // and restructure the location to the form Map.Marker needs.
  getPlaces() {
    fetch(this.baseURL + 'view_places',
        {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
            'User-Agent': 'Android'
          }
        }
      )
      .then(res => res.json())
      .then(parsedRes => {
        const placesArray = [];
        for (const key in parsedRes) {
          placesArray.push({
            latitude: parsedRes[key].location.lat,
            longitude: parsedRes[key].location.lng,
            placeId: parsedRes[key]._id,
            name: parsedRes[key].name,
            theme: parsedRes[key].theme,
            key: key.toString(),
          });
        }
        this.setState({ 
          myPlaces: placesArray
         });
        console.log(placesArray);
      })
      .catch(err => console.log(err));
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
    this.getPlaces();

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

  render() {
    this.placeMarkers =  this.state.myPlaces.map(place =>
       (<MapView.Marker 
        coordinate={place} 
        key={place.key} 
        placeId={place.placeId}
        title={place.name}
        // if the marker gets pressed, forward to view one place page.
        onPress={() => this.props.navigation.push('ViewPlace', 
        {placeId: place.placeId, title: place.name})}
        />));

    return (
      <View style={{
        flex: 1,
        //flexDirection: 'column',
        paddingTop: 550,
      }}>
      <MapView
        // ref={map => {
        //   this.map = map;
        // }}
        style={ {...styles.map, marginBottom: this.state.marginBottom} }
        provider={ PROVIDER_GOOGLE }
        onMapReady={ this.onMapReady }
        showsUserLocation={ true }
        showsMyLocationButton={ true }
        rotateEnabled={ true }
        region={ this.state.region }
        // onRegionChange={ region => this.setState({region}) }
        // onRegionChangeComplete={ region => this.setState({region}) }
        onRegionChangeComplete={ this.onRegionChangeComplete }
      >
        { this.placeMarkers } 
      </MapView>
      <BottomNavigator/>
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

const stackNavigator = createStackNavigator({
  Map: MapScreen,
  ViewPlace: ViewPlaceScreen,
});

export default createAppContainer(stackNavigator);  