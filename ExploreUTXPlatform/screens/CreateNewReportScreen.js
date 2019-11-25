import React, {Component} from 'react';
import {
  View, Text, StyleSheet, ScrollView,Platform,PermissionsAndroid,
  Image,Button,Dimensions
} from 'react-native';
import t from 'tcomb-form-native';
import SYImagePicker from 'react-native-syan-image-picker';
import Toast from 'react-native-simple-toast';

const Form = t.form.Form;

//the width of phone screen
const {width} = Dimensions.get('window');

//Place struct: used to build the form's inputs
const Report = t.struct({
  title: t.String,
  comment: t.String,
});

// extra options for the form's inputs
const options = {
  fields: {
    title: {
      error: 'Please input the report title.'
    },
    comment: {
        multiline: true,
        stylesheet: {
            ...Form.stylesheet,
            textbox: {
                ...Form.stylesheet.textbox,
                normal: {
                    ...Form.stylesheet.textbox.normal,
                    height: 150,
                    textAlignVertical:'top'
                },
                error: {
                    ...Form.stylesheet.textbox.error,
                    textAlignVertical:'top',
                    height: 150
                }
            }
        },
        error: 'Please input the report content.'
    },
  },
};

//the component for "create new place"
export default class CreateNewReportScreen extends Component {

  static navigationOptions = {
    title: 'Create New Report',
    headerTintColor: '#fff',
    headerStyle: {
      backgroundColor: '#BF5700',
    },
  };

  constructor(props) {
    super(props);
    //state for the current component includes:
    //1) photos(array): photos which the user picks from the gallery or camera
    //2) value(object): the values of the form's inputs
    this.state = {
      photos: [],
      value:{}
    };
    this.placeId = this.props.navigation.getParam('placeId', '5dca01e229953646f96aebda');
  }

  //Check if picking-images-related permissions have been granted. If not, request corresponding permissions through "PermissionsAndroid.request()" method.
  hasImagePermission = async () => {
    if (Platform.OS === 'ios' ||(Platform.OS === 'android' && Platform.Version < 23)) {
      return true;
    }

    const hasWritePermission = await PermissionsAndroid.check(
      PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE
    );
    const hasReadPermission = await PermissionsAndroid.check(
      PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE
    );
    const hasCameraPermission = await PermissionsAndroid.check(
      PermissionsAndroid.PERMISSIONS.CAMERA
    );
    if (hasWritePermission && hasReadPermission && hasCameraPermission) {
      return true;
    }

    try {
      if(!hasWritePermission){
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
          {
              title: 'request write_external_storage permission.',
              message:
                  'The picking images function needs the permission to write to the storage.',
              buttonNeutral: 'Ask Me Later',
              buttonNegative: 'Cancel',
              buttonPositive: 'Ok'
          }
        );
        if (granted === PermissionsAndroid.RESULTS.GRANTED) {
            console.log('You now have the write_external_storage permission.');
        } 
        else {
            console.log('Permissions denied');
        }
      }
      if(!hasReadPermission){
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
          {
              title: 'request read_external_storage permission.',
              message:
                  'The picking images function needs the permission to read the storage.',
              buttonNeutral: 'Ask Me Later',
              buttonNegative: 'Cancel',
              buttonPositive: 'Ok'
          }
        );
        if (granted === PermissionsAndroid.RESULTS.GRANTED) {
            console.log('You now have the read_external_storage permission.');
        } 
        else {
            console.log('Permissions denied');
        }
      }
      if(!hasCameraPermission){
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.CAMERA,
          {
              title: 'request the permission to use the camera.',
              message:
                  'The picking images function needs the permission to use the camera.',
              buttonNeutral: 'Ask Me Later',
              buttonNegative: 'Cancel',
              buttonPositive: 'Ok'
          },
        );
        if (granted === PermissionsAndroid.RESULTS.GRANTED) {
            console.log('You now have the permission to use the camera.');
        } 
        else {
            console.log('Permissions denied');
        }
      }
    } catch (err) {
        console.warn(err);
    }
};

  //Open the image picker to pick images from the gallery and camera and set this.state.photos using the photos picked
  handlePromiseSelectPhoto = async() => {
    const hasImagePermission = await this.hasImagePermission();
    if (!hasImagePermission) return;

    SYImagePicker.asyncShowImagePicker({imageCount: 9, enableBase64: false})
        .then(photos => {
            console.log(photos);
            const arr = photos.map(v => {
                return {...v, enableBase64: false}
            });
            this.setState({
                photos: [...arr]
            })
        })
        .catch(err => {
          console.log(err.message);
        })
};

  //When the form's inputs change, update the value state.
  handleFormChange = (value) => {
    this.setState({value:value});
  }

  //check if the current inputs of the form  are valid. If not, show the corresponding toasts.
  formDataValid = () => {
    const value = this.formRef.getValue();
    const photos=this.state.photos;
    if(value==null){
      return false;
    }
    if(photos.length==0){
      Toast.show('Please pick at least one image for the place before submission.', Toast.LONG);
      return false;
    }
    return true;
  }

  //Reset the form's inputs
  handleReset = () => {
    this.setState({value:{},photos:[]});
  }

  //Submit the form's content when it passes the validation
  handleSubmit = async() => {
    if(this.formDataValid()){
      await this.postForm();
      Toast.show('Successfully created the new report.', Toast.LONG);
      this.props.navigation.goBack();
    }
  }

  //Post the formdata to remote server
  postForm= async() =>{
    const formData = new FormData();
    const photos = this.state.photos;
    const value = this.state.value;

    for (let i = 0; i < photos.length; i++) {
      formData.append('pic_files', {
        name: "photo"+i+'.jpg',
        type: 'image/jpeg',
        uri:
          Platform.OS === 'android' ? photos[i].original_uri : photos[i].original_uri.replace('file://', '')
      });
    }
    
    Object.keys(value).forEach(key => {
      formData.append(key, value[key]);
    });

    //add place_id & user_id
    formData.append('place_id',this.placeId);

    //to update: change the current default user_id to the current logged-in user's id
    formData.append('user_id','jiayiyang1997@gmail.com');

    try {
      const response = await fetch('https://explore-ut.appspot.com/create_new_report', {
        method: 'POST',
        body: formData,
        headers: {
          'Content-Type': 'multipart/form-data',
          'Connection':'close'
        }
      });
    } catch (error) {
      console.error('Error:', error);
    }
  }

  //Render using JSX
  render() {
    return (
      <ScrollView>
        <Text style={styles.header}>Create New Report</Text>
        <View style={styles.container}>
          <Form ref={(c) => (this.formRef = c)}
                type={Report} 
                options={options}
                value={this.state.value}
                onChange={this.handleFormChange} />
          <View style={{flexDirection: 'column',marginTop:15}}>
            <View style={styles.button_container}>
              <Button title="Pick Images" onPress={this.handlePromiseSelectPhoto}/>
            </View>
            <ScrollView style={{flex: 1}} contentContainerStyle={styles.scroll}>
              {this.state.photos.map((photo, index) => {
                  let source = {uri: photo.uri};
                  if (photo.enableBase64) {
                      source = {uri: photo.base64};
                  }
                  return (
                      <Image
                          key={`image-${index}`}
                          style={styles.image}
                          source={source}
                          resizeMode={"cover"}
                      />
                  )
              })}
            </ScrollView>
          <View style={{marginTop: 15}}>
            <Button title="Reset" onPress={this.handleReset} />
          </View>
          <View style={{marginTop: 15}}>
            <Button title="Submit" onPress={this.handleSubmit} />
          </View>
          </View>
        </View>
      </ScrollView>
    );
  }
}

//styles for specific components
const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    marginTop: 0,
    padding: 20,
    backgroundColor: '#ffffff',
    flexDirection:'column'
  },
  buttonText: {
    fontSize: 18,
    color: 'white',
    alignSelf: 'center'
  },
  button: {
    height: 36,
    backgroundColor: '#BBED',
    borderColor: '#48B',
    borderWidth: 1,
    borderRadius: 8,
    marginBottom: 10,
    alignSelf: 'stretch',
    justifyContent: 'center'
  },
  text: {
    margin: 10,
    fontSize: 12,
    fontWeight: 'bold',
    textAlign: 'center'
  },
  header:{
    margin: 10,
    fontSize: 34,
    fontWeight: 'bold',
    textAlign: 'center'
  },
  scroll: {
    paddingLeft: 5,
    flexWrap: 'wrap',
    flexDirection: 'row'
  },
  image: {
    margin:16,
    width: (width - 150) / 3,
    height: (width - 150) / 3,
    backgroundColor: '#F0F0F0'
  },
  button_container:{
    width: "40%",
    alignItems: 'flex-start'
  }
});

