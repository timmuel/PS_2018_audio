#include <SoftwareSerial.h>

SoftwareSerial vUart(2, 4); // RX, TX


byte msg[3];                                             // init variables
byte recv[2];

byte to_send = 0;
byte to_recv = 0;
byte total_send = 0;
byte total_recv = 0;
int HEADER_SIZE = 2;
bool new_message = false;


void setup() {                                        // Start both serial connections
  Serial.begin(57600);
  while (!Serial) {
    ; //Wait for usb / bluetooth to connect
  }
  Serial.println("dsp Ready");
  vUart.begin(4800);
}

void loop() {
  if (vUart.available() && to_send == 0) {            // reveive from uart, only receive if msg sent
    if (to_recv == 0 && !new_message) {               // listen for starting bit 0xff
      byte starting = vUart.read();
      if (starting == 0xff) {
        Serial.println("received starting byte");
        new_message = true;
      }
    }
    if (vUart.available() && to_recv == 0 && new_message) {
      byte total_size = vUart.read();
      new_message = false;
      Serial.print("receiving ");
      Serial.print(total_size);
      Serial.println(" bytes");
      to_recv = total_size + HEADER_SIZE - 1;
      total_recv = to_recv;
    }
    if (vUart.available() && to_recv > 0) {
      recv[total_recv - to_recv] = vUart.read();
      to_recv--;
      if (to_recv == 0) {                             // last byte received
        Serial.print("Command Received: id=");
        Serial.print( recv[0]);
        if (recv[0] == 0x01) {
          Serial.print(" Set volume to ");
          Serial.print(recv[1]);
          Serial.print('\n');
        }
        if (recv[0] == 0x02) {
          Serial.print(" Set Input to ");
          Serial.print(recv[1]);
          Serial.print('\n');
        }
        to_send = total_recv+1;
        total_send = to_send;
      }
    }
  }

  if (to_send != 0) {
    if (to_send == total_send) {                    // first byte
      Serial.println("sending response");
      vUart.write(0xff);                            // Starting byte
      vUart.write(0x01);                            // length
    }
    else{
      vUart.write(recv[total_send - to_send-1]);
    }
    to_send--;
  }
}
