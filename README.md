AwakeDebug
==========
<a href="https://play.google.com/store/apps/details?id=com.afzaln.awakedebug">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" />
</a>

<p align="left">
  <img src="/design/latest_playstore.png" alt="Messages screen (Phone)" height="30%" width="30%"/>
</p>


This app allows you to keep your screen on when the device is connected to USB and Debugging (ADB) is enabled.

It switches to the previously set display timeout when the device is unplugged.

Permissions:
- WRITE_SETTINGS to store the old display timeout.
- INTERNET for Crashlytics (crash reports)

Note: If the app crashes, please send the crash report so that I can fix it. No user-specific information is sent, only information related to the crash (stacktrace) and device information like OS version, free space, rooted/non-rooted, free RAM, etc, is sent.

This application is open-source under the Apache license. 


## Test cases

1. New install. Plug into AC/USB, screen off timeout is unchanged.

### For USB debugging

1. Switch on Awake for Debug, user sees permission dialog. User clicks cancel, everything remains unchanged.
2. User grants permission, Switch is enabled. If user is already plugged into USB and debugging, screen off timeout is modified immediately. If not, after user plugs in USB and starts debugging, screen timeout is modified.
3. User plugs out or disables/stops debugging, screen timeout goes back to what it was.
4. User disables option, screen timeout goes back to what it was.

### For AC Power

1. Enable "On when connected to AC". If user is already plugged into AC, screen off timeout is modified immediately. If not, after user plugs in AC, screen timeout is modified.
2. User plugs out, screen timeout goes back to what it was.
3. User disables option, screen timeout goes back to what it was.