import React, {Component} from 'react';
import { BottomNavigation, Text } from 'react-native-paper';
import {Icon} from 'react-native-vector-icons/FontAwesome5';


const BuildingRoute = () => <Text >Building</Text>;

const StudyRoute = () => <Text>Study</Text>;

const ActivityRoute = () => <Text>Activity</Text>;

const StatueRoute = () => <Text>Statue</Text>;

class BottomNavigator extends Component {

  constructor() {
    super();
    this.state = {
    index: 0,
    routes: [
      { key: 'building', title: 'Building', icon: 'building' },
      { key: 'study', title: 'Study', icon: 'library' },
      { key: 'activity', title: 'Activity', icon: 'activity' },
      { key: 'statue', title: 'Statue', icon: 'street_view' },
    ],
  };
  }
  

  handleIndexChange = index => this.setState({ index });

  renderScene = BottomNavigation.SceneMap({
    building: BuildingRoute,
    study: StudyRoute,
    activity: ActivityRoute,
    statue: StatueRoute,
  });

  render() {
    return (
      <BottomNavigation
        navigationState={this.state}
        onIndexChange={this.handleIndexChange}
        renderScene={this.renderScene}
      />
    );
  }
}

export default BottomNavigator;