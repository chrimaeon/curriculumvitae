#!/bin/sh

# see https://developer.android.com/training/wearables/get-started/debugging#bt-debugging

if [[ -z $ADB ]]; then ADB=adb; fi
$ADB connect $1:5555
