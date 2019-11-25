import React, { Component } from 'react';
import { Keyboard } from 'react-native';
import SearchBar from 'react-native-search-bar';


export default class MySearchBar extends Component {
    constructor(props) {
        super(props);
        this.state = {searchTag: ''};
        this.keyboardDidHideListener = null;
    }
    
    _forceLoseFocus = () => {
      this.search.blur();
      this.setState({searchTag: ''});
      this.props.onCancel();
    }

    componentDidMount() {
      this.setState({searchTag: ''});
      this.search.focus();
      this.keyboardDidHideListener = Keyboard.addListener('keyboardDidHide', this._forceLoseFocus);
    }

    componentWillUnmount() {
      // remove event listener
      this.keyboardDidHideListener.remove();
    }

    render() {
      return (
        <SearchBar 
          ref={search => this.search = search}
          placeholder="Search places by tags"
          onChangeText={(text) => this.setState({searchTag: text})}
          onSearchButtonPress={() => this.props.navigation.push('Search', {searchTag: this.state.searchTag})}/>
      );
    }
  }
