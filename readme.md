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
- If Google Wallet isn't installed, you get the menu instead of a blank screen.

### The flashlight's four controls
The screen becomes the light, and the face splits into four tap targets big enough to hit in the dark without looking. Colour, brightness and timer choices are remembered between uses.

| | Left | Right |
|---|---|---|
| **Top** | `WALLET` — leave the light and open Google Wallet | `COLOR` — white, or red to keep your night vision |
| **Bottom** | `BRIGHT` — five brightness steps | `TIMER` — 30s / 1m / 2m / 4m / 5m before auto-off |

Tapping `TIMER` restarts the countdown from whichever duration you land on, so you can extend the light without leaving it. Only that quadrant resets the timer — dimming the light at 4:59 elapsed still lets it expire a second later.

### Tapping the app icon
The icon opens a small menu (Flashlight / Google Wallet), so both are reachable without binding a button to anything.
<img src="https://github.com/TransitNow/flashpay-wearos/assets/2457368/2465877c-63a2-43ab-b492-f3c494b0c62a" alt="image" width="300"/>

<img src="https://github.com/TransitNow/flashpay-wearos/assets/2457368/82ef1e3a-f9bb-476c-8c81-3ac15ed31f5f" alt="image" width="300"/>



## How to setup
### For Samsung Galaxy Watch 4/5/6 users 
- Assign **"Flashpay Instant"** to the double-press tap feature under Settings -> Advanced -> Customize button -> Double press.
- Pick `Flashpay Instant`, **not** `Flashpay`. Samsung's button picker lists launcher entries, and plain `Flashpay` is the menu — binding that would cost you the one-press speed the app is for. `Flashpay Instant` is the trampoline that decides and acts in one frame.
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
- Uninstall directly on Wear OS watch Settings -> App Info -> Flashpay -> Uninstall or run `adb uninstall com.jsyntax.nowtap`

## Will you release this on the Google Play Store?
**Submitted 9 August 2026** — v1.1.0 is in review for the Wear OS form factor. Wear verdicts take anywhere from one to eight days; this page will say so when it's live.

Sideloading stays supported either way. The Play build and the APKs on the [releases page](https://github.com/TransitNow/flashpay-wearos/releases/) are signed with the same certificate, so you can move between the two without uninstalling.

Note for anyone still on v1.0.5 or earlier: those were signed with a key that has since been lost, so you'll need to uninstall before installing v1.0.6+. That break is one-time.

## Beta Feedback
We welcome feedback on the beta version to improve the app. Please reach out with your comments and suggestions. 

Find my email on my [YouTube channel - https://www.youtube.com/@JSyntax](https://www.youtube.com/@JSyntax)  

## Support me by checking out my other apps
- [Cycle Now, a bike share app](https://cyclenowapp.com/) 
- [Transit Now, a TTC, MBTA, AC Transit bus predictions app](https://transitnowapp.com/)


