import React, { Component } from 'react';
import { StyleSheet, View, ActivityIndicator } from 'react-native';
import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';

import ListCardView from '../layouts/ListCardView';
import ViewPlaceScreen from './ViewPlaceScreen';
import CreateNewReportScreen from'./CreateNewReportScreen';

class ViewAllScreen extends Component {
  static navigationOptions = {
    title: 'View All Places',
    headerTintColor: '#fff',
    headerStyle: {
      backgroundColor: '#BF5700',
    },
  };

  constructor(props){
    super(props);
    this.state ={isLoading: true}
    this.baseURL = 'https://explore-ut.appspot.com/';
    this.focusListener=null;
  }

  componentDidMount() {
    this.focusListener = this.props.navigation.addListener("didFocus", () => this.viewAllPlaceAsync());
  }

  componentWillUnmount() {
    // remove event listener
    this.focusListener.remove();
}

  async viewAllPlaceAsync() {
    this.setState({isLoading: true})
    try {
      let response = await fetch(
        this.baseURL + 'view_places',
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
        <ListCardView dataSource={this.state.dataSource} 
        baseURL={this.baseURL} navigate={this.props.navigation} />
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

const stackNavigator = createStackNavigator({
  ViewAll: ViewAllScreen,
  ViewPlace: ViewPlaceScreen,
  CreateNewReport : CreateNewReportScreen,
});

export default createAppContainer(stackNavigator);