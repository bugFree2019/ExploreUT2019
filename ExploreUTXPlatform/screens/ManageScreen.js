import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, Alert} from 'react-native';
import { GoogleSignin, statusCodes, GoogleSigninButton } from '@react-native-community/google-signin';
import { Container, Content, Header, Form, Input, Item, Button, Label } from 'native-base'

import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';
import ViewPlaceScreen from './ViewPlaceScreen';

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
  }

  _configureGoogleSignIn() {
    GoogleSignin.configure(
      {
      webClientId: '1062639050908-c26utn16jdp6ab1m1hbijphee5tblh50.apps.googleusercontent.com',  //Replace with your own client id
      offlineAccess: false,
      }
    );
  }

  _signIn = async () => {
    try {
      await GoogleSignin.hasPlayServices();
      const userInfo = await GoogleSignin.signIn();
      await GoogleSignin.revokeAccess();
      console.log('Success:',userInfo);
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

  render() {
    return (
      // <View style={styles.container}>
      <Container style={styles.container}>
        <Form>
          <Item floatingLabel>
            <Label>Email</Label>
            // <Input
            //   autoCorrect={false}
            //   autoCapitalize="none"
            //   onChangeText={(email) => this.setState({email})}
            />
          </Item>

          <Item floatingLabel>
            <Label>Password</Label>
            // <Input
            //   secureTextEntry={true}
            //   autoCorrect={false}
            //   autoCapitalize="none"
            //   onChangeText={(password) => this.setState({password})}
            />
          </Item>

          <Button style={{ marginTop: 10, width: 320, height: 48 }}
            full
            rounded
            success
            // onPress={()=> this.loginUser(this.state.email, this.state.password)}
          >
            <Text style={{ color: 'white' }}>Login</Text>
          </Button>

          <Button style={{ marginTop: 10, width: 320, height: 48 }}
            full
            rounded
            primary
            // onPress={()=> this.signUpUser(this.state.email, this.state.password)}
          >
            <Text style={{ color: 'white' }}>Sign Up</Text>
          </Button>

        </Form>
       <GoogleSigninButton
          style={{ width: 320, height: 48 }}
          size={GoogleSigninButton.Size.Wide}
          color={GoogleSigninButton.Color.Dark}
          onPress={this._signIn}
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
  ViewPlace: ViewPlaceScreen,
});

export default createAppContainer(stackNavigator);