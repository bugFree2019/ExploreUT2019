import React, { Component } from 'react';
import { StyleSheet, Text, ScrollView, View, ActivityIndicator, TouchableHighlight, Image, FlatList } from 'react-native';
import HorizontalLine from '../layouts/HorizontalLine';
import VerticalMargin from '../layouts/VerticalMargin';
import SignOutButton from '../layouts/SignOutButton';
import { GoogleSignin } from '@react-native-community/google-signin';
import * as firebase from 'firebase';

export default class ViewPlaceScreen extends Component {
  static navigationOptions = ({ navigation }) => {
    return {
      title: navigation.getParam('title', 'View One Place'),
      headerTintColor: '#fff',
      headerStyle: {
        backgroundColor: '#BF5700',
      },
      headerRight: <SignOutButton navigation={navigation} screen="ViewPlace"/>,
    };
  };

  constructor(props){
    super(props);
    this.state ={isLoading: true}
    this.baseURL = 'https://explore-ut.appspot.com/';
    this.placeId = this.props.navigation.getParam('placeId', '5dca01e229953646f96aebda');
    this.userEmail = '';
    this.focusListener=null;
  }

  componentDidMount() {
    this.focusListener = this.props.navigation.addListener("didFocus", () => this.viewPlaceAsync());
  }

  async checkUser() {
    const isSignedIn = await GoogleSignin.isSignedIn();
    if (isSignedIn) {
      try {
        // user is signed in
        const userInfo = await GoogleSignin.signIn();
        this.userEmail = userInfo.user.email;
      }
      catch(error) {
        // user is signed in
      }
    }
    else {
        var user = await firebase.auth().currentUser;
        if (user) {
          // User is signed in.
          this.userEmail = user.email;
        } else {
          // No user is signed in.
          this.userEmail = '';
        }
    }
  }

  componentWillUnmount() {
    // remove event listener
    this.focusListener.remove();
  }

  async viewPlaceAsync() {
    await this.checkUser();
    this.setState({isLoading: true})
    try {
      let response = await fetch(
        // needs to add user email in the URL if the user already logins
        this.baseURL + 'view_one_place?place_id=' + this.placeId + '&user_email=' + this.userEmail,
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
        pictureNumbers: [...Array(responseJson['num_pics']).keys()],
      });
    }
    catch (error) {
      console.error(error);
    };
  }

  async subscribe() {
    try {
      let response = await fetch(
        // needs to add user email in the URL if the user already logins
        this.baseURL + 'subscribe/' + this.placeId + '?user_email=' + this.userEmail,
        {
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'User-Agent': 'Android'
          }
        }
      );
      let responseJson = await response.json();
      this.toggleSubscribeStatus();
    }
    catch (error) {
      console.error(error);
    };
  }

  async unsubscribe() {
    try {
      let response = await fetch(
        // needs to add user email in the URL if the user already logins
        this.baseURL + 'unsubscribe/' + this.placeId + '?user_email=' + this.userEmail,
        {
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'User-Agent': 'Android'
          }
        }
      );
      let responseJson = await response.json();
      this.toggleSubscribeStatus();
    }
    catch (error) {
      console.error(error);
    };
  }

  addReport() {
    this.props.navigation.push('CreateNewReport', {placeId: this.placeId});
  }

  toggleSubscribeStatus() {
    let dataSource = this.state.dataSource;
    if (dataSource['subscribe_status'] == 1) {
      dataSource['subscribe_status'] = 0;
      this.setState({dataSource: dataSource});
    }
    else if (dataSource['subscribe_status'] == 0) {
      dataSource['subscribe_status'] = 1;
      this.setState({dataSource: dataSource});
    }
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
      <ScrollView style={styles.container}>
        <FlatList 
          horizontal={true}
          data={this.state.pictureNumbers}
          renderItem={({item}) =>
          <View style={{flexDirection: "row", marginEnd: 10}}>
            <View>
              <Image source={{uri: this.baseURL + 'place_image/' + this.placeId + '/' + item.toString() + '.jpg'}} 
                style={{flex: 1,
                width: 200,
                height: 150,
                resizeMode: 'cover'
                }}/>
            </View>
          </View>}
          keyExtractor={(item, index) => item.toString()} 
        />
        <View>
          <SubscribeButton title="Subscribe" 
            onPress={()=>{this.subscribe();}}
            subscribe_status={this.state.dataSource['subscribe_status']} />
          <UnsubscribeButton title="Unsubscribe" 
            onPress={()=>{this.unsubscribe();}}
            subscribe_status={this.state.dataSource['subscribe_status']} />
        </View>
        <View style={{marginStart: 10, justifyContent: 'center'}}>
          <Text>Theme: {this.state.dataSource['theme']}</Text>
          <Text>Tags: {this.state.dataSource['tags']}</Text>
          <VerticalMargin />
          <Text style={{color: "#BF5700"}}>Introduction: {this.state.dataSource['intro']}</Text>
        </View>
        <VerticalMargin />
        <View><Text>Comments about this place:</Text></View>
        <FlatList
          data={this.state.dataSource['reviews']}
          renderItem={({item}) =>
          <View style={{marginTop: 10}}>
            <View>
              <Text> {item} </Text>
            </View>
            <HorizontalLine/>
          </View>
          }
          keyExtractor={(item, index) => item.toString()} 
        />
        <View>
          <AddReportButton title="Add Report"
            subscribe_status={this.state.dataSource['subscribe_status']} 
            onPress={()=>{this.addReport();}} />
        </View>
      </ScrollView>
    );
  }
}

class SubscribeButton extends Component {
  render() {
    if (this.props.subscribe_status != 0) {
      // console.log("subscribe_status is "  +this.props.subscribe_status);
      return null;
    }
    return (
      <TouchableHighlight onPress={this.props.onPress}>
        <View style={styles.button}><Text style={styles.buttonText}>{this.props.title}</Text></View>
      </TouchableHighlight>
    );
  }
}

class UnsubscribeButton extends Component {
  render() {
    if (this.props.subscribe_status != 1) {
      // console.log("subscribe_status is " + this.props.subscribe_status);
      return null;
    }
    return (
      <TouchableHighlight onPress={this.props.onPress}>
        <View style={styles.button}><Text style={styles.buttonText}>{this.props.title}</Text></View>
      </TouchableHighlight>
    );
  }
}

class AddReportButton extends Component {
  render() {
    // to update: uncomment the following statements when 'subscribe_status' works
    if (this.props.subscribe_status < 0) {
     return null;
    }
    return (
      <TouchableHighlight onPress={this.props.onPress} >
        <View style={[styles.button, {marginBottom: 50}]}><Text style={styles.buttonText}>{this.props.title}</Text></View>
      </TouchableHighlight>
    );
  }
}

var styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  button: {
    marginTop: 10,
    marginBottom: 10,
    width: 130,
    alignItems: 'center',
    backgroundColor: '#d3d3d3'
  },
  buttonText: {
    textAlign: 'center',
    padding: 10,
    color: '#BF5700',
    fontWeight: 'bold'
  },
});
