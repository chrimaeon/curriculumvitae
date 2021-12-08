#!/bin/sh

# see https://developer.android.com/training/wearables/get-started/debugging#bt-debugging

if [[ -z $ADB ]]; then ADB=adb; fi
$ADB -d forward tcp:4444 localabstract:/adb-hub
$ADB -d connect 127.0.0.1:4444
