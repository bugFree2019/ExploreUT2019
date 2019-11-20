import { createAppContainer } from 'react-navigation';
import { createDrawerNavigator } from 'react-navigation-drawer';
import ViewAllScreen from './screens/ViewAllScreen';
import SearchScreen from './screens/SearchScreen';
import ManageScreen from './screens/ManageScreen';
import MapScreen from './screens/MapScreen';

// class MyHomeScreen extends Component {
//   static navigationOptions = {
//     title: 'Home',
//     drawerLabel: 'Home'
//     drawerIcon: ({ tintColor }) => (
//       <Image
//         source={require('./chats-icon.png')}
//         style={[styles.icon, { tintColor: tintColor }]}
//       />
//     ),
//   };

//   render() {
//     return (
//       <View><Text>This is the home page</Text></View>
//     );
//   }
// }

const MyDrawerNavigator = createDrawerNavigator({
  ViewAll: {
    screen: ViewAllScreen,
  },
  Map: {
    screen: MapScreen,
  },
  Search: {
    screen: SearchScreen,
  },
  Manage: {
  	screen: ManageScreen,
  },

});

export default createAppContainer(MyDrawerNavigator);