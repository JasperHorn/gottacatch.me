#include "TheThingsUno.h"

#define debugSerial Serial
#define loraSerial Serial1

#define CLOCK_PIN 8
#define DATA_PIN 9

#define NO_CODE 0
#define RIGHT_CODE 1
#define WRONG_CODE 2


// Set your app Credentials
const byte appEui[8] = { 0x70, 0xB3, 0xD5, 0x7E, 0xD0, 0x00, 0x01, 0xF2 }; //for example: {0x70, 0xB3, 0xD5, 0x7E, 0xE0, 0xE0, 0x01, 0x4A1}
const byte appKey[16] = { 0x26, 0x6B, 0x9F, 0x5B, 0x3F, 0x42, 0x81, 0x79, 0x0D, 0x28, 0xD2, 0xAA, 0xF6, 0x96, 0x03, 0xFA }; //for example: {0x73, 0x6D, 0x24, 0xD2, 0x69, 0xBE, 0xE3, 0xAE, 0x0E, 0xCE, 0xF0, 0xBB, 0x6C, 0xA4, 0xBA, 0xFE}
TheThingsUno ttu;

unsigned long keypresstime;

void setup() 
{
//  Serial.begin(9600);
  pinMode(CLOCK_PIN, OUTPUT);
  pinMode(DATA_PIN, INPUT);
  digitalWrite(CLOCK_PIN, HIGH);

  keypresstime = millis();

  debugSerial.begin(115200);
  loraSerial.begin(57600);

  ttu.init(loraSerial, debugSerial); //Initializing...
  ttu.reset();
  ttu.join(appEui, appKey);

  delay(6000);
  ttu.showStatus();
  debugSerial.println("Setup for The Things Network complete");

  delay(1000);
}

int last_but = 0;
int code_entered[4];
int current_num = 0;
int pincode[] = { 1, 2, 3, 4 };

int get_button()
{
  int but_pressed = 0;
  
  for (int button = 1; button < 9; button++)
  {
      digitalWrite(CLOCK_PIN, LOW);
      int databit = digitalRead(DATA_PIN);
      digitalWrite(CLOCK_PIN, HIGH);

      if (!databit) but_pressed = button;
  };

  return but_pressed;
}

int isCodeEntered()
{
  int but = get_button();
  int result = NO_CODE;
  
  if (but == last_but) return result;

  if (but == 0)
  {
    if (current_num == 4)
    {
      if (code_entered[0] == pincode[0] &&
          code_entered[1] == pincode[1] &&
          code_entered[2] == pincode[2] &&
          code_entered[3] == pincode[3])
        result = RIGHT_CODE;
      else
        result = WRONG_CODE;

      current_num = 0;
      return result;
    }
  }

  if (last_but == 0)
  {
    Serial.print(but);
    code_entered[current_num] = but;
    current_num++;
  }
  
  last_but = but;

  return NO_CODE;
}

void loop() {
  int result = isCodeEntered();
  if (result != NO_CODE) {
    Serial.print("   -> ");
    Serial.println(result);

    byte msg[] = {0xEE, 0x00};
    if (result == RIGHT_CODE)
      msg[1] = 0x01;
    else
      msg[1] = 0x00;

    ttu.sendBytes(msg, 2);
  }
  delay(10);
}
