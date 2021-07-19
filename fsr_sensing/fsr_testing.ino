#include "FirebaseESP32.h"
#define SWAP 0 
#include <WiFi.h>
#define FIREBASE_HOST "lckbraille-e205d.firebaseio.com" // http달린거 빼고 적어야 됩니다.
#define FIREBASE_AUTH "n4hk1WgRM4c8q7XW0szM7VmxFWrWO3509Z9yAZYK" // 데이터베이스 비밀번호
const char* ssid = "KAU-Guest";
const char* password = "";
FirebaseData firebaseData;
FirebaseJson json;

unsigned long previousMillis = 0; 
const long interval = 5000; 

const int threshold = 2000;

//GPIO pin number
const int FSR1 =  36;//
const int FSR2 =  39;//
const int FSR3 =  34;
const int FSR4 =  35;
const int FSR5 =  32;//
const int FSR6 =  33;//

WiFiServer server(80);

//change value of FSR
int first_number = 0;
int second_number = 0;
int third_number = 0;
int fourth_number = 0;
int fifth_number = 0;
int sixth_number = 0;


void setup() { 
  Serial.begin(115200); 
  Serial.print("Connecting to "); 
  Serial.println(ssid); 
  WiFi.mode(WIFI_STA); 
  WiFi.begin(ssid, password); 
  while (WiFi.status() != WL_CONNECTED) { 
    delay(500); 
    Serial.print("."); 
    }
  Serial.println(""); 
  Serial.println("WiFi connected."); 
  Serial.println("IP address: "); 
  Serial.println(WiFi.localIP()); 
  server.begin();
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  Firebase.setMaxRetry(firebaseData, 3);
  Firebase.setMaxErrorQueue(firebaseData, 30);
  Firebase.enableClassicRequest(firebaseData, true);
}
int threshold_number(int number){
  if(number > threshold){
    number = 1;
  }
  else{
    number = 0;
  }
  return number;
}
void loop() {

  unsigned long currentMillis = millis(); 
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis; 
    json.set("/data",first_number);
    json.set("/data2",second_number);
    json.set("/data3",third_number);
    json.set("/data4",fourth_number);
    json.set("/data5",fifth_number);
    json.set("/data6",sixth_number);
    Firebase.updateNode(firebaseData,"/Sensor",json);
   }
//  // put your main code here, to run repeatedly:
   first_number = threshold_number(analogRead(FSR1));
   second_number = threshold_number(analogRead(FSR2));
   third_number = threshold_number(analogRead(FSR3));
   fourth_number = threshold_number(analogRead(FSR4));
   fifth_number = threshold_number(analogRead(FSR5));
   sixth_number = threshold_number(analogRead(FSR6));
//
//  first_number = analogRead(FSR1);
//  second_number = analogRead(FSR2);
//  third_number = analogRead(FSR3);
//  fourth_number = analogRead(FSR4);
//  fifth_number = analogRead(FSR5);
//  sixth_number = analogRead(FSR6);

  Serial.printf("fsr1: %d, fsr2: %d fsr3: %d, fsr4: %d fsr5: %d, fsr6: %d\n",first_number,second_number,third_number,fourth_number,fifth_number,sixth_number);

}
