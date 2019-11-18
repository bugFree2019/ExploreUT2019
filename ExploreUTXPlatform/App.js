import { createAppContainer } from 'react-navigation';
import { createDrawerNavigator } from 'react-navigation-drawer';
import ViewAllScreen from './screens/ViewAllScreen';
import SearchScreen from './screens/SearchScreen';

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
  ViewAllScreen: {
    screen: ViewAllScreen,
  },
  Search: {
    screen: SearchScreen,
  },
});

export default createAppContainer(MyDrawerNavigator);