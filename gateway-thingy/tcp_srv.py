import socket
import redis

HOST = "0.0.0.0"  # Standard loopback interface address (localhost)
PORT = 65438  # Port to listen on (non-privileged ports are > 1023)

r = redis.Redis(host='localhost', port=6379, db=0)

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    conn, addr = s.accept()
    with conn:
        print(f"Connected by {addr}")
        while True:
            data = conn.recv(4096)
            if not data:
                break
            print(data)
            if data.startswith(b"Bitch"):
                continue
            dat_str = data.decode('utf-8').split('+')
            ssid = dat_str[0]
            encryption_type = dat_str[1]
            r.set(ssid, encryption_type)
            