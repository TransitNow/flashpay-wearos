# ADB Installation and Wear OS Watch Pairing Guide

## ADB Installation

### Windows

1. **Download ADB Platform Tools:**
   - Visit [Google's developer site](https://developer.android.com/studio/releases/platform-tools) and download the latest SDK Platform-Tools for Windows.

2. **Extract Files:**
   - Right-click the downloaded ZIP file and choose "Extract All". Extract to a folder you can easily access (e.g., `C:\adb`).

3. **Add ADB to PATH:**
   - Right-click on 'This PC' or 'My Computer', choose 'Properties'.
   - Go to 'Advanced system settings' -> 'Environment Variables'.
   - Under 'System Variables', select 'Path' and click 'Edit'.
   - Add the path to your ADB folder (e.g., `C:\adb`).
   - Click 'OK' to close the dialogs.

### Mac

1. **Install Homebrew:**
   - Open Terminal and run `/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"`.

2. **Install ADB:**
   - After Homebrew installation, run `brew install android-platform-tools` in the Terminal.

## Pairing Wear OS Watch (For Galaxy Watch 4/5/6)

### Enable Debug Mode on Wear OS

1. **Access Settings on Watch:**
   - Go to the 'Settings' on your Wear OS watch.

2. **Enable Developer Options:**
   - Tap 'System' -> 'About'.
   - Scroll to 'Software Version' and tap it 5 times (for Galaxy Watch 4/5/6) until you see a message that you are now a developer.

3. **Enable ADB Debugging:**
   - Go back to the main 'Settings' menu.
   - Tap 'Developer Options'.
   - Toggle 'ADB Debugging' to 'On'.

### Enable Wireless Debugging

1. **On Your Watch:**
   - In 'Developer Options', toggle 'Debug over Wi-Fi' to 'On'.
   - Note the IP address displayed under 'Debug over Wi-Fi'.

### Pairing and Connecting

1. **Pairing:**
   - Open a command prompt or terminal.
   - Navigate to your ADB directory (e.g., `cd C:\adb` on Windows).
   - Run `adb pair ipaddress:port` (replace `ipaddress` and `port` with the ones noted from your watch).

2. **Connecting:**
   - After pairing, run `adb connect ipaddress:port`.

Your Wear OS watch should now be successfully paired and connected via ADB.
