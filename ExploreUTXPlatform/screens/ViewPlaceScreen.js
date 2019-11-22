import React, { Component } from 'react';
import { StyleSheet, Text, View, ActivityIndicator, Button, Image, FlatList } from 'react-native';
import HorizontalLine from '../layouts/HorizontalLine';
import VerticalMargin from '../layouts/VerticalMargin';

export default class ViewPlaceScreen extends Component {
  static navigationOptions = ({ navigation }) => {
    return {
      title: navigation.getParam('title', 'View One Place'),
      headerTintColor: '#fff',
      headerStyle: {
      backgroundColor: '#BF5700',
    },
    };
  };

  constructor(props){
    super(props);
    this.state ={isLoading: true}
    this.baseURL = 'https://explore-ut.appspot.com/';
    this.placeId = this.props.navigation.getParam('placeId', '5dca01e229953646f96aebda');
    console.log(this.placeId);
  }

  componentDidMount() {
    this.viewPlaceAsync();
  }

  async viewPlaceAsync() {
    this.setState({isLoading: true})
    try {
      let response = await fetch(
        // needs to add user email in the URL if the user already logins
        this.baseURL + 'view_one_place?place_id=' + this.placeId,
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
      console.log(responseJson)
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
      console.log(responseJson)
      this.toggleSubscribeStatus();
    }
    catch (error) {
      console.error(error);
    };
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
      <View style={styles.container}>
        <FlatList 
        horizontal={true}
          data={this.state.pictureNumbers}
          renderItem={({item}) =>
          <View style={{flexDirection: "row", marginEnd: 10}}>
            <View>
              <Image source={{uri: this.baseURL + 'place_image/' + this.placeId + '/' + item.toString() + '.jpg'}} 
                style={{flex: 1,
                width: 200,
                height: 200,
                resizeMode: 'contain'
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
          <Text>Introduction: {this.state.dataSource['intro']}</Text>
        </View>
        <VerticalMargin />
        <View><Text>Comments about this place:</Text></View>
        <FlatList
          data={this.state.dataSource['reviews']}
          renderItem={({item}) =>
          <View>
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
            subscribe_status={this.state.dataSource['subscribe_status']}  />
        </View>
      </View>
    );
  }
}

class SubscribeButton extends Component {
  render() {
    if (this.props.subscribe_status != 0) {
      return null;
    }
    return (
      <Button title={this.props.title}
      onPress={this.props.onPress} />
    );
  }
}

class UnsubscribeButton extends Component {
  render() {
    if (this.props.subscribe_status != 1) {
      return null;
    }
    return (
      <Button title={this.props.title}
      onPress={this.props.onPress} />
    );
  }
}

class AddReportButton extends Component {
  render() {
    if (this.props.subscribe_status < 0) {
      return null;
    }
    return (
      <Button title={this.props.title}
      onPress={this.props.onPress} />
    );
  }
}

var styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF',
  },
});
