import socket
import network
import time
import machine

CONTENT_PAGE = """\
HTTP/1.0 200 OK

<!DOCTYPE html>
<html>
<head> <title>ESP8266 CONTROLLER SETUP</title> </head>
<meta name="viewport" content="width=device-width, initial-scale=1,5">
<body style="max-width:500px">
<form action="/setup">
Fill:
<br>
<input type='text' name='ssid' placeholder="ssid"><br>
<input type='password' name='password' placeholder="password"><br>
<input type='text' name='room' placeholder="room name"><br>
<input type='text' name='controller' placeholder="controller name"><br><br>
<button name="send" value="SEND" type="submit">SEND</button>
</form>
</html>
"""
ERROR_MSG= """\
HTTP/1.0 200 OK

<!DOCTYPE html>
<html>
<head> <title>ESP8266 AP SETUP ERROR</title> </head>
<form>
ERROR DATA. WAIT AND TRY AGAIN
</form>
</html>
"""

SUCCESS_MSG= """\
HTTP/1.0 200 OK

<!DOCTYPE html>
<html>
<head> <title>ESP8266 AP SETUP ERROR</title> </head>
<form>
SUCCES. YOU CAN LEAVE NOW
</form>
</html>
"""

def start():
	ip=ap.ifconfig()[0]
	s = socket.socket()
	ai = socket.getaddrinfo(ip, 80)
	print("Web Server: Bind address info:", ai)
	addr = ai[0][-1]

	s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
	s.bind(addr)
	s.listen(1)
	s.settimeout(2)
	print("Web Server: Listening http://{}:80/".format(ip))
	try:
		while 1:
			try:
				res = s.accept()
				client_sock = res[0]
				client_stream = client_sock
				req = client_stream.readline()
				while True:
					h = client_stream.readline()
					if h == b"" or h == b"\r\n" or h == None:
						break
					print(h)
				req_url = req[4:-11] 
				api = req_url[:6]
				if api == b'/setup':
					request_string  = str(req_url)
					request = request_string .split('SEND')[0].split('?')[-1].split('&')
					if len(request) == 5:
						ssid = request[0].split('=',1)[1]
						password = request[1].split('=',1)[1]
						room = request[2].split('=',1)[1]
						controller = request[3].split('=',1)[1]
						if ssid and password and room and controller:
							save_connection_data(ssid,password,room,controller)
							client_stream.write(SUCCESS_MSG)
							client_stream.close()
							time.sleep(5)
							return True
						else:
							client_stream.write(ERROR_MSG)
							client_stream.close()
							time.sleep(5)
				client_stream.write(CONTENT_PAGE)
				client_stream.close()
			except:
				print("timeout for web... moving on...")

			time.sleep_ms(300)
	except KeyboardInterrupt:
		print('Closing')
	return True

def save_connection_data(client,password,room,controller):
	f = open('data.py','w')
	f.write('USER_client ='+ repr(client)+'\n')
	f.write('USER_password ='+ repr(password)+'\n') 
	f.write('USER_room ='+ repr(room)+'\n')
	f.write('USER_controller ='+ repr(controller)+'\n')
	f.close()

ap = network.WLAN(network.AP_IF)
ap.active(True)
ap.config(essid="HEAT CONTROLLER", password="bajabongo", authmode=4) #authmode=1 == no pass




