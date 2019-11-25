import React, { Component } from 'react';
import { StyleSheet, Text, View, TouchableWithoutFeedback } from 'react-native';
import { ToastAndroid } from "react-native";
import { GoogleSignin } from '@react-native-community/google-signin';
import * as firebase from 'firebase';

export default class SignOutButton extends Component {

  constructor(props){
    super(props);
    this.focusListener=null;
    this.state = {user: null};
  }

  componentDidMount() {
    this.focusListener = this.props.navigation.addListener("didFocus", () => this.getUser());
  }

  getUser() {
    this.setState({user: firebase.auth().currentUser});
  }

  showSignOutToast = () => {
    ToastAndroid.showWithGravityAndOffset(
      "Signed Out",
      ToastAndroid.LONG,
      ToastAndroid.BOTTOM,
      25,
      50
    );}

  async signOutUser() {
    try {
      firebase.auth().signOut();
      const isSignedIn = await GoogleSignin.isSignedIn();
      if (isSignedIn) {
        await GoogleSignin.revokeAccess();
        await GoogleSignin.signOut();
      }
      this.setState({user: null});
      this.showSignOutToast();
      if (this.props.screen === 'Manage') {
        this.props.navigation.navigate(this.props.title);
      }
      else if (this.props.screen === 'ViewPlace') {
        let placeId = this.props.navigation.getParam('placeId', '5dca01e229953646f96aebda');
        let title = this.props.navigation.getParam('title', 'View One Place');
        this.props.navigation.replace('ViewPlace', {placeId: placeId, title: title});
      }
    }
    catch (error) {
      console.log(error,toString())
    }
  }

  render() {
    if (!this.state.user) {
      // console.log("user not logged in, hide sign out button");
      return null;
    }
    return (
      <TouchableWithoutFeedback onPress={()=>{this.signOutUser();}}>
        <View style={styles.button}><Text style={styles.buttonText}>Sign Out</Text></View>
      </TouchableWithoutFeedback>
    );
  }
}

  var styles = StyleSheet.create({
    button: {
      marginTop: 10,
      marginBottom: 10,
      width: 130,
      alignItems: 'center',
      backgroundColor: '#BF5700'
    },
    buttonText: {
      textAlign: 'center',
      padding: 10,
      color: '#fff',
      fontWeight: 'bold'
    },
  });