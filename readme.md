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
## How it works
- When there's ambient light (enough light to see), it opens Google Wallet.
- When it's dark, it turns on the flashlight.
- If you're in a dim environment and using the shortcut once incorrectly brings up the flashlight, you can quickly double press again to open Google Wallet.
<img src="https://github.com/TransitNow/flashpay-wearos/assets/2457368/2465877c-63a2-43ab-b492-f3c494b0c62a" alt="image" width="300"/>

<img src="https://github.com/TransitNow/flashpay-wearos/assets/2457368/82ef1e3a-f9bb-476c-8c81-3ac15ed31f5f" alt="image" width="300"/>



## How to setup
### For Samsung Galaxy Watch 4/5/6 users 
- Assign this app to the double-press tap feature under Settings -> Advanced -> Customize button -> Double press.
<img src="https://github.com/TransitNow/flashpay-wearos/assets/2457368/324ab880-2cbb-4c9c-a976-042e5147ec2a" alt="image" width="300"/>


### For TicWatch Pro 3/5 users
- Press the recent app shortcut (top button for TWP5, bottom button for TWP3), you will be prompted to bind that shortcut to the default recent apps or this app. Select this app.

<img src="https://github.com/TransitNow/flashpay-wearos/assets/2457368/07901428-a050-4fbe-94f1-7dd9a0668cdf" alt="image" width="300"/>




## Why Samsung Pay sucks (a rant to Galaxy Watch users)
### Why don't I just bind the double press top button to Google Wallet?
Samsung limits users to only binding the long-press bottom button to Samsung Pay, preventing its use with other apps. Sure, you can bind Google Wallet to the double press top button, but then you lose the flexibility to use it for other functions, like activating the flashlight—a feature I find essential at night

### Why Google Wallet is better

| Feature | Google Wallet                                         | Samsung Pay |
| ------- |-------------------------------------------------------| ----------- |
| Unlocking Process | Single unlock when initially putting on watch         | Requires PIN twice: once to unlock the watch, again for payment |
| NFC Reliability | High reliability with 100% success rate               | Occasional NFC issues, may need to reopen app and retry |
| Access to Loyalty Cards & Tickets | Supports loyalty cards, QR codes, plane tickets, etc. | Does not offer access to loyalty cards or similar features |


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

## Will you release this on the Google Play Store?
It depends if Google will approve. Ever since Wear OS 4, Google has imposed strict rules on what Wear OS apps get approved. Since this app is a utility app, it doens't fit in the mold of typical apps. I will update this page if I do release it, but for now, it's quite easy and safe to sideload it.

## Beta Feedback
We welcome feedback on the beta version to improve the app. Please reach out with your comments and suggestions. 

Find my email on my [YouTube channel - https://www.youtube.com/@JSyntax](https://www.youtube.com/@JSyntax)  

## Support me by checking out my other apps
- [Cycle Now, a bike share app](https://cyclenowapp.com/) 
- [Transit Now, a TTC, MBTA, AC Transit bus predictions app](https://transitnowapp.com/)


