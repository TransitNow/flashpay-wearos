# Flashpay for Samsung Galaxy Watches and TicWatch Pro 3/5 users
<table>
<tr>
<td>

![image](https://github.com/TransitNow/flashpay-wearos/assets/2457368/f929c520-443a-4539-8c60-f5ff520b0676)

</td>
<td>

- Bind a **ONE physical button** to **Google Wallet** **and flashlight** depending on the ambient light
- Adds **haptic feedback** when launching shortcut so you know when you've successfully triggered the shortcut.
- Allow TWP3/5 users to bind their Voice Assistant (long-press) shortcut to [Google Assistant Go](https://www.apkmirror.com/apk/google-inc/google-assistant-go/google-assistant-go-2-8-0-release/) [optional]

</td>
</tr>
</table>



<a href="https://www.youtube.com/watch?v=qyKzMmxhpaQ">
    <img src="https://img.youtube.com/vi/qyKzMmxhpaQ/maxresdefault.jpg" width="50%">
</a>

- [Watch video for Galaxy Watch users](https://www.youtube.com/watch?v=qyKzMmxhpaQ)
- [Watch video for TicWatch Pro 3/5 users](https://youtu.be/lqnD2iWeQBI)
- Download the Wear OS APK [here](https://github.com/TransitNow/flashpay-wearos/releases/)

## TLDR
This app provides Samsung Galaxy Watch 4/5/6 and TicWatch Pro 5/3 users with a quick method to access Google Wallet and a flashlight (in dark scenarios) by using the native double press shortcut or overriding "show recent apps".

## How to setup
### For Samsung Galaxy Watch 4/5/6 users 
- Assign this app to the double-press tap feature under Settings -> Advanced -> Customize button -> Double press.

### For TicWatch Pro 3/5 users
- Press the recent app shortcut (top button for TWP5, bottom button for TWP3), you will be prompted to bind that shortcut to the default recent apps or this app. Select this app. 
- Long-press the voice shortcut (crown button for TWP5, bottom button for TWP3), if you have multiple voice assistants installed (like Alexa), you will be prompted to select one. Select Google Assistant Go. If you don't have Google Assistant Go 2.8 installed, make sure to [side load it](https://www.apkmirror.com/apk/google-inc/google-assistant-go/google-assistant-go-2-8-0-release/).
  - Note: I actually prefer to use [Alexa for Wear OS](https://www.apkmirror.com/apk/amazon-mobile-llc/amazon-alexa-for-smart-watches-wear-os/) instead. Assistant Go is a little cumbersome since it was designed for a Phone. Unfortunately, you cannot sideload the official Assistant apk for Wear OS.
  - Note: Galaxy Watch cannot override the recent apps shortcut, so you will need to use the double press shortcut instead.
 

## How it works
- When there's ambient light (enough light to see), it opens Google Wallet.
- When it's dark, it turns on the flashlight.
- If you're in a dim environment and using the shortcut once incorrectly brings up the flashlight, you can quickly double press again to open Google Wallet.
- For TWP3/5 users, you will be presented with an option to bind the long-press voice button to Google Assistant Go (GAG) for a more seamless experience. 

# Why Samsung Pay sucks (a rant to Galaxy Watch users)
## Why don't I just bind the double press top button to Google Wallet?
Samsung limits users to only binding the long-press bottom button to Samsung Pay, preventing its use with other apps. Sure, you can bind Google Wallet to the double press top button, but then you lose the flexibility to use it for other functions, like activating the flashlight—a feature I find essential at night

## Why Samsung Pay sucks compared to Google Wallet
- Samsung Pay requires entering your PIN twice: first to unlock the watch and then to use the payment feature, which can be frustrating if you forget a step. In contrast, Google Wallet simplifies the process by only needing a single unlock, making transactions smoother.
- The performance of Samsung Pay can be inconsistent, with occasional NFC issues requiring you to reopen the app and retry. On the other hand, Google Wallet offers a more reliable experience with a 100% success rate when tapping on NFC terminal.
- Google Wallet offers access to loyalty cards (barcodes, QR codes, plane tickets, etc) while Samsung Pay does not.

## How to install
### Wear OS app prerequisites
- Install [Google Wallet](https://play.google.com/store/apps/details?id=com.google.android.apps.walletnfcrel&hl=en&gl=US) for this app to work properly.
- [OPTIONAL FOR TWP3/5] Install [Google Assistant Go](https://www.apkmirror.com/apk/google-inc/google-assistant-go/google-assistant-go-2-8-0-release/) for this app to work properly.

### Option 1: via phone
- Download the [Wear installer 2](https://www.reddit.com/r/WearOS/comments/u9hf2m/new_app_wear_installer_2_a_free_general_purpose/) app on your phone.
- Follow their instructions on how to sideload the Flashpay apk onto your watch.

### Option 2: via computer
- Install ADB on your PC or Mac. [Instructions](https://www.xda-developers.com/install-adb-windows-macos-linux/) or use my [quick guide](https://github.com/TransitNow/flashpay-wearos/blob/main/install-adb-quick-guide)
- Download the Flashpay apk on to your computer.
- Connect your watch to your computer via USB
  - Enable developer mode on your watch
      - Tap 'System' -> 'About'.
      - Scroll to 'Software Version' and tap it 5 times (for Galaxy Watch) until you see a message that you are now a developer.
  - Go to Settings > Developer Options > ADB Debugging
  - Enable ADB Debugging and debug via WIFI
  - First pair your watch using the `adb connect <ip address:port>` command
  - Then, `adb connect <ip address:port>`
  - Run `adb devices` should show your watch
- Run `adb install <path to apk>`


## Beta Feedback
We welcome feedback on the beta version to improve the app. Please reach out with your comments and suggestions. 

Find my email on my [YouTube channel - https://www.youtube.com/@JSyntax](https://www.youtube.com/@JSyntax)  

## Support me by checking out my other apps
- [Cycle Now, a bike share app](https://cyclenowapp.com/) 
- [Transit Now, a TTC, MBTA, AC Transit bus predictions app](https://transitnowapp.com/)


