#!/bin/bash
emulator -avd circleci-android19 -no-audio -no-window &
circle-android wait-for-boot
./gradlew check connectedCheck
