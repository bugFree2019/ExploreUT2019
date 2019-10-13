# Copyright 2018 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import datetime
from pymongo import MongoClient

from flask import Flask, render_template, request
from google.auth.transport import requests
from google.cloud import datastore
import google.oauth2.id_token



firebase_request_adapter = requests.Request()

# [START gae_python37_datastore_store_and_fetch_user_times]
datastore_client = datastore.Client()

# [END gae_python37_datastore_store_and_fetch_user_times]
app = Flask(__name__)


# [START gae_python37_datastore_store_and_fetch_user_times]
def store_time(email, dt):
    entity = datastore.Entity(key=datastore_client.key('User', email, 'visit'))
    entity.update({
        'timestamp': dt
    })

    datastore_client.put(entity)


def fetch_times(email, limit):
    ancestor = datastore_client.key('User', email)
    query = datastore_client.query(kind='visit', ancestor=ancestor)
    query.order = ['-timestamp']

    times = query.fetch(limit=limit)

    return times
# [END gae_python37_datastore_store_and_fetch_user_times]


# [START gae_python37_datastore_render_user_times]
@app.route('/')
def root():

    # test APIs for Place
    # create_place(db, place_id='005', name='McCombs School of Business')
    # new_place = Place(place_id='006', name='Jester Second Floor Dining', intro='Eating hall')

    # test APIs for User
    # create_user(db, user_id='001', email='abcd@utexas.edu', username='abcd', password='123', first='ab', last='cd')
    # new_user = User(user_id='002', email='asdf@utexas.edu', username='asdf', password='456', first='ef', last='gh')
    
    # test APIs for Article
    #create_article(db, article_id='a0001', article_title='Jester', place_id='p006', user_id='u001',
                   #comment='Jester is delicious', create_date=datetime.datetime.now())
    # new_article = Article(article_id='a0002', article_title='Business School', place_id='p005', user_id='u002',
    #                       comment='Business school is beautiful', create_date=datetime.datetime.now())


    # print the updated document
    # Verify Firebase auth.
    id_token = request.cookies.get("token")
    error_message = None
    claims = None
    times = None

    if id_token:
        try:
            # Verify the token against the Firebase Auth API. This example
            # verifies the token on each page load. For improved performance,
            # some applications may wish to cache results in an encrypted
            # session store (see for instance
            # http://flask.pocoo.org/docs/1.0/quickstart/#sessions).
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)

            store_time(claims['email'], datetime.datetime.now())
            times = fetch_times(claims['email'], 10)

        except ValueError as exc:
            # This will be raised if the token is expired or any other
            # verification checks fail.
            error_message = str(exc)

    return render_template(
        'index2.html',
        user_data=claims, error_message=error_message, times=times)
# [END gae_python37_datastore_render_user_times]

@app.route('/index', methods=['GET'])
def index():
    client = MongoClient(
        "mongodb+srv://hlzhou:hlzhoumongodb@cluster0-ribbv.mongodb.net/test?retryWrites=true&w=majority")
    db = client['utdb']

    business_school = read_place(db, 'place_id', '005')
    user_result = read_user(db, 'username', 'abcd')
    article_result = read_article(db, 'article_id', 'a0001')

    allplaces = read_all_place(db)
    allusers = read_all_user(db)
    allarticles = read_all_article(db)

    id_token = request.cookies.get("token")
    error_message = None
    claims = None
    times = None

    if id_token:
        try:
            # Verify the token against the Firebase Auth API. This example
            # verifies the token on each page load. For improved performance,
            # some applications may wish to cache results in an encrypted
            # session store (see for instance
            # http://flask.pocoo.org/docs/1.0/quickstart/#sessions).
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)

            store_time(claims['email'], datetime.datetime.now())
            times = fetch_times(claims['email'], 10)

        except ValueError as exc:
            # This will be raised if the token is expired or any other
            # verification checks fail.
            error_message = str(exc)

    return render_template(
        'index2.html',
        user_data=claims, error_message=error_message, times=times, places=allplaces, users=allusers, articles=allarticles)
# [END gae_python37_datastore_render_user_times]

# define place object for the data model
class Place(object):
    def __init__(self, place_id=None, name=None, theme=None, address=None, intro=None, pics=None, reviews=None,
                 likes=0):
        self.place_id = place_id
        self.name = name
        self.theme = theme
        self.address = address
        self.intro = intro
        self.pics = pics
        self.reviews = reviews
        self.likes = likes


# define user object for the data model
class User(object):
    def __init__(self, user_id=None, email=None, username=None, password=None, first=None, last=None, profile=None,
                 gender=None, age=None, group='normal', level=0):
        self.user_id = user_id
        self.email = email
        self.username = username
        self.password = password
        self.first = first
        self.last = last
        self.profile = profile
        self.gender = gender
        self.age = age
        self.group = group
        self.level = level

    def full_name(self):
        return self.first + ' ' + self.last


class Article(object):
    def __init__(self, article_id=None, article_title=None, place_id=None,
                 user_id=None, pics=None, comment=None, create_date=None):
        self.article_id = article_id
        self.article_title = article_title
        self.place_id = place_id
        self.user_id = user_id
        self.pics = pics
        self.comment = comment
        self.create_date = create_date


# Place CRUD API
def create_place(db, place_id=None, name=None, theme=None, address=None, intro=None, pics=None, reviews=None,
                 likes=0):
    place = Place(place_id, name, theme, address, intro, pics, reviews, likes)
    return db.place.insert_one(place.__dict__)


def read_place(db, condition_key, condition_value):
    return db.place.find_one({condition_key: condition_value})

def read_all_place(db):
    return db.place.find()

# note that this update method overwrite all fields of a user
def update_place_by_id(db, old_place_id, new_place):
    db.place.update_one({'place_id': old_place_id}, {'$set': {'place_id': new_place.place_id,
                                                              'place_name': new_place.name, 'theme': new_place.theme,
                                                              'address': new_place.address, 'intro': new_place.intro,
                                                              'pics': new_place.pics, 'reviews': new_place.reviews,
                                                              'likes': new_place.likes}})


def delete_place_by_id(db, old_place_id):
    db.place.delete_one({'place_id': old_place_id})


# User CRUD API
def create_user(db, user_id=None, email=None, username=None, password=None, first=None, last=None,
                profile=None, gender=None, age=None, group='normal', level=0):
    user = User(user_id, email, username, password, first, last, profile, gender, age, group, level)
    return db.user.insert_one(user.__dict__)


def read_user(db, condition_key, condition_value):
    return db.user.find_one({condition_key: condition_value})

def read_all_user(db):
    return db.user.find()

# note that this update method overwrite all fields of a user
def update_user_by_id(db, old_user_id, new_user):
    db.user.update_one({'user_id': old_user_id}, {'$set': {'user_id': new_user.user_id, 'email': new_user.email,
                                                           'username': new_user.username, 'password': new_user.password,
                                                           'first': new_user.first, 'last': new_user.last,
                                                           'profile': new_user.profile, 'gender': new_user.gender,
                                                           'age': new_user.age, 'group': new_user.group,
                                                           'level': new_user.level}})


def delete_user_by_id(db, old_user_id):
    db.user.delete_one({'user_id': old_user_id})


def create_article(db, article_id=None, article_title=None, place_id=None, user_id=None,
                   pics=None, comment=None, create_date=None):
    article = Article(article_id, article_title, place_id, user_id, pics, comment, create_date)
    return db.article.insert_one(article.__dict__)


def read_article(db, condition_key, condition_value):
    return db.article.find_one({condition_key: condition_value})

def read_all_article(db):
    return db.article.find()

def update_article_by_id(db, old_article_id, new_article):
    db.article.update_one({'article_id': old_article_id}, {'$set': {'article_id': new_article.article_id,
                                                                    'place_id': new_article.place_id,
                                                                    'user_id': new_article.user_id,
                                                                    'pics': new_article.pics,
                                                                    'comment': new_article.comment,
                                                                    'create_date': new_article.create_date}})


def delete_article_by_id(db, old_article_id):
    db.article.delete_one({'article_id': old_article_id})


if __name__ == '__main__':
    # This is used when running locally only. When deploying to Google App
    # Engine, a webserver process such as Gunicorn will serve the app. This
    # can be configured by adding an `entrypoint` to app.yaml.

    # Flask's development server will automatically serve static files in
    # the "static" directory. See:
    # http://flask.pocoo.org/docs/1.0/quickstart/#static-files. Once deployed,
    # App Engine itself will serve those files as configured in app.yaml.
    app.run(host='127.0.0.1', port=8080, debug=True)
