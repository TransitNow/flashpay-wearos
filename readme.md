# Flashpay for Samsung Galaxy Watches and TicWatch Pro 3/5 users
<table>
<tr>
<td>

![2024![flashpayicon](https://github.com/TransitNow/flashpay-wearos/assets/2457368/bc0d1626-5b75-4024-bc83-e3a52b6eb742)
-01-08 21_17_14-ChatGPT](https://github.com/TransitNow/flashpay-wearos/assets/2457368/96135707-da36-484d-87ac-991b5948b980)
</td>
<td>
- Bind a <b>tactile button</b> to <b>Google Wallet</b> and <b>flashlight</b> for hands-free access
- Adds <b>haptic feedback</b> when launching shortcut so you know when you've successfully triggered the shortcut
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

Want to bring back **Google Assistant** for TWP3/5 users? Check out [my other app](https://github.com/TransitNow/voice-assistant-shortcut-wearos)

## How to setup
### For Samsung Galaxy Watch 4/5/6 users 
- Assign this app to the double-press tap feature under Settings -> Advanced -> Customize button -> Double press.

### For TicWatch Pro 3/5 users
- Press the recent app shortcut (top button for TWP5, bottom button for TWP3), you will be prompted to bind that shortcut to the default recent apps or this app. Select this app.

## How it works
- When there's ambient light (enough light to see), it opens Google Wallet.
- When it's dark, it turns on the flashlight.
- If you're in a dim environment and using the shortcut once incorrectly brings up the flashlight, you can quickly double press again to open Google Wallet.

## Why Samsung Pay sucks (a rant to Galaxy Watch users)
### Why don't I just bind the double press top button to Google Wallet?
Samsung limits users to only binding the long-press bottom button to Samsung Pay, preventing its use with other apps. Sure, you can bind Google Wallet to the double press top button, but then you lose the flexibility to use it for other functions, like activating the flashlight—a feature I find essential at night

### Why Samsung Pay sucks compared to Google Wallet
- Samsung Pay requires entering your PIN twice: first to unlock the watch and then to use the payment feature, which can be frustrating if you forget a step. In contrast, Google Wallet simplifies the process by only needing a single unlock, making transactions smoother.
- The performance of Samsung Pay can be inconsistent, with occasional NFC issues requiring you to reopen the app and retry. On the other hand, Google Wallet offers a more reliable experience with a 100% success rate when tapping on NFC terminal.
- Google Wallet offers access to loyalty cards (barcodes, QR codes, plane tickets, etc) while Samsung Pay does not.

## How to install (sideload) apks
### Wear OS app prerequisites
- Install [Google Wallet](https://play.google.com/store/apps/details?id=com.google.android.apps.walletnfcrel&hl=en&gl=US) for this app to work properly.

### Option 1: via phone
- Download the [Wear installer 2](https://www.reddit.com/r/WearOS/comments/u9hf2m/new_app_wear_installer_2_a_free_general_purpose/) app on your phone.
- Follow their instructions on how to sideload the Flashpay apk onto your watch.

### Option 2: via computer
See [instructions](https://github.com/TransitNow/flashpay-wearos/blob/main/docs/how-to-sideload-apks-with-adb-on-computer.md).

## How to uninstall or re-assign the recent apps shortcut to another app?
- For Galaxy Watch users, just re-customize your double-tap shortcut.
- For TWP3/5, if you want to re-assign/reset the recent apps shortcut, you will need to uninstall this app. There's no option to easily change the default app for the recent app shortcut (at least w/ TWP3).
- Uninstall directly on Wear OS watch Settings -> App Info -> Flashpay launcher -> Uninstall or run `adb uninstall com.jsyntax.nowtap`


## Beta Feedback
We welcome feedback on the beta version to improve the app. Please reach out with your comments and suggestions. 

Find my email on my [YouTube channel - https://www.youtube.com/@JSyntax](https://www.youtube.com/@JSyntax)  

## Support me by checking out my other apps
- [Cycle Now, a bike share app](https://cyclenowapp.com/) 
- [Transit Now, a TTC, MBTA, AC Transit bus predictions app](https://transitnowapp.com/)


