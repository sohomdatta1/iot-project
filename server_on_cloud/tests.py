import requests as r
import uuid
import json

our_uuid = '8fa4ba24-6b86-46ef-9e69-5c1018f275d9'

ses = r.Session()
res = ses.post('http://0.0.0.0:8000/login', { 'username': 'Admin', 'password': 'Admin' })
print(res.text)
#res = ses.post('http://0.0.0.0:8000/add_device', { 'device_name': 'Home_Scanner', 'device_uuid': our_uuid })
#print(res.text)
res = ses.post('http://0.0.0.0:8000/add_wifi_deets', { 'ssid': 'AndroidAP015f', 'password': '198hsf15' })
print(res.text)
res_json = json.loads(res.text)
while True:
    res = ses.post('http://0.0.0.0:8000/get_wifi_ect_for_app', { 'wifi_uuid': res_json['uuid'] })
    res_data = json.loads(res.text)
    if res_data['wifi']['ect'] != 'None':
        print(res_data)
        break