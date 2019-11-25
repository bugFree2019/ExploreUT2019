import React, { Component } from 'react';
import { StyleSheet, View, ActivityIndicator, TouchableOpacity  } from 'react-native';
import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';
import Icon from "react-native-vector-icons/Ionicons";

import ListCardView from '../layouts/ListCardView';
import SignOutButton from '../layouts/SignOutButton';
import MySearchBar from '../layouts/MySearchBar';
import SearchButton from '../layouts/SearchButton';
import ViewPlaceScreen from './ViewPlaceScreen';
import CreateNewReportScreen from'./CreateNewReportScreen';
import SearchScreen from './SearchScreen';

class ViewAllScreen extends Component {
  static navigationOptions = ({ navigation }) => {
    return {
      title: 'View All Places',
      headerTintColor: '#fff',
      headerStyle: {
        backgroundColor: '#BF5700',
      },
      headerLeft : <Icon name={Platform.OS === "ios" ? "md-menu" : "md-menu"}  
                         size={30} 
                         color='#fff'
                         style={{marginLeft: 10}}
                         onPress={() => navigation.openDrawer()} />,
      headerRight: <SignOutButton navigation={navigation} screen="ViewAll"/>,
    };
  };

  constructor(props){
    super(props);
    this.state ={isLoading: true,
                 isSearching: false};
    this.baseURL = 'https://explore-ut.appspot.com/';
    this.focusListener=null;
  }

  componentDidMount() {
    this.focusListener = this.props.navigation.addListener("didFocus", () => this.viewAllPlaceAsync());
  }

  componentWillUnmount() {
    // remove event listener
    this.focusListener.remove();
  }

  async viewAllPlaceAsync() {
    this.setState({isLoading: true,
                   isSearching: false});
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
      // console.log(responseJson)
      this.setState({
        isLoading: false,
        dataSource: responseJson,
      });
    }
    catch (error) {
      console.error(error);
    };
  }

  render() {
    if(this.state.isLoading) {
      return(
        <View style={{flex: 1, padding: 20}}>
          <ActivityIndicator/>
        </View>
      )
    }

    return (
      <View style={styles.container}>
        { 
          this.state.isSearching &&
          <MySearchBar navigation={this.props.navigation} onCancel={() => {this.setState({isSearching: false});}}/>
        }
        <ListCardView dataSource={this.state.dataSource} 
        baseURL={this.baseURL} navigate={this.props.navigation} />
        <SearchButton onPress={() => {this.setState({isSearching: true});}} />
      </View>
    );
  }
}

var styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF'
  },
});

const stackNavigator = createStackNavigator({
  ViewAll: ViewAllScreen,
  Search: SearchScreen,
  ViewPlace: ViewPlaceScreen,
  CreateNewReport : CreateNewReportScreen,
});

export default createAppContainer(stackNavigator);