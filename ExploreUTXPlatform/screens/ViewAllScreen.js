import React, { Component } from 'react';
import { StyleSheet, View, ActivityIndicator } from 'react-native';

import ListCardView from '../layouts/ListCardView';

export default class ViewAllScreen extends Component {
  static navigationOptions = {
    title: 'View All',
  };

  constructor(props){
    super(props);
    this.state ={ isLoading: true}
    this.baseURL = "https://explore-ut.appspot.com/";
  }

  componentDidMount() {
    this.viewAllPlaceAsync();
  }

  async viewAllPlaceAsync() {
    this.setState({isLoading: true})
    try {
      let response = await fetch(
        this.baseURL + '/view_places',
        {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
            'User-Agent': 'Android'
          }
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
        <ListCardView dataSource={this.state.dataSource} baseURL={this.baseURL} />
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
