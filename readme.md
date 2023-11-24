# Flashpay for Samsung Galaxy Watches

## TLDR
This app provides Samsung Galaxy Watch 4/5/6 users with a quick method to access Google Wallet and a flashlight app by using the native double-tap shortcut. 

## Why don't I just bind double-tap top button to Google Wallet?
Unfortunately, Samsung has restricted their users from binding long-press bottom button to Google Wallet.
Now, yes, you can simply bind the double-tap top button to Google Wallet, but that means you lose the ability to use the double-tap top button for other things, like flashlight, which is personally a critical feature for me in the night.

## Why Samsung Pay sucks and why Google Wallet is superior
- You must enter your PIN/pattern twice: once to unlock the watch, and once to use Samsung Pay, which is a huge pain. Google Wallet only requires you to unlock the watch once so it's way more seamless.
- It's flaky, sometimes the NFC doesn't work and you need re-open the app and try again. Google Wallet is 100% reliable.

## What Flashpay improves on
- Add haptic feedback whenever you double-tap the top button, so you know when you've successfully triggered the shortcut.
- Automatically open Google Wallet when you double-tap the top button in bright environments.
- Automatically open a flashlight app when you double-tap the top button in low light conditions.
  - Fun fact: Samsung does not allow users to bind the double-tap shortcut to their native Flash. This 3rd party app is more customizable and does not have this restriction.
- Double-tap twice to open your loyalty cards (e.g. Starbucks, Tim Hortons, etc.) via Stocard.


## How to install
### Option 1: via phone
- Download the [Wear2 installer](https://www.reddit.com/r/WearOS/comments/u9hf2m/new_app_wear_installer_2_a_free_general_purpose/) app on your phone.
- Follow the instructions to sideload the Flashpay apk onto your watch.

### Option 2: via computer
- Install ADB on your PC or Mac. [Instructions](https://www.xda-developers.com/install-adb-windows-macos-linux/)
- Download the Flashpay apk on to your computer.
- Connect your watch to your computer via USB
  - Enable developer mode on your watch (tap the build number 7 times)
  - Go to Settings > Developer Options > ADB Debugging
  - Enable ADB Debugging and debug via WIFI
  - First pair your watch using the `adb connect <ip address:port>` command
  - Then, `adb connect <ip address:port>`
  - Run `adb devices` should show your watch
- Run `adb install <path to apk>`

## Wear OS app prerequisites
Install these apps for this app to operate properly:
- [Google Wallet](https://play.google.com/store/apps/details?id=com.google.android.apps.walletnfcrel&hl=en&gl=US)
- [Stocard](https://play.google.com/store/apps/details?id=de.stocard.stocard&hl=en&gl=US)
- [Wear Flashlight](https://play.google.com/store/apps/details?id=com.codverter.wearflashlight&hl=en_CA&gl=US)


## Beta Feedback
We welcome feedback on the beta version to improve the app. Please reach out with your comments and suggestions.

## Support me by checking out my other apps
- (Cycle Now (bike share app))[https://cyclenowapp.com/] 
- (Transit Now (TTC, MBTA, AC Transit bus predictions app))[https://transitnowapp.com/] 


