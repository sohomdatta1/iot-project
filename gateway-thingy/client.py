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

@sio.on('message')
def on_message(ws, message):
    message_data = json.loads(message)
    if message_data['type'] == 'wifi_data':
        ssid = message_data['type']['ssid']
        uuid = message_data['type']['uuid']
        if rdis.exists(ssid):
            ect = rdis.get(ssid)
            ws.send(json.dumps({'type': 'krack_data_req', 'uuid': uuid, 'secret': device_secret}))
            ws.send(json.dumps({'type': 'wifi_ect_data', 'uuid': uuid, 'ect': ect, 'secret': device_secret}))
    elif message_data['type'] == 'krack_data':
        ssid = message_data['type']['ssid']
        password = message_data['type']['password']
        uuid = message_data['type']['uuid']
        result = perform_krack(ssid, password)
        ws.send(json.dumps(json.dumps({'type': 'krack_data_send', 'uuid': uuid, 'result': result})))

sio.connect("http://0.0.0.0:5000/device_comms")
