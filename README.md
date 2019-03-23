# RemoteDebug Library for Arduino - source converter

Converter for RemoteDebug. Convert standard Serial.prints to RemoteDebug library


<a href="#releases">![build badge](https://img.shields.io/badge/version-v0.1.0-blue.svg)</a> 
<a href="https://github.com/JoaoLopesF/RemoteDebugConverter/blob/master/LICENSE.txt">
![GitHub](https://img.shields.io/github/license/mashape/apistatus.svg)</a>

This Java program is a converter, to help to migrate your Arduino codes, that not uses the __RemoteDebug__ library,
to add __RemoteDebug__ codes, as initializations and convert Serial.print commands to __RemoteDebug__.

Note: it is a __beta code__, if you have any problem or suggestion, please add an issue.


## Contents

- [About](#about)
- [Github](#github)
- [Beta version](#beta-version)
- [How it looks](#how-it-looks)
- [Usage](#usage)
- [Converter](#converter)
- [Releases](#releases)

## About

__RemoteDebug__ is a library for Arduino to debug devices over WiFi (telnet) with Print commands like Serial Monitor.

- [RemoteDebug library](https://github.com/JoaoLopesF/RemoteDebug)

This converter is to help migrate your codes to use __RemoteDebug__ library

This program do:

- Put #include for __RemoteDebug__ library
- Convert Serial.print commands to __RemoteDebug__ debug macros.
- Save the source files in separate diretory, appending _RDbg in names
- Open the directory converted, into a system explorer,
  so You can open this in Arduino

## Beta version

This is a beta version. 
Not yet fully tested, optimized, and documented.

Note:  

- It not yet converting to use [RemoteDebugger library](https://github.com/JoaoLopesF/RemoteDebugger).
- If you want add debugger in your codes, after this first conversion, run this converter:  
  please access: [RemoteDebuggerConverter](https://github.com/JoaoLopesF/RemoteDebuggerConverter)
  
## Github

Contribute to this libray development by creating an account on GitHub.

Please give a star, if you find this library usefull, 
this help a another people, discover it too.

Please add a issue for problems or suggestion.

I suggest you use a Github Desktop New app to clone, 
it help to keep updated.


## How it looks

![Screenshot 1](https://github.com/JoaoLopesF/RemoteDebugConverter/blob/master/screenshots/screenshot1.png)
![Screenshot 2](https://github.com/JoaoLopesF/RemoteDebugConverter/blob/master/screenshots/screenshot2.png)
![Screenshot 3](https://github.com/JoaoLopesF/RemoteDebugConverter/blob/master/screenshots/screenshot3.png)
![Screenshot 4](https://github.com/JoaoLopesF/RemoteDebugConverter/blob/master/screenshots/screenshot4.png)
![Screenshot 5](https://github.com/JoaoLopesF/RemoteDebugConverter/blob/master/screenshots/screenshot5.png)
![Screenshot 6](https://github.com/JoaoLopesF/RemoteDebugConverter/blob/master/screenshots/screenshot6.png)

## Usage

This runs in MacOsx, Linux and Windows (and other supported by Java).

You can use this converter in 2 modes:

- Java project

  If you is a Java developer:

  - Just download or clone this repository.

  - And open project a Java IDE, as Eclipse.

- Binary release (needs Java runtime)

  - You can download binary release of this converter

  Latest binary release: [Jar runnable - release v0.1.0](https://github.com/JoaoLopesF/RemoteDebugConverter/releases/download/v0.1.0/RemoteDebugConverter.jar.zip)

  - Uncompress the Zip file,
  - And run the jar file

## Converter

About the converter:

Attention, the converter not convert several prints to a single command,
for one command that uses printf formatter to show messages,
like debug* and rdebug*

If your code not yet using the powerfull printf,
dont worry, the converter will do the conversion.

For example:

```cpp

Serial.print("* Time: ");
Serial.print(mRunSeconds);
Serial.print(" seconds");

```

Will converted to a macros rprint*, create to be used in this converter

```cpp

rprintD("* Time: ");
rprintD(mRunSeconds);
rprintDln(" seconds");

```

Note: this mode is to allow a fast e easy convertion,
but some features as auto func is will not work.

But, if your code still use printf sintax:

```cpp

Serial.printf("* Time: %u seconds\n", mRunSeconds);

```

Will converted a single command, with all features working:

```cpp

rdebugD("* Time: %u seconds\n", mRunSeconds);

```

Note: the logic to convert automaticaly serial prints in one printf is very hard work.
For while, if you want all features, must convert all code to singles printf, before conversion.
Or, use converter even so, not all features. 
Dont worry, Migration to simple commands can be done in the future:

```cpp

rprintD("* Time: ");
rprintD(mRunSeconds);
rprintDln(" seconds");

```

 To:

```cpp

// Better -> one command to the job

rdebugD("* Time: %u seconds\n", mRunSeconds);

// Or (same sintax os SerialDebug):

debugD("* Time: %u seconds", mRunSeconds);

```

### TO-DO after conversion

One of most important features of RemoteDebug,
is the debug levels, as have in modern platforms,
as Android, iOS, javascript, and ESP-IDF.

Debug levels in order of importance (high -> less debug output):

- __Error__:    Critical errors
- __Always__:   Important messages
- __Warning__:  Error conditions but not critical
- __Info__:     Information messages
- __Debug__:    Extra information
- __Verbose__:  More information than the usual  

The converter will convert all debug to same level,
as debug. Your work (suggested) is redefine the debug levels,
according of the list above.

The capitalize character on suffix of debug macros,
is the level of this.

- Error (debugE, rprint*E, ..)
- Always (debugA, rprint*A, ..)
- Warning (debugW, rprint*W, ..)
- debug (debugD, rprint*D, ..)
- Verbose (debugV, rprint*V, ..)

So, suggest you open the code converted,
and change the suffix of these debug macros.

For example:

```cpp

rprintD("occurs an error on processing");

///...

rprintD("some details ....................................");

rprintD("* Time: ");
rprintD(mRunSeconds);
rprintDln(" seconds");
````

Change to:

```cpp

rprintE("occurs an error on processing");

///...

rprintV("some details ....................................");

rprintV("* Time: ");
rprintV(mRunSeconds);
rprintVln(" seconds");

````

## Releases

### 0.1.0 - 2019-03-06

    - First beta

