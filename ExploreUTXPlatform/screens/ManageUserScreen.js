import React, {Component} from 'react';
import { StyleSheet, View, ActivityIndicator, Text } from 'react-native';
import Icon from "react-native-vector-icons/Ionicons";

import ListCardView from '../layouts/ListCardView';
import SignOutButton from '../layouts/SignOutButton';
import SearchButton from '../layouts/SearchButton';
import MySearchBar from '../layouts/MySearchBar';
import * as firebase from 'firebase';

import { ToastAndroid } from "react-native";


export default class ManageUserScreen extends Component {
  static navigationOptions = ({ navigation }) => {
    return {
      title: 'Manage',
      headerTintColor: '#fff',
      headerStyle: {
        backgroundColor: '#BF5700',
      },
      headerLeft : <Icon name={Platform.OS === "ios" ? "md-menu" : "md-menu"}  
                         size={30} 
                         color='#fff'
                         style={{marginLeft: 10}}
                         onPress={() => navigation.openDrawer()} />,
      headerRight: <SignOutButton navigation={navigation} screen="Manage" />,
    };
  };

  constructor(props){
    super(props);
    this.state ={
      isLoading: true,
      isSearching: false}
    this.baseURL = 'https://explore-ut.appspot.com/';
    this.userEmail = null;
    this.focusListener=null;
  }

  async checkUser() {
    if (firebase.auth().currentUser) {
      this.userEmail = firebase.auth().currentUser.email
      console.log(this.userEmail);
    }
    else {
      this.userEmail = '';
      console.log('user not logged in')
    }
  }

  showSignOutToast = () => {
  ToastAndroid.showWithGravityAndOffset(
    "Signed Out",
    ToastAndroid.LONG,
    ToastAndroid.BOTTOM,
    25,
    50
  );}

  componentDidMount() {
    this.focusListener = this.props.navigation.addListener("didFocus", () => this.manageAsync());
  }

  componentWillUnmount() {
    // remove event listener
    this.focusListener.remove();
  }

  async manageAsync() {
    this.setState({isLoading: true, isSearching: false})
    try {
      console.log(this.userEmail);
      await this.checkUser();
      if (!this.userEmail) {
        const { navigate } = this.props.navigation;
        navigate('Manage');
      }
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

