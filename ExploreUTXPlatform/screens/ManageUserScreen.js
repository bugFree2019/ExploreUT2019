import React, {Component} from 'react';
import { StyleSheet, View, ActivityIndicator, Text } from 'react-native';
import { GoogleSignin, statusCodes, GoogleSigninButton } from '@react-native-community/google-signin';
import { Container, Content, Header, Form, Input, Item, Button, Label } from 'native-base'

import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';

import ListCardView from '../layouts/ListCardView';
import ManageScreen from './ManageScreen';
import * as firebase from 'firebase';

import { ToastAndroid } from "react-native";


export default class ManageUserScreen extends Component {

  static navigationOptions = {
    title: 'Manage',
    headerTintColor: '#fff',
    headerStyle: {
      backgroundColor: '#BF5700',
    },
  };

  constructor(props){
    super(props);
    this.state ={
      isLoading: true,
      }
    this.baseURL = 'https://explore-ut.appspot.com/';
    this.userEmail = this.props.navigation.getParam('userEmail', 'changpengtong');
    this.focusListener=null;
  }

  showSignOutToast = () => {
  ToastAndroid.showWithGravityAndOffset(
    "Signed Out",
    ToastAndroid.LONG,
    ToastAndroid.BOTTOM,
    25,
    50
  );}

  signOutUser = () => {
      try {
        firebase.auth().signOut();
        const { navigate } = this.props.navigation;
        navigate('Manage');
        this.showSignOutToast();
      }
      catch (error) {
        console.log(error,toString())
      }
  }

  signOut = async () => {
  try {
    await GoogleSignin.revokeAccess();
    await GoogleSignin.signOut();
    // this.setState({ user: null }); // Remember to remove the user from your app's state as well
    const { navigate } = this.props.navigation;
    navigate('Manage');
  } catch (error) {
    console.error(error);
  }
};

  componentDidMount() {
    this.focusListener = this.props.navigation.addListener("didFocus", () => this.manageAsync());
  }

  componentWillUnmount() {
    // remove event listener
    this.focusListener.remove();
  }

  async manageAsync() {
    this.setState({isLoading: true})
    try {
      console.log(this.userEmail);
      const data = {};
      data.email = this.userEmail;
      let response = await fetch(
        this.baseURL + 'index',
        {
          method: 'POST',
          headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json',
            
            'User-Agent': 'Android'
          },
          body: JSON.stringify(data)
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
          <Button style={{ marginTop: 10, width: 100, height: 48 }}
            full
            rounded
            success
            onPress={()=> this.signOutUser()}
          >
            <Text style={{ color: 'white' }}>Sign Out</Text>
          </Button>
        <ListCardView dataSource={this.state.dataSource} 
        baseURL={this.baseURL} navigate={this.props.navigation} />

      </View>
    );
  }
}

type Props = {};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF'
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

