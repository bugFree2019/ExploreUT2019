import google.oauth2.id_token
import base64

from bson import ObjectId
from flask import Flask, render_template, request, redirect, url_for
from pymongo import MongoClient
from google.auth.transport import requests
from flask_googlemaps import GoogleMaps, Map
from db import *

app = Flask(__name__)
GoogleMaps(app, key='AIzaSyDfiw9D8Ga_cvPreutbTmjdLZ1lBwyE3Qw')
firebase_request_adapter = requests.Request()

# connect to remote mongoDB database
URL = 'mongodb+srv://hlzhou:hlzhoumongodb@cluster0-ribbv.mongodb.net/test?retryWrites=true&w=majority'
client = MongoClient(URL)
db = client['utdb']


@app.route('/', methods=['GET'])
def home_places():
	condition = request.args.get('condition')  # in the future, condition will be nearby location
	places = read_places(db, condition)
	return render_template('home.html', places=places)


@app.route('/index', methods=['GET'])
def index():
	id_token = request.cookies.get('token')
	error_message = None
	claims = None
	times = None
	thisuser = None
	allplaces = None

	if id_token:
		try:
			# Verify the token against the Firebase Auth API. This example
			# verifies the token on each page load. For improved performance,
			# some applications may wish to cache results in an encrypted
			# session store (see for instance
			# http://flask.pocoo.org/docs/1.0/quickstart/#sessions).
			claims = google.oauth2.id_token.verify_firebase_token(id_token, firebase_request_adapter)

			thisuser = read_user(db, 'email', claims['email'])
			if thisuser != None:
				# user_subscriptions = [ObjectId(subscription) for subscription in thisuser['subscription']]
				allplaces = read_places(db, {'_id': {'$in': thisuser['subscription']}})
			else:
				thisuser = create_user(db, email=claims['email'])

		except ValueError as exc:
			# This will be raised if the token is expired or any other
			# verification checks fail.
			error_message = str(exc)

	return render_template('index.html', user_data=claims, error_message=error_message, times=times, users=thisuser,
							places=allplaces)


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
		return render_template('search.html', places=[], result_tag=tag)

	# query the database and extract the places corresponding to that tag
	places = read_places(db, {'tags': tag})

	# send the search result to the front end html template
	return render_template('search.html', places=places, result_tag=tag)


@app.route('/subscribe/<place_id>', methods=['POST'])
def subscribe(place_id):
	subscribe_helper(place_id, True)
	return redirect(url_for('.view_one_place', placeId=place_id))


@app.route('/unsubscribe/<place_id>', methods=['POST'])
def unsubscribe(place_id):
	subscribe_helper(place_id, False)
	return redirect(url_for('.view_one_place', placeId=place_id))


def subscribe_helper(place_id, is_subscribe):
	id_token = request.cookies.get("token")
	if id_token:
		claims = google.oauth2.id_token.verify_firebase_token(id_token, firebase_request_adapter)
		if is_subscribe:
			update_user_subscription(db, claims['email'], ObjectId(place_id))
		else:
			update_user_unsubscription(db, claims['email'], ObjectId(place_id))


@app.route('/view_one_place', methods=['GET'])
def view_one_place():
	# print(request.method)
	if request.method == 'GET':
		place_id = request.args.get('placeId')
		# subscribe_status: -1 denotes not logged in, 0 denotes not not subscribed 1 denotes subscribed
		subscribe_status = -1
		if not place_id:
			return render_template('view_one_place.html', place=[], subscribe_status=subscribe_status, error_message=None)

		place = read_place(db, '_id', ObjectId(place_id))
		id_token = request.cookies.get('token')
		if id_token:
			try:
				claims = google.oauth2.id_token.verify_firebase_token(id_token, firebase_request_adapter)
				current_user = read_user(db, 'email', claims['email'])
				if current_user is not None:
					if ObjectId(place_id) in current_user['subscription']:
						print("yes")
						subscribe_status = 1
						return render_template('view_one_place.html', place=place, subscribe_status=subscribe_status, error_message=None)
					else:
						print("no")
						subscribe_status = 0
						return render_template('view_one_place.html', place=place, subscribe_status=subscribe_status, error_message=None)
			except ValueError as exc:
				error_message = str(exc)
				return render_template('view_one_place.html', place=place, subscribe_status=subscribe_status,
										error_message=error_message)

		return render_template('view_one_place.html', place=place, subscribe_status=subscribe_status, error_message=None)


@app.route('/view_places', methods=['GET'])
def view_places():
	condition = request.args.get('condition')  # in the future, condition will be nearby location
	places = read_places(db, condition)
	return render_template('view_places.html', places=places)


@app.route('/create_new_place', methods=['GET', 'POST'])
def add_place():
	if request.method == 'POST':
		data = request.form.to_dict(flat=True)

		# If an image was uploaded, update the data to point to the new image.
		# [START image_url]
		# image_url = upload_image_file(request.files.get('image'))
		# [END image_url]
		# image_file = request.files.get('pic_file')

		image_files = request.files.getlist("pic_files")
		data['pics'] = []
		for image in image_files:
			data['pics'].append(base64.b64encode(image.read()))

		data['reviews'] = []
		data['likes'] = 0
		# [START image_url2]
		# [END image_url2]

		place_id = create_place(db, data)

		return redirect(url_for('.view_one_place', placeId=place_id))

	return render_template("add_new_place.html", action="Add", place={})


@app.route('/map', methods=["GET"])
def my_map():
	# creating a map in the view
	mymap = Map(
		identifier="view-side",
		lat=37.4419,
		lng=-122.1419,
		markers=[(37.4419, -122.1419)]
	)
	sndmap = Map(
		identifier="sndmap",
		lat=37.4419,
		lng=-122.1419,
		markers=[
		  {
			 'icon': 'http://maps.google.com/mapfiles/ms/icons/green-dot.png',
			 'lat': 37.4419,
			 'lng': -122.1419,
			 'infobox': "<b>Hello World</b>"
		  },
		  {
			 'icon': 'http://maps.google.com/mapfiles/ms/icons/blue-dot.png',
			 'lat': 37.4300,
			 'lng': -122.1400,
			 'infobox': "<b>Hello World from other place</b>"
		  }
		]
	)
	return render_template('map.html', mymap=mymap, sndmap=sndmap)


if __name__ == '__main__':
	app.run(host='127.0.0.1', port=8080, debug=True)
