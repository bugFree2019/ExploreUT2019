import { createAppContainer } from 'react-navigation';
import { createDrawerNavigator } from 'react-navigation-drawer';
import Icon from "react-native-vector-icons/Ionicons";
import ViewAllScreen from './screens/ViewAllScreen';
import ManageScreen from './screens/ManageScreen';
import MapScreen from './screens/MapScreen';
import CreateNewPlaceScreen from './screens/CreateNewPlaceScreen';

const MyDrawerNavigator = createDrawerNavigator({
  ViewAll: {
    screen: ViewAllScreen, 
  },
  Map: {
    screen: MapScreen,
  },
  CreateNewPlace:{
    screen: CreateNewPlaceScreen,
  },
  Manage: {
  	screen: ManageScreen,
  },
}, {
  drawerPosition: 'left',
  // contentComponent: CustomDrawerNavigation,
  drawerOpenRoute: 'DrawerOpen',
  drawerCloseRoute: 'DrawerClose',
  drawerToggleRoute: 'DrawerToggle',
});

export default createAppContainer(MyDrawerNavigator);