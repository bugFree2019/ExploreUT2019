import React, {Component} from 'react';
import { BottomNavigation, Text } from 'react-native-paper';


// currently not under use by any screen.
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

    BuildingRoute = () => <Text>Building</Text>;

    StudyRoute = () => <Text>Study</Text>;

    ActivityRoute = () => <Text>Activity</Text>;

    StatueRoute = () => <Text>Statue</Text>;

  handleIndexChange = index => this.setState({ index });

  renderScene = BottomNavigation.SceneMap({
    building: this.BuildingRoute,
    study: this.StudyRoute,
    activity: this.ActivityRoute,
    statue: this.StatueRoute,
  });

  render() {
    return (
      <BottomNavigation
        navigationState={this.state}
        onIndexChange={this.handleIndexChange}
        renderScene={this.renderScene}
        barStyle={{backgroundColor:'#BF5700'}}
      />
    );
  }
}

export default BottomNavigator;