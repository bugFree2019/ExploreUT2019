import datetime as dt
from pymongo import MongoClient


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


def read_places(db, condition):
    return db.place.find(condition)


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


def update_article_by_id(db, old_article_id, new_article):
    db.article.update_one({'article_id': old_article_id}, {'$set': {'article_id': new_article.article_id,
                                                                    'place_id': new_article.place_id,
                                                                    'user_id': new_article.user_id,
                                                                    'pics': new_article.pics,
                                                                    'comment': new_article.comment,
                                                                    'create_date': new_article.create_date}})


def delete_article_by_id(db, old_article_id):
    db.article.delete_one({'article_id': old_article_id})


# execute and test the APIs
def main():
    # connect to the remote database instance
    client = MongoClient(
        "mongodb+srv://hlzhou:hlzhoumongodb@cluster0-ribbv.mongodb.net/test?retryWrites=true&w=majority")
    db = client['utdb']

    # test APIs for Place
    create_place(db, place_id='002', name='UT Tower')
    new_place = Place(place_id='003', name='UT Tower', intro='The Tower of UT')
    update_place_by_id(db, '002', new_place)
    ut_tower = read_place(db, 'place_id', '003')
    delete_place_by_id(db, '003')

    # test APIs for User
    create_user(db, user_id='001', email='abcd@utexas.edu', username='abcd', password='123', first='ab', last='cd')
    new_user = User(user_id='002', email='asdf@utexas.edu', username='asdf', password='456', first='ef', last='gh')
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
