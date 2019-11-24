import React, { Component } from 'react';
import { StyleSheet, View, ActivityIndicator } from 'react-native';
import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';
import { GoogleSignin } from '@react-native-community/google-signin';
import Icon from "react-native-vector-icons/Ionicons";
import * as firebase from 'firebase';

import ListCardView from '../layouts/ListCardView';
import ViewPlaceScreen from './ViewPlaceScreen';
import CreateNewReportScreen from'./CreateNewReportScreen';

class ViewAllScreen extends Component {
  static navigationOptions = ({ navigation }) => {
    return {
      title: 'View All Places',
      headerTintColor: '#fff',
      headerStyle: {
        backgroundColor: '#BF5700',
      },
      headerLeft : <Icon name={Platform.OS === "ios" ? "ios-menu-outline" : "md-menu"}  
                         size={30} 
                         color='#fff'
                         style={{marginLeft: 10}}
                         onPress={() => navigation.openDrawer()} />,
    };
  };

  constructor(props){
    super(props);
    this.state ={isLoading: true}
    this.baseURL = 'https://explore-ut.appspot.com/';
    this.userEmail = '';
    this.focusListener=null;
  }

  componentDidMount() {
    this.focusListener = this.props.navigation.addListener("didFocus", () => this.viewAllPlaceAsync());
  }

  componentWillUnmount() {
    // remove event listener
    this.focusListener.remove();
}

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

  async viewAllPlaceAsync() {
    await this.checkUser();
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
        <ListCardView dataSource={this.state.dataSource} 
        baseURL={this.baseURL} navigate={this.props.navigation} userEmail={this.userEmail} />
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
  ViewPlace: ViewPlaceScreen,
  CreateNewReport : CreateNewReportScreen,
});

export default createAppContainer(stackNavigator);