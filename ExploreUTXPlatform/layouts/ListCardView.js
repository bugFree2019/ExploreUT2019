import React, { Component } from 'react';
import { StyleSheet, View, Text, Image, FlatList, TouchableHighlight  } from 'react-native';

import { Colors } from 'react-native/Libraries/NewAppScreen';

import CardView from 'react-native-cardview';

export default class ListCardView extends Component {
  render() {
    return (
      <FlatList
        data={this.props.dataSource}
        renderItem={({item}) =>
        <TouchableHighlight  onPress={() => this.props.navigate.push('ViewPlace', {placeId: item['_id'], title: item['name']})}>
        <CardView style={{marginBottom: 10, flexDirection: 'row', justifyContent: 'flex-start'}}
          cardElevation={2}
          cornerRadius={5}>
          <View>
            <Image source={{uri: this.props.baseURL + "place_image/" + item['_id'] + "/0.jpg"}} 
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
        </CardView>
        </TouchableHighlight >}
        keyExtractor={(item, index) => item['_id']}
      />
    );
  }
}

var styles = StyleSheet.create({
  title: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
});