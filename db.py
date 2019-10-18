import datetime as dt
from pymongo import MongoClient


# define place object for the data model
class Place(object):
    def __init__(self, name=None, theme=None, tags=[], address=None, intro=None, pics=[],
                 reviews=[], likes=0, user_id=None):
        self.name = name
        self.theme = theme
        self.tags = tags
        self.address = address
        self.intro = intro
        self.pics = pics
        self.reviews = reviews
        self.likes = likes
        self.user_id = user_id


# define user object for the data model
class User(object):
    def __init__(self, email=None, username=None, name=None, profile=None,
                 gender=None, age=None, group='normal', level=0, subscription=[]):
        self.email = email
        self.username = username
        self.name = name
        self.profile = profile
        self.gender = gender
        self.age = age
        self.group = group
        self.level = level
        self.subscription = subscription


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
def create_place(db, place_data):
    """
    Create a place, insert it into the database and return the result
    :param db: a MongoClient that connects to a database through a particular URL
    :param place_data: the dict form of the place's data
    :return: a Result containing the ack and inserted id
    """
    return db.place.insert_one(place_data).inserted_id


def read_place(db, condition_key, condition_value):
    """
    Read from the database and return a single place that matches the search condition
    :param db: a MongoClient that connects to a database through a particular URL
    :param condition_key: a string with the field name we are interested in
    :param condition_value: a string with the value we want to match
    :return: a dict that represents a single place that matches the condition with decoded pictures (can be any of them if there are many)
    """
    place = db.place.find_one({condition_key: condition_value})
    if place is not None:
        if place['pics']:
            for i in range(len(place['pics'])):
                place['pics'][i] = place['pics'][i].decode()
    return place


def read_places(db, condition):
    """
    Read from the database and return a pymongo cursor with all matching places
    :param db: a MongoClient that connects to a database through a particular URL
    :param condition: a dict with key value pair(s) specifying the field(s) and value(s) we are interested in
    :return: a list of all documents matching the search criteria with decoded pictures
    """
    places = list(db.place.find(condition))
    for place in places:
        # skip places with empty photos
        if not place['pics']:
            continue
        for i in range(len(place['pics'])):
            place['pics'][i] = place['pics'][i].decode()
    return places


# note that this update method overwrite all fields of a user
def update_place_by_id(db, old_place_id, new_place):
    """
    Update an old place in the database with a new one (overwriting each field)
    :param db: a MongoClient that connects to a database through a particular URL
    :param old_place_id: a string with the old place id we want to update
    :param new_place: a place with all the new fields we want
    :return: None
    """
    db.place.update_one({'_id': old_place_id}, {'$set': {'place_name': new_place.name, 'theme': new_place.theme,
                                                         'address': new_place.address, 'intro': new_place.intro,
                                                         'pics': new_place.pics, 'reviews': new_place.reviews,
                                                         'likes': new_place.likes}})


def delete_place_by_id(db, old_place_id):
    """
    Delete a place in the database with its id
    :param db: a MongoClient that connects to a database through a particular URL
    :param old_place_id: a string with the place id we want to delete
    :return: None
    """
    db.place.delete_one({'_id': old_place_id})


# User CRUD API
def create_user(db, email=None, username=None, name=None, profile=None, gender=None, age=None, group='normal', level=0,
                subscription=[]):
    """
    Create a user, insert it into the database and return the result
    :param db: a MongoClient that connects to a database through a particular URL
    :param email: a string that represents the user's email
    :param username:  a string that represents the user's username
    :param name a string that represents the user's actual name
    :param profile: a string that represents the user's encoded profile picture
    :param gender: a string that represents the user's gender
    :param age: an int tha represents the user's age
    :param group: a string that represents if the user is an admin or normal user
    :param level: an int that represents the user's level in this app
    :param subscription: a list of place ids that the user subscribed
    :return: a Result containing the ack and inserted id
    """
    user = User(email, username, name, profile, gender, age, group, level, subscription)
    return db.user.insert_one(user.__dict__)


def read_user(db, condition_key, condition_value):
    """
    Read from the database and return a single user that matches the search condition
    :param db: a MongoClient that connects to a database through a particular URL
    :param condition_key: a string with the field name we are interested in
    :param condition_value: a string with the value we want to match
    :return: a dict that represents a single user that matches the condition (can be any of them if there are many)
    """
    return db.user.find_one({condition_key: condition_value})


# note that this update method overwrite all fields of a user
def update_user_by_id(db, old_user_email, new_user):
    """
    Update an old user in the database with a new one (overwriting each field)
    :param db: a MongoClient that connects to a database through a particular URL
    :param old_user_email: a string with the old user email we want to update
    :param new_user: an user with all the new fields we want
    :return: None
    """
    db.user.update_one({'email': old_user_email}, {'$set': {'email': new_user.email, 'username': new_user.username,
                                                      'name': new_user.name, 'profile': new_user.profile,
                                                      'gender': new_user.gender, 'age': new_user.age,
                                                      'group': new_user.group, 'level': new_user.level}})


def update_user_subscription(db, user_email, place_id):
    """
    Update an user and a place in the database with a new subscription
    :param db: a MongoClient that connects to a database through a particular URL
    :param user_email: a string with the user email we want to update
    :param place_id: the place id that  the user subscribe
    :return: None
    """
    db.user.update_one({'email': user_email}, {'$addToSet': {"subscription": str(place_id)}})


def delete_user_by_id(db, old_user_email):
    """
    Delete an user in the database with its id
    :param db: a MongoClient that connects to a database through a particular URL
    :param old_user_email: a string with the user email we want to delete
    :return: None
    """
    db.user.delete_one({'email': old_user_email})


def create_article(db, article_id=None, article_title=None, place_id=None, user_id=None,
                   pics=None, comment=None, create_date=None):
    """
    Create a article, insert it into the database and return the result
    :param db: a MongoClient that connects to a database through a particular URL
    :param article_id: a string that represents the id of the article
    :param article_title: a string that represents the title of the article
    :param place_id: a string that represents the id of the place associated with this article
    :param user_id: a string that represents the id of the user that post this article
    :param pics: a list of strings that represent the encoded pictures in the article
    :param comment: a string that represents the article body
    :param create_date: the time when this article is created
    :return: a Result containing the ack and inserted id
    """
    article = Article(article_id, article_title, place_id, user_id, pics, comment, create_date)
    return db.article.insert_one(article.__dict__)


def read_article(db, condition_key, condition_value):
    """
    Read from the database and return a single article that matches the search condition
    :param db: a MongoClient that connects to a database through a particular URL
    :param condition_key: a string with the field name we are interested in
    :param condition_value: a string with the value we want to match
    :return:  a dict that represents a single article that matches the condition (can be any of them if there are many)
    """
    return db.article.find_one({condition_key: condition_value})


def read_articles(db, condition):
    return list(db.article.find(condition))


def update_article_by_id(db, old_article_id, new_article):
    """
    Update an old article in the database with a new one (overwriting each field)
    :param db: a MongoClient that connects to a database through a particular URL
    :param old_article_id: a string with the old article id we want to update
    :param new_article:  an article with all the new fields we want
    :return: None
    """
    db.article.update_one({'article_id': old_article_id}, {'$set': {'article_id': new_article.article_id,
                                                                    'place_id': new_article.place_id,
                                                                    'user_id': new_article.user_id,
                                                                    'pics': new_article.pics,
                                                                    'comment': new_article.comment,
                                                                    'create_date': new_article.create_date}})


def delete_article_by_id(db, old_article_id):
    """
    Delete an article in the database with its id
    :param db: a MongoClient that connects to a database through a particular URL
    :param old_article_id:  a string with the article id we want to delete
    :return: None
    """
    db.article.delete_one({'article_id': old_article_id})


# execute and test the APIs
def main():
    # connect to the remote database instance
    client = MongoClient(
        "mongodb+srv://hlzhou:hlzhoumongodb@cluster0-ribbv.mongodb.net/test?retryWrites=true&w=majority")
    db = client['utdb']

    # test APIs for Place
    place = Place(name='UT Tower')
    place_id = create_place(db, place.__dict__)
    new_place = Place(name='UT Tower', intro='The Tower of UT')
    update_place_by_id(db, place_id, new_place)
    ut_tower = read_place(db, '_id', place_id)
    delete_place_by_id(db, place_id)

    # test APIs for User
    create_user(db, user_id='001', email='abcd@utexas.edu', username='abcd', password='123', name='cd')
    new_user = User(user_id='002', email='asdf@utexas.edu', username='asdf', password='456', name='gh')
    update_user_by_id(db, '001', new_user)
    user_result = read_user(db, 'username', 'asdf')
    delete_user_by_id(db, '002')

    # test APIs for Article
    create_article(db, article_id='a999', article_title='UT tower', place_id='p002', user_id='u001',
                   comment='UT tower is beautiful', create_date=dt.datetime.now())
    new_article = Article(article_id='a1000', article_title='UT tower', place_id='p002', user_id='u001',
                          comment='UT tower is orange at night', create_date=dt.datetime.now())
    update_article_by_id(db, 'a999', new_article)
    article_result = read_article(db, 'article_id', 'a1000')
    delete_article_by_id(db, 'a1000')

    # print the updated document
    print(str(ut_tower))
    print(str(user_result))
    print(str(article_result))


if __name__ == '__main__':
    main()
