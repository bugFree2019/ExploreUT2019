import google.oauth2.id_token
import base64
import time
import io

from bson import ObjectId
from bson.json_util import dumps
from flask import Flask, render_template, request, redirect, url_for, send_file, make_response, abort
from google.auth.transport import requests
from flask_googlemaps import GoogleMaps, Map
from db import *
from bson.binary import Binary

app = Flask(__name__)
GoogleMaps(app, key='AIzaSyDfiw9D8Ga_cvPreutbTmjdLZ1lBwyE3Qw')
firebase_request_adapter = requests.Request()


@app.route('/', methods=['GET'])
def home_places():
    places = read_places(db, {})
    # return json object to android app
    user_agent = request.headers.get('User-Agent')
    if 'android' in user_agent.lower():
        return json_response(places)
    return render_template('home.html', places=places)


@app.route('/index', methods=['GET', 'POST'])
def index():
    if request.method == 'GET':
        id_token = request.cookies.get('token')
        error_message = None
        claims = None
        times = None
        thisuser = None
        allplaces = None
        allarticles = None

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
                    allarticles = read_articles(db, {'user_id': thisuser['_id']})
                else:
                    thisuser = create_user(db, email=claims['email'])

            except ValueError as exc:
                # This will be raised if the token is expired or any other
                # verification checks fail.
                error_message = str(exc)

        return render_template('index.html', user_data=claims, error_message=error_message, times=times, users=thisuser,
                               places=allplaces, articles=allarticles)

    else:
        thisuser = None
        allplaces = None
        allarticles = None
        user = request.get_json()
        user_agent = request.headers.get('User-Agent')
        # print(user['email'])
        thisuser = read_user(db, 'email', user['email'])

        if thisuser != None:
    # user_subscriptions = [ObjectId(subscription) for subscription in thisuser['subscription']]
            allplaces = read_places(db, {'_id': {'$in': thisuser['subscription']}})
            allarticles = read_articles(db, {'user_id': thisuser['_id']})
            if 'android' in user_agent.lower():
                if (len(allplaces) > 0):
                    print(allplaces[0]['name'])
                return json_response(allplaces)
            return render_template('index.html', users=thisuser,
                               places=allplaces, articles=allarticles)
        else:
            thisuser = create_user(db, email=user['email'])
            if 'android' in user_agent.lower():
                return json_response(None)
            return render_template('index.html', users=thisuser,
                               places=allplaces, articles=allarticles)


@app.route('/search', methods=['GET'])
def search():
    """
    The function that handle searching with a particular tag
    :return: a html page for rendering with the matching places found
    """
    # get the query tag from the html form input
    tag = request.args.get('tag')

    # get the user agent from the request
    user_agent = request.headers.get('User-Agent')

    # return empty list if tag is None or null
    if not tag:
        if 'android' in user_agent.lower():
            return json_response(None)
        return render_template('search.html', places=[], result_tag=tag)

    # query the database and extract the places corresponding to that tag
    places = read_places(db, {'tags': {'$regex': tag, '$options': 'i'}})

    if 'android' in user_agent.lower():
        return json_response(places)

    # send the search result to the front end html template
    return render_template('search.html', places=places, result_tag=tag)


@app.route('/subscribe/<place_id>', methods=['POST'])
def subscribe(place_id):
    subscribe_helper(place_id, True)
    user_agent = request.headers.get('User-Agent')
    if 'android' in user_agent.lower():
        return app.response_class(response=dumps(Place().__dict__), status=200, mimetype='application/json')
    return redirect(url_for('.view_one_place', place_id=place_id))


@app.route('/unsubscribe/<place_id>', methods=['POST'])
def unsubscribe(place_id):
    subscribe_helper(place_id, False)
    user_agent = request.headers.get('User-Agent')
    if 'android' in user_agent.lower():
        return app.response_class(response=dumps(Place().__dict__), status=200, mimetype='application/json')
    return redirect(url_for('.view_one_place', place_id=place_id))


def subscribe_helper(place_id, is_subscribe):
    id_token = request.cookies.get("token")
    user_email = request.args.get('user_email')
    if id_token or user_email:
        if id_token:
            claims = google.oauth2.id_token.verify_firebase_token(id_token, firebase_request_adapter)
            user_email = claims['email']
        if is_subscribe:
            update_user_subscription(db, user_email, ObjectId(place_id))
        else:
            update_user_unsubscription(db, user_email, ObjectId(place_id))


@app.route('/view_one_place', methods=['GET'])
def view_one_place():
    # subscribe_status: -1 denotes not logged in, 0 denotes not not subscribed 1 denotes subscribed
    subscribe_status = -1

    user_agent = request.headers.get('User-Agent')
    place_id = request.args.get('place_id')
    if not place_id:
        if 'android' in user_agent.lower():
            return abort(404)
        return render_template('view_one_place.html', place=[], subscribe_status=subscribe_status, error_message=None)
    place = read_place(db, '_id', ObjectId(place_id))
    id_token = request.cookies.get('token')
    user_email = request.args.get('user_email')
    if id_token or user_email:
        try:
            if id_token:
                claims = google.oauth2.id_token.verify_firebase_token(id_token, firebase_request_adapter)
                user_email = claims['email']
            current_user = read_user(db, 'email', user_email)
            if current_user is not None:
                if ObjectId(place_id) in current_user['subscription']:
                    subscribe_status = 1
                else:
                    subscribe_status = 0
        except ValueError as exc:
            if 'android' in user_agent.lower():
                return abort(400)
            error_message = str(exc)
            return render_template('view_one_place.html', place=place, subscribe_status=subscribe_status,
                                   error_message=error_message)

    if 'android' in user_agent.lower():
        return json_response(place, subscribe_status)
    return render_template('view_one_place.html', place=place, subscribe_status=subscribe_status, error_message=None)


@app.route('/view_places', methods=['GET'])
def view_places():
    condition = request.args.get('condition')  # in the future, condition will be nearby location
    places = read_places(db, condition)
    user_agent = request.headers.get('User-Agent')
    if 'android' in user_agent.lower():
        return json_response(places)
    return render_template('view_places.html', places=places)


@app.route('/create_new_place', methods=['GET', 'POST'])
def add_place():
    if request.method == 'POST':
        data = request.form.to_dict(flat=True)
        data['tags'] = []
        data['tags'].append(data['tag'])
        data.pop('tag')

        location_str = data['location']
        coordinates = location_str.split(' ')
        lat = float(coordinates[0])
        lng = float(coordinates[1])
        location = {'lat': lat, 'lng': lng}
        data['location'] = location

        data['pics'] = []
        image_files = request.files.getlist('pic_files')
        for image_file in image_files:
            binary = Binary(base64.b64encode(image_file.read()))
            data['pics'].append(binary)

        place_id = create_place(db, data)

        return redirect(url_for('.view_one_place', place_id=place_id))

    return render_template('add_new_place.html', action='Add', place={})


@app.route('/create_new_report', methods=['GET', 'POST'])
def add_report():
    place_id = ''
    if request.method == 'GET':
        place_id = request.args.get('place_id')
        place_name = get_place_name_by_id(db, ObjectId(place_id))

    if request.method == 'POST':
        data = request.form.to_dict(flat=True)
        image_files = request.files.getlist('pic_files')
        #update_place_pics_by_id(db, data['place_id'], base64.b64encode(image_file.read()))
        # add this pic to the pics array of the place
        for image in image_files:
            update_place_pics_by_id(db,data['place_id'],Binary(base64.b64encode(image.read())))
        # add the comment to the reviews array of the plac
        update_place_reviews_by_id(db,data['place_id'],data['comment'])
        data['user_id'] = get_user_id_from_email(db, data['user_id'])
        data['create_date'] = time.asctime(time.localtime(time.time()))
        create_article(db, data)
        return redirect(url_for('.view_one_place', place_id=data['place_id']))

    return render_template('add_new_report.html', action='Add', place_name=place_name, place_id=place_id, article={})


@app.route('/view_places_by_theme', methods=['GET'])
def view_places_by_theme():
    """
    The function that handle searching with a particular tag
    :return: a html page for rendering with the matching places found
    """
    # get the query tag from the html form input
    theme = request.args.get('theme')

    # get the user agent from the request
    user_agent = request.headers.get('User-Agent')

    # return empty list if tag is None or null
    if not theme:
        if 'android' in user_agent.lower():
            return json_response(None)
        return abort(404)

    # query the database and extract the places corresponding to that tag
    places = read_places(db, {'theme': {'$regex': theme, '$options': 'i'}})

    if 'android' in user_agent.lower():
        return json_response(places)

    # send the search result to the front end html template
    return abort(404)


@app.route('/map', methods=['GET'])
def my_map():
    # creating a map in the view
    mymap = Map(
        identifier='view-side',
        lat=37.4419,
        lng=-122.1419,
        markers=[(37.4419, -122.1419)]
    )
    sndmap = Map(
        identifier='sndmap',
        lat=37.4419,
        lng=-122.1419,
        markers=[
            {
                'icon': 'http://maps.google.com/mapfiles/ms/icons/green-dot.png',
                'lat': 37.4419,
                'lng': -122.1419,
                'infobox': '<b>Hello World</b>'
            },
            {
                'icon': 'http://maps.google.com/mapfiles/ms/icons/blue-dot.png',
                'lat': 37.4300,
                'lng': -122.1400,
                'infobox': '<b>Hello World from other place</b>'
            }
        ]
    )
    return render_template('map.html', mymap=mymap, sndmap=sndmap)


@app.route('/place_image/<place_id>/<image_id>.jpg', methods=['GET'])
def get_place_image(place_id, image_id):
    """"
    This method return the image with specified place id and image id (list index)
    Note that the extension of the file has to be jpg in this function
    :param place_id: the place id where we want  to extract image from
    :param image_id: the image index for the place
    :return: a response with image_id th image about the place
    """
    image_id = int(image_id)
    images = read_place_images(db, ObjectId(place_id))
    if images and image_id >= 0 and image_id < len(images):
        image = images[image_id]
        if isinstance(image, bytes):
            image = base64.decodebytes(image)
            byte_io = io.BytesIO(image)
            response = make_response(send_file(byte_io, mimetype='image/jpg'))
            response.headers['Content-Transfer-Encoding'] = 'base64'
            return response
        else:
            abort(404)
    else:
        # return 404 not found if the image or place does not exist
        abort(404)


def json_response(places, subscribe_status=-2):
    """
    return a json serialization of the place(s) and exclude images
    :param places: the place(s) object we want to convert to json
    :param subscribe_status: used by view_one_place to return the status of the user subscription
    :return: a response of json form containing all the place(s)
    """
    # exclude the images
    if isinstance(places, list):
        for place in places:
            num_pics = 0
            if place.__contains__('pics'):
                num_pics = len(place['pics'])
                del place['pics']
            place.update({'num_pics':num_pics})
            place['_id'] = str(place['_id'])

    elif isinstance(places, dict):
        num_pics = 0
        if places.__contains__('pics'):
            num_pics = len(places['pics'])
        del places['pics']
        places.update({'num_pics':num_pics})
        if subscribe_status != -2:
            places['subscribe_status'] = subscribe_status
        places['_id'] = str(places['_id'])
    # serialize the places to json and return the response
    response = app.response_class(response=dumps(places), status=200, mimetype='application/json')
    return response


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8080, debug=True)
