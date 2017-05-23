import time
import machine
import onewire, ds18x20
import http_client2
import ntptime
import utime
import os
import captive

def readTemp():
	# the device is on GPIO12
	dat = machine.Pin(2)
	# create the onewire object
	ds = ds18x20.DS18X20(onewire.OneWire(dat))
	# scan for devices on the bus
	roms = ds.scan()
	print('found devices:', roms)
	ds.convert_temp()
	time.sleep_ms(750)
	for rom in roms:
		return ds.read_temp(rom)

def perform_connection():
	print("performing connection")
	import network
	sta_if = network.WLAN(network.STA_IF)
	ap_if = network.WLAN(network.AP_IF)
	ap_if.active(False)
	if not sta_if.isconnected():
		sta_if.active(True)
		import data
		sta_if.connect(data.USER_client,data.USER_password)
		while not sta_if.isconnected():
			time.sleep(1)

def erese_data():
	import os
	os.remove('data.py')

def perform_task():
	print("performing task")
	post = True
	while True:
		ts = utime.localtime()
		if post and ts[4] == 00 or ts[4] == 30:
			temperature = readTemp()
			import data
			r = http_client2.post('http://192.168.0.65/Control/postTemp.php',json={'ROOM': data.USER_room,'TEMPERATURE': temperature ,'CONTROLLER': data.USER_controller})
			print(r.status_code)
			print(r.text)
			r = None
			temperature = None
			post = False
		time.sleep(1)
		import machine
		button = machine.Pin(12, machine.Pin.IN, machine.Pin.PULL_UP)
		if not button.value():
			erese_data()
			import machine
			machine.reset()
		if ts[4] != 00 or ts[4] != 30:
			post = True;
			

dirContentTable = os.listdir()
if not any('data.py' in s for s in dirContentTable):
	print("no data")
	while not captive.start():
		time.sleep(1)	
perform_connection()
ntptime.settime()
perform_task()


print("succesfully connected")



