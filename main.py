from flask import Flask, render_template, request
from pymongo import MongoClient
import base64
from db import *

app = Flask(__name__)

# connect to remote mongoDB database
URL = "mongodb+srv://hlzhou:hlzhoumongodb@cluster0-ribbv.mongodb.net/test?retryWrites=true&w=majority"
client = MongoClient(URL)
db = client['utdb']


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
