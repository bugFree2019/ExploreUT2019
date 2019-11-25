import React, { Component } from 'react';
import { StyleSheet, View, ActivityIndicator } from 'react-native';

import ListCardView from '../layouts/ListCardView';
import SignOutButton from '../layouts/SignOutButton';

export default class SearchScreen extends Component {
  static navigationOptions = ({ navigation }) => {
    return {
      title: 'Search Result',
      headerTintColor: '#fff',
      headerStyle: {
        backgroundColor: '#BF5700',
      },
      headerRight: <SignOutButton navigation={navigation} screen="Search"/>,
    };
  };

  constructor(props){
    super(props);
    this.state = {isLoading: true,
                  searchTag: this.props.navigation.getParam('searchTag', '') };
    this.baseURL = 'https://explore-ut.appspot.com/';
  }

  componentDidMount() {
    this.focusListener = this.props.navigation.addListener("didFocus", () => this.searchPlaceAsync());
  }

  async searchPlaceAsync() {
    this.setState({isLoading: true});
    try {
      let response = await fetch(
        this.baseURL + 'search?tag=' + this.state.searchTag,
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
        <ListCardView dataSource={this.state.dataSource} baseURL={this.baseURL} 
          navigate={this.props.navigation} />
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