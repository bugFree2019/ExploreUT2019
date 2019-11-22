import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, Alert} from 'react-native';
import { GoogleSignin, statusCodes, GoogleSigninButton } from '@react-native-community/google-signin';
import { Container, Content, Header, Form, Input, Item, Button, Label } from 'native-base'

import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';
import ViewPlaceScreen from './ViewPlaceScreen';

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

  // _userExist() {
  //   if (this.state.email == '') {
  //     console.log('no user');
  //   } else {
  //     console.log('exist user');
  //   }
  // }

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

  // _decideSignIn = async () => {
  //   try {
  //     console.log('now user ', this.state.email);
  //     if (this.state.email == '') {
  //       console.log('ready to login ', this.state.email);
  //       this._signIn();
  //       console.log('after login ', this.state.email);
  //     }
  //   } catch (error) {
  //     console.log(error);
  //   }
  // }

  _signIn = async () => {
    try {
      console.log('userEmail:', this.state.email);
      const userInfo = await GoogleSignin.signIn();
      const accessToken = undefined;
      const idToken = userInfo.idToken;
      const credential = firebase.auth.GoogleAuthProvider.credential(idToken, accessToken);
      await firebase.auth().signInWithCredential(credential);
      this.setState({email: userInfo.user.email});

      const { navigate } = this.props.navigation;
      navigate('ManageUser');

      console.log('userEmail:', this.state.email);
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
        <Button style={{ alignItems: 'center', justifyContent: 'center', marginTop: 10, width: 320, height: 48 }}
          full
          rounded
          primary
          onPress={() => this.props.navigation.navigate('ManageUser')}
        >
        <Text style={{ color: 'white' }}>Jump</Text>
        </Button>
      </Container>
      

      // </View>
    );
  }

}

class ManageUsersScreen extends Component {
  render() {
    return (
      <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
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

const stackNavigator = createStackNavigator({
  Manage: ManageScreen,
  ManageUser: ManageUsersScreen,

});

const AppContainer = createAppContainer(stackNavigator);

export default class App extends Component {
  render() {
    return <AppContainer />;
  }
}