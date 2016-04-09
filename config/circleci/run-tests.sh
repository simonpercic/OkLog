#!/bin/bash

if [ $CIRCLE_NODE_INDEX -eq 0 ]
then
    ./gradlew check
elif [ $CIRCLE_NODE_INDEX -eq 1 ]
then
    emulator -avd circleci-android22 -no-audio -no-window
    circle-android wait-for-boot
    ./gradlew connectedCheck
fi
