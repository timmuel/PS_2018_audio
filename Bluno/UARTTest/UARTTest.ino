#include <SoftwareSerial.h>

SoftwareSerial mySerial(0,1);

void setup() {
  Serial.begin(115200);  //initial the Serial
}

mySerial.begin(4800);
mySerial.println("hello");

void loop() {
  String test;
  if (Serial.available())  {
    test = Serial.readString();//send what has been received
    Serial.print(test);//send what has been received
  }
  if(mySerial.available()){
    Serial.write(mySerial.read());
  }
}
