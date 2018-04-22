#include <SoftwareSerial.h>

SoftwareSerial vUart(2, 4); // RX, TX


byte msg[3]; // 0:Length, 1:type, 2:data      // init variables
byte recv[2];

byte to_send = 0;
byte to_recv = 0;
byte total_send = 0;
byte total_recv = 0;
int HEADER_SIZE = 2;
bool new_message = false;


void setup() {                              //Start both serial connections
  Serial.begin(57600);
  while (!Serial) {
    ; //Wait for usb / bluetooth to connect
  }
  Serial.println("bluetoth Ready");
  vUart.begin(4800);
}


void loop() {
  if (Serial.available() && to_send == 0) {  // only receive new data if last msg sent
    char inp = Serial.read();
    bool found = false;

    if (inp == 'v') {                        // control volume
      Serial.println("Sending command: volume");
      msg[0] = 0x01;
      msg[1] = 0x01;
      msg[2] = 0x08;
      found = true;
    }

    if (inp == 'i') {                        // control input
      Serial.println("Sending command: input");
      msg[0] = 0x01;
      msg[1] = 0x02;
      msg[2] = 0x05;
      found = true;
    }

    if (found) {
      to_send = msg[0] + HEADER_SIZE;
      total_send = to_send;
    }
  }

  if (to_send != 0) {
    if (to_send == total_send) vUart.write(0xff);       // Send starting byte
    vUart.write(msg[total_send - to_send]);             // Send Message
    to_send--;
  }


  if (vUart.available()) {                  // Receive confirmation
    if (to_recv > 0) {
      recv[total_recv - to_recv] = vUart.read();
      to_recv--;
      if (to_recv == 0) {                  // last byte received
        Serial.print("confirmation Received: ");
        if (recv[0] == 0x01) {
          Serial.print("Set volume to ");
          Serial.print(recv[1]);
          Serial.print('\n');
        }
        if (recv[0] == 0x02) {
          Serial.print("Set Input to ");
          Serial.print(recv[1]);
          Serial.print('\n');
        }
      }
    }
    if (to_recv == 0) {
      if (new_message) {
        new_message = false;
        to_recv = vUart.read() + HEADER_SIZE - 1;
        total_recv = to_recv;
      } else {
        if (vUart.read() == 0xff) {
          Serial.println("receiving response");
          new_message = true;
        }
      }
    }
  }
}
