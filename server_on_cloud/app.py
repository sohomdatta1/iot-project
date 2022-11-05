import os
from werkzeug.utils import secure_filename
from flask import *
import hashlib
import uuid
import random
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy.sql import func

from flask_socketio import SocketIO, emit, send

app = Flask(__name__, template_folder='templates')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///db.db'
app.static_folder = 'templates/static'
db = SQLAlchemy(app)

socketio = SocketIO(app)

class User(db.Model):
    username = db.Column(db.String(30), primary_key = True)
    password = db.Column(db.String(300))
    user_id = db.Column(db.String(37))
    wifi_networks = db.relationship('Wifi', backref='user')
    devices = db.relationship('Device', backref='user')


class Wifi(db.Model):
    uuid = db.Column(db.String(37), primary_key = True)
    ssid = db.Column(db.String(300))
    password = db.Column(db.String(300))
    ect = db.Column(db.String(5))
    krack = db.Column(db.String(1))
    user_id = db.Column(db.String(30), db.ForeignKey('user.username'))


class Device(db.Model):
    name = db.Column(db.String(30))
    uuid = db.Column(db.String(37), primary_key = True)
    user_id = db.Column(db.String(30), db.ForeignKey('user.username'))

with app.app_context():
    db.create_all()

@app.route('/')
def landing():
    return render_template("index.html")

@app.route('/login', methods = ['POST', 'GET'])
def login():
    if request.method == 'POST' and 'username' in request.form and 'password' in request.form:
        username = request.form['username']
        password = request.form['password']
        password = hashlib.sha256(password.encode()).hexdigest()
        user = User.query.filter_by(username = username, password = password).first()
        if user:
            resp = make_response(redirect('user'))
            resp.set_cookie('user_identifier', user['user_id'])
            return resp
    return render_template('login.html')
@app.route('/register', methods = ['POST', 'GET'])
def register():
    msg = ''
    if request.method == 'POST' and 'username' in request.form and 'password' in request.form:
        username = request.form['username']
        password = request.form['password']
        user_id = uuid.uuid4()
        password = hashlib.sha256(password.encode()).hexdigest()
        secret = os.urandom(30).hex()
        user = User.query.filter_by(username = username).all()
        if user:
            msg = 'user already exists!'
            return render_template('register.html', msg=msg)
        user = User(username = username, password = password, user_id=user_id, secret = secret)
        db.session.add(user)
        db.session.commit()
        print("done")
        msg = 'You have successfully registered!'
    return render_template('register.html', msg=msg)

@socketio.on('message', namespace='/device_comms')
def handle_message(message):
    message_data = json.loads(message)
    mtype = message_data['type']
    if mtype == 'wifi_ect_data':
        secret = message_data['secret']
        wifi_uuid = message_data['uuid']
        dvice = Device.query.filter_by(uuid=secret).first()
        wifi = Wifi.query.filter_by(uuid=wifi_uuid).first()
        if dvice.user.username == wifi.user.username:
            wifi.ect = message_data['ect']
            db.session.commit()
        else:
            # do nothing
            pass

@app.route('/add_device', methods = ['POST'])
def add_device():
    user_identifier = request.cookies.get('user_identifier')
    user = User.query.filter_by(user_id=user_identifier).first()
    if user:
        if 'device_uuid' in request.form and 'device_name' in request.form:
            device_uuid = request.form['device_uuid']
            name = request.form['device_name']
            dvice = Device(name=name, uuid=device_uuid, user=user)
            db.session.add(dvice)
            db.session.commit()
        else:
                return { 'error': 'Invalid data' }
    else:
        return { 'msg': 'Not logged in' }

@app.route('/get_wifi_ect_for_app', methods = ['POST'])
def get_wifi_data():
    user_identifier = request.cookies.get('user_identifier')
    user = User.query.filter_by(user_id=user_identifier).first()
    if user:
        if 'wifi_uuid' in request.form:
            wifi_uuid = request.form['wifi_uuid']
            wifi = Wifi.query.filter_by(uuid=wifi_uuid).first()
            if wifi.user.username == user.username:
                return { 'wifi': wifi._asdict() }
            else:
                return { 'msg': 'You are not authorized to access data about this WiFi network' }
        else:
             return { 'error': 'Invalid data' }
    else:
        return { 'msg': 'Not logged in' }

@app.route('/forget_wifi_data', methods = [ 'POST' ])
def forget_wifi_data():
    pass

@app.route('/add_wifi_deets', methods= ['POST'])
def add_wifi_deets():
    user_identifier = request.cookies.get('user_identifier')
    user = User.query.filter_by(user_id=user_identifier).first()
    if user:
        if 'ssid' in request.form and 'password' in request.form:
            ssid = request.form['ssid']
            password = request.form['password']
            wifi_uuid = uuid.uuid4()
            wifi = Wifi(uuid=wifi_uuid, ssid=ssid, password=password, user=user)
            db.session.add(wifi)
            db.session.commit()
            emit(json.dumps({ 'type': 'wifi_data', 'uuid': wifi_uuid, 'ssid': ssid}), broadcast=True)
            return { 'msg': 'Added wifi device', 'uuid': wifi_uuid }
        else:
            return { 'error': 'Invalid data' }
    else:
        return { 'msg': 'Not logged in' }

if __name__ == "__main__":
    socketio.run(app)
    app.run(host='0.0.0.0',port=8000,allow_unsafe_werkzeug=True)
