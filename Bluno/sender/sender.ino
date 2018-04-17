#include <Wire.h>

void setup() {
  Wire.begin();
  Serial.begin(115200);
}

void loop() {
  while(Serial.available()){
    char inp = Serial.read();
    Wire.beginTransmission(8);
    Wire.write(inp);
    Wire.endTransmission();
    Serial.print("request sent \n");
  }
  while(Wire.available()){
    Serial.print("Answer received: ");
    Serial.println(Wire.read());
    Serial.println();
  }




  
  /*Wire.requestFrom(8,3);                    //Addres / number of bytes
  while(Wire.available()){
    char c = Wire.read();
    Serial.print(c);
  }*/
}
