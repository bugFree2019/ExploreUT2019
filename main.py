import datetime
import google.oauth2.id_token
import base64
from flask import Flask, render_template, request
from pymongo import MongoClient
from google.auth.transport import requests
from google.cloud import datastore
from db import *

app = Flask(__name__)
firebase_request_adapter = requests.Request()
datastore_client = datastore.Client()

# connect to remote mongoDB database
URL = "mongodb+srv://hlzhou:hlzhoumongodb@cluster0-ribbv.mongodb.net/test?retryWrites=true&w=majority"
client = MongoClient(URL)
db = client['utdb']


@app.route('/', methods=['GET'])
def home_places():
    condition = request.args.get('condition')  # in the future, condition will be nearby location
    places = read_places(db, condition)
    return render_template('home.html', places=places)


@app.route('/index', methods=['GET'])
def index():
    id_token = request.cookies.get("token")
    error_message = None
    claims = None
    times = None
    thisuser = None
    allarticles = None

    if id_token:
        try:
            # Verify the token against the Firebase Auth API. This example
            # verifies the token on each page load. For improved performance,
            # some applications may wish to cache results in an encrypted
            # session store (see for instance
            # http://flask.pocoo.org/docs/1.0/quickstart/#sessions).
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)

            thisuser = read_user(db, 'email', claims['email'])
            if thisuser != None:
                allarticles = read_articles(db, {'user_id': thisuser['user_id']})

        except ValueError as exc:
            # This will be raised if the token is expired or any other
            # verification checks fail.
            error_message = str(exc)

    return render_template(
        'index.html',
        user_data=claims, error_message=error_message, times=times, users=thisuser, articles=allarticles)


@app.route('/search', methods=['GET'])
def search():
    """
    The function that handle searching with a particular tag
    :return: a html page for rendering with the matching places found
    """
    # get the query tag from the html form input
    tag = request.args.get('tag')

    # return empty list if tag is None or null
    if not tag:
        return render_template('search.html', reports=[])

    # query the database and extract the places corresponding to that tag
    places = read_places(db, {'tags': tag})

    # send the search result to the front end html template
    return render_template('search.html', places=places)


@app.route('/view_one_place', methods=['GET'])
def view_one_place():
    place_id = request.args.get('placeId')

    if not place_id:
        return render_template('view_one_place.html', place=[])

    place = read_place(db, 'place_id', place_id)

    return render_template('view_one_place.html', place=place)


@app.route('/view_places', methods=['GET'])
def view_places():
    condition = request.args.get('condition')  # in the future, condition will be nearby location
    places = read_places(db, condition)
    return render_template('view_places.html', places=places)


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8080, debug=True)
