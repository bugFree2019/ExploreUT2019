import React, { Component } from 'react';
import { StyleSheet, Dimensions, PermissionsAndroid, View } from 'react-native';
import { createStackNavigator } from 'react-navigation-stack';
import { createAppContainer } from 'react-navigation';
import MapView, { PROVIDER_GOOGLE } from 'react-native-maps';
import Geolocation from '@react-native-community/geolocation';
import { GoogleSignin } from '@react-native-community/google-signin';
import * as firebase from 'firebase';
import { BottomNavigation, Text } from 'react-native-paper';
// import Icon from 'react-native-vector-icons/FontAwesome5';
import FontAwesome5 from 'react-native-vector-icons/FontAwesome5';

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

const myIconAllPlaces = <Icon name={'place-of-worship'} size={12} color="#FFF" />;
const myIconBuilding = <Icon name={'building'} />;
const myIconStudy = <Icon name={'book-open'} />;
const myIconActivity = <Icon name={'local-activity'} />;
const myIconStatue = <Icon name={'monument'} />;

const AllRoute = () => <Text>all</Text>;

const BuildingRoute = () => <Text>building</Text>;

const StudyRoute = () => <Text>study</Text>;

const ActivityRoute = () => <Text>activity</Text>;

const StatueRoute = () => <Text>statue</Text>;

const themes = ["All", "Buildings", "Study", "Activity", "Monument"];


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
    this.focusListener=null;
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

      // theme: "All",

      index: 0,
        routes: [
        { key: 'all', title: 'All', icon: 'places' },
        { key: 'building', title: 'Building', icon: 'buildings' },
        { key: 'study', title: 'Study', icon: 'study' },
        { key: 'activity', title: 'Activity', icon: 'activity' },
        { key: 'statue', title: 'Statue', icon: 'statue' }
        ],
    };
    this.baseURL = 'https://explore-ut.appspot.com/';
    this.userEmail = '';
  }

  handleIndexChange = index => {
    this.setState({ 
      index: index,
    });
    this.updateMap(index);
  };

  async updateMap (index) {
    if (index === 0) {
      await this.getPlaces();
    } else {
      await this.getThemePlaces(themes[index]);
    }
  }

  renderScene = BottomNavigation.SceneMap({
    all: AllRoute,
    building: BuildingRoute,
    study: StudyRoute,
    activity: ActivityRoute,
    statue: StatueRoute,
  });

  async checkUser() {
    const isSignedIn = await GoogleSignin.isSignedIn();
    if (isSignedIn) {
      try {
        const userInfo = await GoogleSignin.signIn();
        this.userEmail = userInfo.user.email;
        console.log(this.userEmail);
      }
      catch(error) {
        console.log('user not logged in')
      }
    }
    else {
      var user = await firebase.auth().currentUser;
      if (user) {
        // User is signed in.
        this.userEmail = user.email;
        console.log(user.email);
      } else {
        // No user is signed in.
        this.userEmail = '';
        console.log('user not logged in')
      }
  }
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
        this.checkUser();
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

  getThemePlaces(theme) {
    fetch(this.baseURL + 'view_places_by_theme' + '?theme=' + theme,
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

  async GetLocation() {
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
    this.focusListener = this.props.navigation.addListener("didFocus", () => this.initializeState());
  }

  componentWillUnmount() {
    if (this.watchID) {
      Geolocation.clearWatch(this.watchID);
    }
    this.focusListener.remove();
  }
  
  async initializeState() {
    console.log("initialize");
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
    await this.getPlaces();
  }

  onRegionChangeComplete = region => {
    this.setState({ region });
  }

  onMapReady = () => {
    this.setState({marginBottom: 0});
  }

  createMarkers() {
    return this.state.myPlaces.map(place =>
      (<MapView.Marker
       coordinate={place} 
       key={place.key} 
       placeId={place.placeId}
       // title={place.name}
       // if the marker gets pressed, forward to view one place page.
       onPress={() => this.props.navigation.push('ViewPlace', 
       {placeId: place.placeId, title: place.name, userEmail: this.userEmail})}
       />));
  }

  render() { 
    return (
      <View style={{
        flex: 1,
        //flexDirection: 'column',
        paddingTop: 550,
      }}>
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
        mapPadding={{
          top: 0,
          right: 0,
          bottom: 50,
          left: 0
        }}
        // onRegionChange={ region => this.setState({region}) }
        // onRegionChangeComplete={ region => this.setState({region}) }
        onRegionChangeComplete={ this.onRegionChangeComplete }
      >
      {this.createMarkers()}
      </MapView>
      <BottomNavigation
        navigationState={this.state}
        onIndexChange={this.handleIndexChange}
        renderScene={this.renderScene}
        barStyle={{backgroundColor:'#BF5700'}}
      />
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