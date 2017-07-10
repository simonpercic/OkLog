#!/bin/bash
emulator -avd circleci-android22 -no-audio -no-window &
circle-android wait-for-boot
./gradlew check connectedCheck
