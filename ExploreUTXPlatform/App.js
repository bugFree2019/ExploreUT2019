import React, {Component} from 'react';
import {
  StyleSheet,
  View,
  Text,
  Image,
  FlatList,
  ActivityIndicator
} from 'react-native';

import {
  Colors,
} from 'react-native/Libraries/NewAppScreen';

import CardView from 'react-native-cardview';
import SearchBar from 'react-native-search-bar';

export default class App extends Component {
  constructor(props){
    super(props);
    this.state ={ isLoading: false,
                  searchTag: '' }
  }

  async searchPlaceAsync() {
    this.setState({isLoading: true})
    try {
      let response = await fetch(
        baseURL + 'search?tag=' + this.state.searchTag,
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

  componentDidMount() {
    // this.searchPlaceAsync('study');
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
        <FlatList
          data={this.state.dataSource}
          renderItem={({item}) =>
          <CardView style={{marginBottom: 10, flexDirection: 'row', justifyContent: 'flex-start'}}
          cardElevation={2}
          cornerRadius={5}>
              <View>
                <Image source={{uri: baseURL + "place_image/" + item['_id'] + "/0.jpg"}} 
                  style={{flex: 1,
                    width: 150,
                    height: 150,
                    resizeMode: 'contain'
                    }}/>
              </View>
              <View style={{marginStart: 10, justifyContent: 'center'}}>
                <Text style={styles.title}>{item['name']} </Text>
                <Text>Theme: {item['theme']}</Text>
                <Text>Tags: {item['tags']}</Text>
              </View>
            </CardView>}
            keyExtractor={(item, index) => item['_id']}
        />
      </View>
    );
  }
}

var baseURL = "https://explore-ut.appspot.com/";

var styles = StyleSheet.create({
  title: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF'
  },
});
