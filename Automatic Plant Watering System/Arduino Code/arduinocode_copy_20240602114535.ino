#include <LiquidCrystal.h>

const int BuzzerPin = 10;
const int LedRed = 12;
const int LedGreen = 11;
const int SoilMoistureSensor = A0;
const int WaterPump = 13;

LiquidCrystal lcd(2, 3, 4, 5, 6, 7);

bool pumpForcedOn = false; // To keep track if the pump is manually forced on
bool pumpState = false;    // To keep track of the pump's actual state

void setup() {
  pinMode(WaterPump, OUTPUT);
  pinMode(LedRed, OUTPUT);
  pinMode(LedGreen, OUTPUT);
  pinMode(BuzzerPin, OUTPUT);
  Serial.begin(9600);

  lcd.begin(16, 2);
  pinMode(BuzzerPin, OUTPUT);

  String message1 = "Welcome";
  String message2 = "Calcutta";
  String message11 = "Institute of Tec.";

  lcd.clear();
  lcd.setCursor(4, 0);
  for (int i = 0; i < message1.length(); i++) {
    lcd.print(message1.charAt(i));
    delay(100);
  }
  lcd.clear();
  lcd.setCursor(4, 0);
  for (int i = 0; i < message2.length(); i++) {
    lcd.print(message2.charAt(i));
    delay(100);
  }

  lcd.setCursor(0, 1);
  for (int i = 0; i < message11.length(); i++) {
    lcd.print(message11.charAt(i));
    delay(100);
  }

  lcd.clear();
  lcd.setCursor(0, 0);

  String message3 = "Automated Plant";
  String message4 = "Watering System";

  for (int i = 0; i < message3.length(); i++) {
    lcd.print(message3.charAt(i));
    delay(100);
  }
  lcd.setCursor(0, 1);
  for (int i = 0; i < message4.length(); i++) {
    lcd.print(message4.charAt(i));
    delay(100);
  }
  delay(100);
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Moisture = ");
  lcd.setCursor(0, 1);
  lcd.print("Water Pump = ");
}

void loop() {
  int Sensor = analogRead(SoilMoistureSensor);
  int mappedValue = map(Sensor, 0, 876, 0, 99);
  Serial.println(mappedValue);
  //delay(3000);

  lcd.setCursor(11, 0);
  lcd.print(mappedValue);
  lcd.setCursor(14, 0);
  lcd.print("%");
  lcd.setCursor(13, 1);

  if (mappedValue > 45) {
    pumpState = true; // Automatically turn on the pump
  } else if (!pumpForcedOn) {
    pumpState = false; // Automatically turn off the pump if not forced on
  }

  if (pumpState || pumpForcedOn) {
    digitalWrite(WaterPump, HIGH);
    digitalWrite(LedGreen, HIGH);
    digitalWrite(LedRed, LOW);
lcd.print(" ON");
    playSound();
  } else {
    digitalWrite(WaterPump, LOW);
    digitalWrite(LedGreen, LOW);
    digitalWrite(LedRed, HIGH);
    lcd.print("OFF");
  }

  if (Serial.available() > 0) {
    String pumpForce = Serial.readStringUntil('\n');
    Serial.println(pumpForce);

    if (pumpForce == "on") {
      pumpForcedOn = true;
      pumpState = true; // Force pump on
    } else if (pumpForce == "off") {
      pumpForcedOn = false;
      pumpState = (mappedValue > 45); // Revert to automatic control based on moisture level
    }
  }
}

void playSound() {
  tone(BuzzerPin, 2500, 100);
  delay(1000);
}
