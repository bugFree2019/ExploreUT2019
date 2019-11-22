import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, Alert} from 'react-native';
import { GoogleSignin, statusCodes, GoogleSigninButton } from '@react-native-community/google-signin';
import { Container, Content, Header, Form, Input, Item, Button, Label } from 'native-base'

import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';

import { ToastAndroid } from "react-native";

import ViewPlaceScreen from './ViewPlaceScreen';
import ManageUserScreen from './ManageUserScreen';

import * as firebase from 'firebase';

try {
firebase.initializeApp({
apiKey: "AIzaSyD_H1xRkNuLBh4LP4RzXbZ-LuKVojIka3E",
authDomain: "explore-ut.firebaseapp.com",
databaseURL: "https://explore-ut.firebaseio.com",
storageBucket: "",
})
} catch (err) {
// we skip the "already exists" message which is
// not an actual error when we're hot-reloading
if (!/already exists/.test(err.message)) {
console.error('Firebase initialization error raised', err.stack)
}}

class ManageScreen extends Component {
  static navigationOptions = {
    title: 'Manage',
    headerTintColor: '#fff',
    headerStyle: {
      backgroundColor: '#BF5700',
    },
  };

  async componentDidMount() {
    this._configureGoogleSignIn();
    // this._userExist();
  }

  showSignInToast = () => {
  ToastAndroid.showWithGravityAndOffset(
    "Signed In",
    ToastAndroid.LONG,
    ToastAndroid.BOTTOM,
    25,
    50
  );}

  showSignUpToast = () => {
  ToastAndroid.showWithGravityAndOffset(
    "Signed In",
    ToastAndroid.LONG,
    ToastAndroid.BOTTOM,
    25,
    50
  );}

  _configureGoogleSignIn() {
    GoogleSignin.configure(
      {
      webClientId: '1062639050908-c26utn16jdp6ab1m1hbijphee5tblh50.apps.googleusercontent.com',  //Replace with your own client id
      offlineAccess: false,
      }
    );
  }

    constructor(props) {
    super(props);

    this.state = ({
      email: '',
      password: ''
    })
  }


  _signIn = async () => {
    try {
      // console.log('userEmail:', this.state.email);
      const userInfo = await GoogleSignin.signIn();
      const accessToken = undefined;
      const idToken = userInfo.idToken;
      const credential = firebase.auth.GoogleAuthProvider.credential(idToken, accessToken);

      await firebase.auth().signInWithCredential(credential);
      this.setState({email: userInfo.user.email});

      const { navigate } = this.props.navigation;
      navigate('ManageUser', {userEmail: this.state.email});

      this.showSignInToast();

      // console.log('userEmail:', this.state.email);
    } catch (error) {
      if (error.code === statusCodes.SIGN_IN_CANCELLED) {
        // sign in was cancelled
        Alert.alert('cancelled');
      } else if (error.code === statusCodes.IN_PROGRESS) {
        // operation in progress already
        Alert.alert('in progress');
      } else if (error.code === statusCodes.PLAY_SERVICES_NOT_AVAILABLE) {
        Alert.alert('play services not available or outdated');
      } else {
        console.log('Something went wrong:',error.toString());
        Alert.alert('Something went wrong', error.toString());
        this.setState({
          error,
        });
      }
    }
  };

    signUpUser = (email, password) => {
    try {
      firebase.auth().createUserWithEmailAndPassword(email, password)

      const { navigate } = this.props.navigation;
      navigate('ManageUser', {userEmail: this.state.email});

      this.showSignUpToast();
    }
    catch(error) {
      console.log(error.toString())
    }
  }

    loginUser = (email, password) => {
    try {
      firebase.auth().signInWithEmailAndPassword(email, password).then(function (user) {
        console.log(user)
      })

      const { navigate } = this.props.navigation;
      navigate('ManageUser', {userEmail: this.state.email});

      this.showSignInToast();
    }
    catch (error) {
      console.log(error,toString())
    }
  }

  render() {
    return (
      // <View style={styles.container}>
      <Container style={styles.container}>
        <Form>
          <Item floatingLabel>
            <Label>Email</Label>
             <Input
              autoCorrect={false}
              autoCapitalize="none"
              onChangeText={(email) => this.setState({email})}
            />
          </Item>

          <Item floatingLabel>
            <Label>Password</Label>
             <Input
              secureTextEntry={true}
              autoCorrect={false}
              autoCapitalize="none"
              onChangeText={(password) => this.setState({password})}
            />
          </Item>

          <Button style={{ marginTop: 10, width: 320, height: 48 }}
            full
            rounded
            success
            onPress={()=> this.loginUser(this.state.email, this.state.password)}
          >
            <Text style={{ color: 'white' }}>Login</Text>
          </Button>

          <Button style={{ marginTop: 10, width: 320, height: 48 }}
            full
            rounded
            primary
            onPress={()=> this.signUpUser(this.state.email, this.state.password)}
          >
            <Text style={{ color: 'white' }}>Sign Up</Text>
          </Button>

        </Form>
       <GoogleSigninButton
          style={{ width: 320, height: 48 }}
          size={GoogleSigninButton.Size.Wide}
          color={GoogleSigninButton.Color.Dark}
          onPress={this._signIn}
          // onPress={console.log('pressed')}
          />
      </Container>
      

      // </View>
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

const stackNavigator = createStackNavigator({
  Manage: ManageScreen,
  ManageUser: ManageUserScreen,
  ViewPlace: ViewPlaceScreen,
});

export default createAppContainer(stackNavigator);