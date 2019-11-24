import { createAppContainer } from 'react-navigation';
import { createDrawerNavigator } from 'react-navigation-drawer';
import Icon from "react-native-vector-icons/Ionicons";
import ViewAllScreen from './screens/ViewAllScreen';
import SearchScreen from './screens/SearchScreen';
import ManageScreen from './screens/ManageScreen';
import MapScreen from './screens/MapScreen';
import CreateNewPlaceScreen from './screens/CreateNewPlaceScreen';

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
  CreateNewPlace:{
    screen: CreateNewPlaceScreen,
  }

}, {
  drawerPosition: 'left',
  // contentComponent: CustomDrawerNavigation,
  drawerOpenRoute: 'DrawerOpen',
  drawerCloseRoute: 'DrawerClose',
  drawerToggleRoute: 'DrawerToggle',
});

export default createAppContainer(MyDrawerNavigator);