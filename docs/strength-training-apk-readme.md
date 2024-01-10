# How to setup strength training apk
The best auto-rep/set detection strength training app. Uses accelerometer to detect reps and sets very accurately. It's an excellent
way to stay focused on the gym, track your progress, and be motivated.

Why Google killed this app is beyond me. It's a shame that they're not supporting it anymore.

## Note
- Confirm working on TicWatch Pro 5/3, Galaxy Watch 4/5/6 since they do not come with Google Fit pre-installed.
- This will only work if
  - your watch does not come with Google Fit pre-installed
  - you disable auto-update on your watch

### Instructions
- You will need to make sure `adb` is installed on your computer (on your PATH) and that you have enabled developer mode on your watch (https://developer.android.com/studio/command-line/adb)
- Uninstall the existing Google Fit app on your watch if already present
    - `adb uninstall "com.google.android.apps.fitness"`
- Download and install the following apks:
    - `adb install "...\wear os apks\strength training google fit\com.google.android.apps.fitness_2.54.27-230-2025427230_minAPI25(armeabi-v7a)(nodpi)_apkmirror.com.apk"`
    - `adb install "...\wear os apks\strength training google fit\FitST_wear.apk"` download from https://freepoc.org/downloads/





