from flask import Flask, render_template, request
from pymongo import MongoClient
import base64
from db import *

app = Flask(__name__)

# connect to remote mongoDB database
URL = "mongodb+srv://hlzhou:hlzhoumongodb@cluster0-ribbv.mongodb.net/test?retryWrites=true&w=majority"
client = MongoClient(URL)
db = client['utdb']


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

    # query the database and extract the report corresponding to that tag
    reports = read_places(db, {'tags': tag})

    # send the search result to the front end html template
    return render_template('search.html', reports=reports)


# sample function to create a place with an image
def insert_image():
    """
    The sample function for creating a new place with encoded image stored in MongoDB
    :return: None
    """
    with open('static/ut-tower.jpg', "rb") as image_file:
        pic = base64.b64encode(image_file.read())
        result = create_place(db, place_id='006', name='UT tower image test', theme=None, tags='test', address=None, intro='tower of UT', pics=[pic], reviews=None, likes=0)


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8080, debug=True)
