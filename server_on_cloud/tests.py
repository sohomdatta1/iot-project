import requests as r
import uuid
import json

ses = r.Session()
res = ses.post('http://0.0.0.0:8000/login', { 'username': 'Admin', 'password': 'Admin' })
print(res.text)
res = ses.post('http://0.0.0.0:8000/add_device', { 'device_name': 'Home_Scanner', 'device_uuid': str(uuid.uuid4()) })
print(res.text)
res = ses.post('http://0.0.0.0:8000/add_wifi_deets', { 'ssid': 'some_network_idk_wtf', 'password': 'pleasework' })
print(res.text)
res_json = json.loads(res.text)
res = ses.post('http://0.0.0.0:8000/get_wifi_ect_for_app', { 'wifi_uuid': res_json['uuid'] })
print(res.text)