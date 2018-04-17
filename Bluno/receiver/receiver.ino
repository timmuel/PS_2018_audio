#include <Wire.h>

void setup() {
  Wire.begin(8);
  Serial.begin(115200);
  Wire.onReceive(rec);
  Wire.onRequest(req);
}

void loop() {
  delay(100);
}

void rec() {
  char rec =  Wire.read();
  Wire.write(rec);
  Serial.print("request received Content: ");
  Serial.print(rec);
  Serial.println();
}

void req(){
  
}
