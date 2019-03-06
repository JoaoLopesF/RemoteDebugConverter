////////
// Example of Arduino code to convert Serial.print to RemoteDebug 
///////


#if defined ESP8266

// Includes of ESP8266

#include <ESP8266WiFi.h>

#elif defined ESP32

// Includes of ESP32

#include <WiFi.h>

#endif // ESP

// WiFi credentials

#define WIFI_SSID "..."  // your network SSID (name)
#define WIFI_PASS "..."  // your network key

/////// Variables

// Time

uint32_t mTimeToSec = 0;

uint8_t mRunSeconds = 0;
uint8_t mRunMinutes = 0;
uint8_t mRunHours = 0;

// Globals for example of debugger

boolean mBoolean = false;
char mChar = 'X';
byte mByte = 'Y';
int mInt = 1;
unsigned int mUInt = 2;
long mLong = 3;
unsigned long mULong = 4;
float mFloat = 5.0f;
double mDouble = 6.0;

String mString = "This is a string";
String mStringLarge = "This is a large stringggggggggggggggggggggggggggggggggggggggggggggg";

char mCharArray[] = "This is a char array";
char mCharArrayLarge[] = "This is a large char arrayyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy";

int mIntArray[5] = {1 ,2 ,3, 4, 5};

//const char mCharArrayConst[] = "This is const";

////// Setup

void setup() {

	// Initialize the Serial (use only in setup codes)

	Serial.begin(230400);

	// Buildin led

#ifdef LED_BUILTIN
    pinMode(LED_BUILTIN, OUTPUT);
    digitalWrite(LED_BUILTIN, LOW);
#endif

	// Connect WiFi

#ifdef WIFI_WIFI_SSID
	WiFi.begin(WIFI_SSID, WIFI_PASS);
#else
  WiFi.begin(); // Stored credentials
#endif

	Serial.println("");

	// Wait for connection
	while (WiFi.status() != WL_CONNECTED) {
		delay(500);
		Serial.print(".");
	}

	// End of setup - show IP

	Serial.println("");
	Serial.print("Connected to ");
	Serial.println(WiFi.SSID());
	Serial.print("IP address: ");
	Serial.println(WiFi.localIP());

}

void loop() {

	// Each second

	if (millis() >= mTimeToSec) {

		// Time

		mTimeToSec = millis() + 1000;

		// Count run time (just a test - for real suggest the TimeLib and NTP, if board have WiFi)

		mRunSeconds++;

		if (mRunSeconds == 60) {
			mRunMinutes++;
			mRunSeconds = 0;
		}
		if (mRunMinutes == 60) {
			mRunHours++;
			mRunMinutes = 0;
		}
		if (mRunHours == 24) {
			mRunHours = 0;
		}

		// Blink the led

#ifdef LED_BUILTIN
		digitalWrite(LED_BUILTIN, !digitalRead(LED_BUILTIN));
#endif

		// Debug the time 

		Serial.print("* Time: ");
		Serial.print(mRunSeconds);
		Serial.println(" seconds");

		if (mRunSeconds % 5 == 0) { // Each 5 seconds

			Serial.println("* This is a message of debug");

		}
	}

	// Give a time for ESP

	yield();

}


// Function example 

void foo() {

  uint8_t var = 1;

  Serial.print("this is a debug - var ");
	Serial.println(var);
  Serial.println("This is a println");
}

/////////// End
