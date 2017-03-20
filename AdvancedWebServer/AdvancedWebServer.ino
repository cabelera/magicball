/*
 * Copyright (c) 2015, Majenko Technologies
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of Majenko Technologies nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <Servo.h> 


 
Servo myservo;  // create servo object to control a servo 
                // twelve servo objects can be created on most boards
                
//const char* ssid = "ACIDBURN";
//const char* password = "day06031988";

const char* ssid = "lsiamostra24";
const char* password = "lsiqazwsx";

ESP8266WebServer server ( 80 );

const int led = LED_BUILTIN;

void handleRoot() {

	char temp[400];
	int sec = millis() / 1000;
	int min = sec / 60;
	int hr = min / 60;

	snprintf ( temp, 400,

"<html>\
  <head>\
    <meta http-equiv='refresh' content='5'/>\
    <title>ESP8266 Demo</title>\
    <style>\
      body { background-color: #cccccc; font-family: Arial, Helvetica, Sans-Serif; Color: #000088; }\
    </style>\
  </head>\
  <body>\
    <h1>Hello from ESP8266!</h1>\
    <p>Uptime: %02d:%02d:%02d</p>\
    <img src=\"/test.svg\" />\
  </body>\
</html>",

		hr, min % 60, sec % 60
	);
	server.send ( 200, "text/html", temp );
}

void handleNotFound() {
	String message = "File Not Found\n\n";
  String luz = "off";
	message += "URI: ";
	message += server.uri();
	message += "\nMethod: ";
	message += ( server.method() == HTTP_GET ) ? "GET" : "POST";
	message += "\nArguments: ";
	message += server.args();
	message += "\n";

	for ( uint8_t i = 0; i < server.args(); i++ ) {
		message += " " + server.argName ( i ) + ": " + server.arg ( i ) + "\n";
	}

  for ( uint8_t i = 0; i < server.args(); i++ ) {
    if( server.argName ( i ) == "luz")
      luz = server.arg ( i );
  }

  if(luz == "on"){
    digitalWrite ( led, 1 );
  }else if(luz == "off"){
    digitalWrite ( led, 0 );
  }


	server.send ( 404, "text/plain", message );
}

void setup ( void ) {
	pinMode ( led, OUTPUT );
	Serial.begin ( 115200 );
  connect_wifi();
  init_OTA_OverTheAir();
  
	if ( MDNS.begin ( "esp8266" ) ) {
		Serial.println ( "MDNS responder started" );
	}

  server_start();
}

void loop ( void ) {
	server.handleClient();
}

void drawGraph() {
	String out = "";
	char temp[100];
	out += "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"400\" height=\"150\">\n";
 	out += "<rect width=\"400\" height=\"150\" fill=\"rgb(250, 230, 210)\" stroke-width=\"1\" stroke=\"rgb(0, 0, 0)\" />\n";
 	out += "<g stroke=\"black\">\n";
 	int y = rand() % 130;
 	for (int x = 10; x < 390; x+= 10) {
 		int y2 = rand() % 130;
 		sprintf(temp, "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke-width=\"1\" />\n", x, 140 - y, x + 10, 140 - y2);
 		out += temp;
 		y = y2;
 	}
	out += "</g>\n</svg>\n";

	server.send ( 200, "image/svg+xml", out);
}

void connect_wifi(void){
  //Inicia o WiFi
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  Serial.print("Conectando");
  //Loop até conectar no WiFi
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
 
  //Logs na porta serial
  Serial.println("");
  Serial.println("WiFi conectado!");
  Serial.print("Conectado na rede ");
  Serial.println(WiFi.SSID());
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
  Serial.print("Subnet Mask: ");
  Serial.println(WiFi.subnetMask());
  Serial.print("MAC Address: ");
  Serial.println(WiFi.macAddress());
  Serial.print("RSSI: ");
  Serial.println(WiFi.RSSI());
 
  digitalWrite(D6, HIGH);
  delay(2000);
}

void server_start(void){

  server.on ( "/", handleRoot );
  server.on ( "/test.svg", drawGraph );
  server.on ( "/inline", []() {
    server.send ( 200, "text/plain", "this works as well" );
  } );
  server.onNotFound ( handleNotFound );
  server.begin();
  Serial.println ( "HTTP server started" );
  
}

void init_OTA_OverTheAir(void){
  
  Serial.println("Booting");
 
  ArduinoOTA.setHostname("espota");
  // No authentication by default
  // ArduinoOTA.setPassword("admin");
 
  ArduinoOTA.onStart([]() {
    String type;
    if (ArduinoOTA.getCommand() == U_FLASH)
      type = "sketch";
    else // U_SPIFFS
      type = "filesystem";
 
    // NOTE: if updating SPIFFS this would be the place to unmount SPIFFS using SPIFFS.end()
    Serial.println("Start updating " + type);
  });
  ArduinoOTA.onEnd([]() {
    Serial.println("\nEnd");
  });
  ArduinoOTA.onProgress([](unsigned int progress, unsigned int total) {
    Serial.printf("Progress: %u%%\r", (progress / (total / 100)));
  });
  ArduinoOTA.onError([](ota_error_t error) {
    Serial.printf("Error[%u]: ", error);
    if (error == OTA_AUTH_ERROR) Serial.println("Auth Failed");
    else if (error == OTA_BEGIN_ERROR) Serial.println("Begin Failed");
    else if (error == OTA_CONNECT_ERROR) Serial.println("Connect Failed");
    else if (error == OTA_RECEIVE_ERROR) Serial.println("Receive Failed");
    else if (error == OTA_END_ERROR) Serial.println("End Failed");
  });
  ArduinoOTA.begin();
  Serial.println("Ready OTA ESP8266");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  
  
}

void start_servo(void){

  myservo.attach(2);  // attaches the servo on GIO2 to the servo object 
  int pos;

  for(pos = 0; pos <= 180; pos += 1) // goes from 0 degrees to 180 degrees 
  {                                  // in steps of 1 degree 
    myservo.write(pos);              // tell servo to go to position in variable 'pos' 
    delay(15);                       // waits 15ms for the servo to reach the position 
  } 
  for(pos = 180; pos>=0; pos-=1)     // goes from 180 degrees to 0 degrees 
  {                                
    myservo.write(pos);              // tell servo to go to position in variable 'pos' 
    delay(15);                       // waits 15ms for the servo to reach the position 
  } 

}

