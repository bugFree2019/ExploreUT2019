import React, { Component } from 'react';
import { TouchableOpacity  } from 'react-native';
import Icon from "react-native-vector-icons/Ionicons";

export default class SearchButton extends Component {
  render() {
    return (
      <TouchableOpacity 
        style={{
          borderWidth:1,
          borderColor:'rgba(0,0,0,0.2)',
          alignItems:'center',
          justifyContent:'center',
          width:60,
          position: 'absolute',                                          
          bottom: 60,                                                    
          right: 30,
          height:60,
          backgroundColor:'#BF5700',
          borderRadius:60,
        }}
        onPress={this.props.onPress}
        >
        <Icon name="ios-search" size={30} color="#fff"/>
      </TouchableOpacity >
    );
  }
}