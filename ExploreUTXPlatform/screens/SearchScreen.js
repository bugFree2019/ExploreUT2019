import React, { Component } from 'react';
import { StyleSheet, View, ActivityIndicator } from 'react-native';
import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';
import SearchBar from 'react-native-search-bar';

import ListCardView from '../layouts/ListCardView';
import ViewPlaceScreen from './ViewPlaceScreen';

class SearchScreen extends Component {
  static navigationOptions = {
    title: 'Search',
    headerTintColor: '#fff',
    headerStyle: {
      backgroundColor: '#BF5700',
    },
  };

  constructor(props){
    super(props);
    this.state ={ isLoading: false,
                  searchTag: '' }
    this.baseURL = 'https://explore-ut.appspot.com/';
  }

  async searchPlaceAsync() {
    this.setState({isLoading: true})
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
      console.log(responseJson)
      this.setState({
        isLoading: false,
        dataSource: responseJson,
        searchTag: '',
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
        <SearchBar
          ref="searchBar"
          placeholder="Search places by tags"
          onChangeText={(text) => this.setState({searchTag: text})}
          onSearchButtonPress={() => this.searchPlaceAsync()}
          onCancelButtonPress={() => searchBar.current.blur()}
        />
        <ListCardView dataSource={this.state.dataSource} baseURL={this.baseURL} navigate={this.props.navigation} />
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
  Search: SearchScreen,
  ViewPlace: ViewPlaceScreen,
});

export default createAppContainer(stackNavigator);