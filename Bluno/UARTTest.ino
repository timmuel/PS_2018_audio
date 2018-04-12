#include <SoftwareSerial.h>

void setup() {
  Serial.begin(115200);  //initial the Serial
}

void loop() {
  String test;
  if (Serial.available())  {
    test = Serial.readString();//send what has been received
    Serial.print(test);//send what has been received
  }
}
