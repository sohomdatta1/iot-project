import redis
import requests
import time

device_secret = '8fa4ba24-6b86-46ef-9e69-5c1018f275d9'
r = requests.Session()
rdis = redis.Redis(host='localhost', port=6379, db=0)

import socketio

import json

sio = socketio.Client()

def perform_krack(ssid, password):
    pass

@sio.event
def connect():
    print("I'm connected!")

@sio.event
def connect_error(data):
    print("The connection failed!")

@sio.event
def disconnect():
    print("I'm disconnected!")

@sio.on('message', namespace='/device_comms')
def on_message(message):
    print(message)
    message_data = json.loads(message)
    if message_data['type'] == 'wifi_data':
        ssid = message_data['ssid']
        uuid = message_data['uuid']
        if rdis.exists(ssid):
            ect = rdis.get(ssid)
            sio.send(json.dumps({'type': 'krack_data_req', 'uuid': uuid, 'secret': device_secret}), namespace='/device_comms')
            sio.send(json.dumps({'type': 'wifi_ect_data', 'uuid': uuid, 'ect': ect.decode(), 'secret': device_secret}), namespace='/device_comms')
    elif message_data['type'] == 'krack_data':
        ssid = message_data['ssid']
        password = message_data['password']
        uuid = message_data['uuid']
        result = perform_krack(ssid, password)
        sio.send(json.dumps({'type': 'krack_data_send', 'uuid': uuid, 'result': result}))

@sio.on('*')
def catch_all(event, data):
    print(event, data)

sio.connect("http://0.0.0.0:8000", namespaces=['/device_comms'])
