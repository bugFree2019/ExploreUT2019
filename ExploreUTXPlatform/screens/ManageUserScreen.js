import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, Alert} from 'react-native';
import { GoogleSignin, statusCodes, GoogleSigninButton } from '@react-native-community/google-signin';
import { Container, Content, Header, Form, Input, Item, Button, Label } from 'native-base'

import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';

import ManageScreen from './ManageScreen';


export default class ManageUserScreen extends Component {
  render() {
    const { navigation } = this.props;
    const userEmail = navigation.getParam('userEmail');
    return (
      <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
        <Text>userEmail: {JSON.stringify(userEmail)}</Text>
        <Text>Details Screen</Text>
      </View>
    );
  }
}

type Props = {};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
    padding: 10,
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