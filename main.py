from flask import Flask, render_template, request
from pymongo import MongoClient
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
    print(tag)
    reports = read_places(db, {'tags': tag})
    print(reports)

    # send the search result to the front end html template
    return render_template('search.html', reports=reports)


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8080, debug=True)
