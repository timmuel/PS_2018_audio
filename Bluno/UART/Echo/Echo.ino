#include <SoftwareSerial.h>

// Using digital pins d2, d4 to emulate UART port

SoftwareSerial dspSerial(2,4);

void setup() {
  Serial.begin(115200);             // Init the Bluetooth / USB serial
  while (!Serial){ }                // Wait for Blutooth / USB serial to be rdy
  dspSerial.begin(4800);            // Start serial to dsp with rate 4800
}

void loop() {
  //Simple echo function (Read from USB, Write to uart, read from uart, write to usb)
  if(dspSerial.available()){
    Serial.write(Serial.read());
  }
  if(Serial.available()){
    dspSerial.write(Serial.read());
  }
}
