#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>

// Replace with your network credentials
#define WIFI_SSID "iPhone15"
#define WIFI_PASSWORD "12345678"

// Replace with your Firebase project credentials
#define FIREBASE_HOST "staus-6d41f-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "Q2i9WkceemTYi5riCPymHSf1jpHXymx0SzvWm7vO"

FirebaseData firebaseData;
FirebaseAuth auth;
FirebaseConfig config;

void setup() {
  Serial.begin(9600);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.println("Connected to Wi-Fi");

  config.host = FIREBASE_HOST;
  config.signer.tokens.legacy_token = FIREBASE_AUTH;

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {

  int mappedValue = 0;
  if (Serial.available() > 0) {
    String sensorValue = Serial.readStringUntil('\n');
    //Serial.println(sensorValue);
  
    mappedValue = sensorValue.toInt();
    //Serial.print("mapped value");
    //Serial.println(mappedValue);
  }
  Firebase.setInt(firebaseData, "/sensorValue" , mappedValue);

  


  // if (mappedValue > 45) {
    // Firebase.setString(firebaseData, "/pumpStatus", "on");
    // Firebase.setString(firebaseData, "/ledStatus", "on");}
  // } else {
  //   Firebase.setString(firebaseData, "/pumpStatus", "off");
  //   Firebase.setString(firebaseData, "/ledStatus", "off");
  // }

  if (Firebase.getString(firebaseData, "/pumpForce")) {
    if (firebaseData.dataType() == "string") {
      String value = firebaseData.stringData();
      Serial.print(value);
      Serial.print("\n");

      if (value == "on") {
        if (Firebase.setString(firebaseData, "/ledStatus", "on")) {
          // Successfully set ledStatus to on
        } else {
          // Failed to set ledStatus
        }

        if (Firebase.setString(firebaseData, "/pumpStatus", "on")) {
          // Successfully set pumpStatus to on
        } else {
          // Failed to set pumpStatus
        }
      } else if (value == "off") {
        if (Firebase.setString(firebaseData, "/ledStatus", "off")) {
          // Successfully set ledStatus to off
        } else {
          // Failed to set ledStatus
        }

        if (Firebase.setString(firebaseData, "/pumpStatus", "off")) {
          // Successfully set pumpStatus to off
        } else {
          // Failed to set pumpStatus
        } 
      }
    }
  } else {
    // Failed to get data
  }

 // delay(3000); // Delay to avoid overwhelming the server
}
