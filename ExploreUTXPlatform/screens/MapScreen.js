import React, { Component } from 'react';
import { StyleSheet, Dimensions, PermissionsAndroid, View } from 'react-native';
import { createStackNavigator } from 'react-navigation-stack';
import { createAppContainer } from 'react-navigation';
import MapView, { PROVIDER_GOOGLE } from 'react-native-maps';
import Geolocation from '@react-native-community/geolocation';
import { BottomNavigation, Text } from 'react-native-paper';
import Icon from "react-native-vector-icons/Ionicons";

// import View One Place to enable the screen forward to it.
import ViewPlaceScreen from './ViewPlaceScreen';
import SignOutButton from '../layouts/SignOutButton';


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

const AllRoute = () => <Text>all</Text>;

const BuildingRoute = () => <Text>building</Text>;

const StudyRoute = () => <Text>study</Text>;

const ActivityRoute = () => <Text>activity</Text>;

const StatueRoute = () => <Text>statue</Text>;

const themes = ["All", "Buildings", "Study", "Activity", "Monument"];


class MapScreen extends Component {
  static navigationOptions = ({ navigation }) => {
    return {
      title: 'Map',
    headerTintColor: '#fff',
    headerStyle: {
      backgroundColor: '#BF5700',
    },
    headerLeft : <Icon name={Platform.OS === "ios" ? "md-menu" : "md-menu"}  
                         size={30} 
                         color='#fff'
                         style={{marginLeft: 10}}
                         onPress={() => navigation.openDrawer()} />,
      headerRight: <SignOutButton navigation={navigation} screen="Map"/>,
    };
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
        { key: 'all', title: 'All', icon: 'map-marker' },
        { key: 'building', title: 'Building', icon: 'office-building' },
        { key: 'study', title: 'Study', icon: 'book-open-variant' },
        { key: 'activity', title: 'Activity', icon: 'ticket' },
        { key: 'statue', title: 'Statue', icon: 'google-street-view' }
        ],
    };
    this.baseURL = 'https://explore-ut.appspot.com/';
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
    this.setState({index: 0});
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
       {placeId: place.placeId, title: place.name})}
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