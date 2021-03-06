      /**
 * Copyright 2018, Google LLC
 * Licensed under the Apache License, Version 2.0 (the `License`);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an `AS IS` BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

function with_subscription() {
    if(document.getElementById("subscribe")) {
        document.getElementById("subscribe").hidden = true;
    }
    if (document.getElementById("unsubscribe")) {
        document.getElementById("unsubscribe").hidden = false;
    }
    console.log('with subscription');
}

function without_subscription() {
    if (document.getElementById("subscribe")) {
        document.getElementById("subscribe").hidden = false;
    }
    if (document.getElementById("unsubscribe")) {
        document.getElementById("unsubscribe").hidden = true;
    }
    console.log('without subscription');
}

function not_logged_in() {
    if (document.getElementById("subscribe")) {
        document.getElementById("subscribe").hidden = true;
    }
    if (document.getElementById("unsubscribe")) {
        udocument.getElementById("unsubscribe").hidden = true;
    }
    console.log('not logged in');
}


window.addEventListener('load', function () {

  // [START gae_python37_auth_signout]
  document.getElementById('sign-out').onclick = function () {
    firebase.auth().signOut();
    alert("Sign out successfully!")
  };
  // [END gae_python37_auth_signout]

  // [START gae_python37_auth_UIconfig_variable]
  // FirebaseUI config.
  var uiConfig = {
    signInSuccessUrl: '/index',
    signInOptions: [
      // Remove any lines corresponding to providers you did not check in
      // the Firebase console.
      firebase.auth.GoogleAuthProvider.PROVIDER_ID,
      firebase.auth.EmailAuthProvider.PROVIDER_ID,
    ],
    // Terms of service url.
    tosUrl: '/index'
  };
  // [END gae_python37_auth_UIconfig_variable]

  // [START gae_python37_auth_request]

  firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
      console.log(`Signed in as ${user.displayName} (${user.email})`);
      user.getIdToken().then(function (token) {
        // Add the token to the browser's cookies. The server will then be
        // able to verify the token against the API.
        // SECURITY NOTE: As cookies can easily be modified, only put the
        // token (which is verified server-side) in a cookie; do not add other
        // user information.
        document.getElementById('sign-out').hidden = false;
        if(document.getElementById('login-info')) {
          document.getElementById('login-info').hidden = false;
        }
        if(document.getElementById('add_report')) {
          document.getElementById('add_report').hidden = false;
        }
          
        if (document.getElementById("subscribe_status")) {
          var subscribe_status = document.getElementById("subscribe_status").textContent;
          if (subscribe_status === "1") {
            with_subscription();
          }
          else if (subscribe_status === "0") {
            without_subscription();
          }
          else if (subscribe_status === "-1") {
            not_logged_in();
          }
        }
        document.cookie = "token=" + token;
      });
    } else {
      // User is signed out.
      // Update the login state indicators.
      // Initialize the FirebaseUI Widget using Firebase.
      if(document.getElementById('firebaseui-auth-container')){
        var ui = new firebaseui.auth.AuthUI(firebase.auth());
        ui.start('#firebaseui-auth-container', uiConfig);
      }
      document.getElementById('sign-out').hidden = true;
      if(document.getElementById('login-info')) {
        document.getElementById('login-info').hidden = true;
      }
      if(document.getElementById('subscribe')) {
        document.getElementById('subscribe').hidden = true;
      }
      if(document.getElementById('unsubscribe')) {
        document.getElementById('unsubscribe').hidden = true;
      }
      if(document.getElementById('add_report')) {
        document.getElementById('add_report').hidden = true;
      }
      // Clear the token cookie.
      document.cookie = "token=";
    }
  }, function (error) {
    console.log(error);
    alert('Unable to log in: ' + error)
  });
  // [END gae_python37_auth_request]
});
