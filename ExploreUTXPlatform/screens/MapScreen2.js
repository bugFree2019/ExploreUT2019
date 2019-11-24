import React, { Component } from 'react';
import { StyleSheet, Dimensions, PermissionsAndroid, View } from 'react-native';
import { createStackNavigator } from 'react-navigation-stack';
import { createAppContainer } from 'react-navigation';
import MapView, { PROVIDER_GOOGLE } from 'react-native-maps';
import Geolocation from '@react-native-community/geolocation';
import Icon from 'react-native-vector-icons/MaterialIcons';

import { BottomNavigation, Text } from 'react-native-paper';
// import View One Place to enable the screen forward to it.
import ViewPlaceScreen from './ViewPlaceScreen';
import SubMapScreen from './SubMapScreen';


const myIconBuilding = <Icon name="building-o" />;
const myIconStudy = <Icon name="book" />;
const myIconActivity = <Icon name="local-activity" />;
const myIconStatue = <Icon name="streetview" />;

const themes = ["Buildings", "Study", "Activity", "Monument"];

const { width, height } = Dimensions.get('window');
const ASPECT_RATIO = width / height;
const LATITUDE = 30.289017;
const LONGITUDE = -97.736480;
const LATITUDE_DELTA = 0.0222;
const LONGITUDE_DELTA = LATITUDE_DELTA * ASPECT_RATIO;

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
      
      myPlaces: [],

      theme: "Buildings",
      index: 0,
        routes: [
        { key: 'building', title: 'Building', icon: "building" },
        { key: 'study', title: 'Study', icon: "library" },
        { key: 'activity', title: 'Activity', icon: "activity" },
        { key: 'statue', title: 'Statue', icon: "streetview" }
        ],
    };
    this.baseURL = 'https://explore-ut.appspot.com/';
  }

  handleIndexChange = index => {
    this.setState({ 
      index: index,
      theme: themes[index],
    });
    console.log(index);
    console.log(this.state.theme);
    this.getThemePlaces();
  };

  renderScene = BottomNavigation.SceneMap({
    building: this.BuildingRoute,
    study: this.StudyRoute,
    activity: this.ActivityRoute,
    statue: this.StatueRoute,
  });

  BuildingRoute = () => <SubMapScreen/>;

  StudyRoute = () => <SubMapScreen/>;
  
  ActivityRoute = () => <SubMapScreen/>;
  
  StatueRoute = () => <SubMapScreen/>;


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

  getThemePlaces() {
    fetch(this.baseURL + 'view_places_by_theme' + '?theme=' + this.state.theme,
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

  

  

  componentDidMount() {
    this.getPlaces();

  }

  createMarkers() {
    return this.state.myPlaces.map(place =>
      (<MapView.Marker
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
        <SubMapScreen/>
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