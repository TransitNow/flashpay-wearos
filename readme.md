# Flashpay for Samsung Galaxy Watches
[![Video](https://img.youtube.com/vi/qyKzMmxhpaQ/maxresdefault.jpg)](https://www.youtube.com/watch?v=qyKzMmxhpaQ)
Download the Wear OS APK [here](https://github.com/TransitNow/flashpay-wearos/releases/download/v1.0.0/flashpay-wearos-v001.apk)

## TLDR
This app provides Samsung Galaxy Watch 4/5/6 users with a quick method to access Google Wallet and a flashlight app by using the native double-tap shortcut. 

## Why don't I just bind the double-tap top button to Google Wallet?
Samsung limits users to only binding the long-press bottom button to Samsung Pay, preventing its use with other apps. Sure, you can bind Google Wallet to the double-tap top button, but then you lose the flexibility to use it for other functions, like activating the flashlight—a feature I find essential at night

## Why Samsung Pay sucks compared to Google Wallet
- Samsung Pay requires entering your PIN twice: first to unlock the watch and then to use the payment feature, which can be frustrating if you forget a step. In contrast, Google Wallet simplifies the process by only needing a single unlock, making transactions smoother.
- The performance of Samsung Pay can be inconsistent, with occasional NFC issues requiring you to reopen the app and retry. On the other hand, Google Wallet offers a more reliable experience with a 100% success rate.

## What Flashpay improves on
- Add haptic feedback whenever you double-tap the top button, so you know when you've successfully triggered the shortcut.
- When there's ambient light (enough light to see), it opens Google Wallet.
- When it's dark, it turns on the flashlight.
  - Fun fact: Samsung does not allow users to bind the double-tap shortcut to their native flashlight app. Fortunately, the suggested 3rd party flashlight app is more customizable and does not have this restriction.
- Double-tap twice to open your loyalty cards (e.g. Starbucks, Tim Hortons, etc.) via Stocard.


## How to install
### Option 1: via phone
- Download the [Wear2 installer](https://www.reddit.com/r/WearOS/comments/u9hf2m/new_app_wear_installer_2_a_free_general_purpose/) app on your phone.
- Follow the instructions to sideload the Flashpay apk onto your watch.

### Option 2: via computer
- Install ADB on your PC or Mac. [Instructions](https://www.xda-developers.com/install-adb-windows-macos-linux/) or use my [quick guide](https://github.com/TransitNow/flashpay-wearos/blob/main/install-adb-quick-guide)
- Download the Flashpay apk on to your computer.
- Connect your watch to your computer via USB
  - Enable developer mode on your watch
      - Tap 'System' -> 'About'.
      - Scroll to 'Software Version' and tap it 5 times (for Galaxy Watch 4/5/6) until you see a message that you are now a developer.
  - Go to Settings > Developer Options > ADB Debugging
  - Enable ADB Debugging and debug via WIFI
  - First pair your watch using the `adb connect <ip address:port>` command
  - Then, `adb connect <ip address:port>`
  - Run `adb devices` should show your watch
- Run `adb install <path to apk>`

## Wear OS app prerequisites
Install these apps for this app to operate properly:
- [Google Wallet](https://play.google.com/store/apps/details?id=com.google.android.apps.walletnfcrel&hl=en&gl=US)
- [Wear Flashlight](https://play.google.com/store/apps/details?id=com.codverter.wearflashlight&hl=en_CA&gl=US)
- [Stocard](https://play.google.com/store/apps/details?id=de.stocard.stocard&hl=en&gl=US) [optional]


## Beta Feedback
We welcome feedback on the beta version to improve the app. Please reach out with your comments and suggestions. 

Find my email on my [YouTube channel - https://www.youtube.com/@JSyntax](https://www.youtube.com/@JSyntax)  

## Support me by checking out my other apps
- [Cycle Now, a bike share app](https://cyclenowapp.com/) 
- [Transit Now, a TTC, MBTA, AC Transit bus predictions app](https://transitnowapp.com/)


