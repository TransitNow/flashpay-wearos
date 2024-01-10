# Common ADB (Android Debug Bridge) Commands

ADB (Android Debug Bridge) is a versatile command-line tool that lets you communicate with a device. Here are some common commands which you can use.

## DPI Related Commands

**Display Current DPI**: This command will show the current DPI. The default DPI is usually around 340.  
Example: `adb shell wm density`

**Change DPI**: You can change the DPI using this command. For example, you can try 225.  
Example: `adb shell wm density 225`

**Reset DPI to Default**: This command will reset the DPI back to its default value.  
Command: `adb shell wm density reset`  

## System Related Commands

**Close System Dialogs**: This command will close system dialogs.  
Command: `adb shell am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS`  

**Connect to a Device**: Connect to a device with IP 10.0.0.X  
Command: `adb connect 10.0.0...`  

## Package Related Commands
**Install a Specific APK**: You can install a specific APK using its file path.  
Command: `adb install "<file_path>"`  
Example: `adb install "..\\wear os apks\\com.facebook.mlite_338.0.0.3.102-436638495_minAPI14(armeabi-v7a)(120,160dpi)_apkmirror.com.apk"`

**List All Packages**: This command will list all the installed packages.  
Example: `adb shell pm list packages -3`
The `-3` option will list only the third-party packages (like the ones you installed.)

**Uninstall a Specific Package**: You can uninstall any installed package using its package name.  
Command: `adb uninstall com <package_id>`  
Example: `adb uninstall com.facebook.mlite`

**Display Version of a Specific Package**: This command will display the version of a specific package.  
Command: `adb shell dumpsys package <package_name> | findstr versionName`  
Example: `adb shell dumpsys package com.google.android.apps.searchlite | findstr versionName`


## Input Related Commands
**Input Text**: This command is useful when signing into apps where you need to enter your name and password, such as Messenger Lite. It helps you input text on the device.  
Command: `adb shell input text '<text>'`  
Example: `adb shell input text 'yourlongemailusername@gmail.com'`
