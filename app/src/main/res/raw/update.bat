@echo off

REM Start the emulator
start /B emulator -avd Pixel_6_Pro_API_TiramisuPrivacySandbox -writable-system

REM Wait for the emulator to start
timeout /T 30

REM Ensure root access
adb -s emulator-5554 root

REM Remount the system partition
adb -s emulator-5554 remount

REM Push the updated hosts file
adb -s emulator-5554 push D:\JD\Android\ImageMovement\app\src\main\res\raw\hosts /system/etc/hosts

REM Reboot the emulator
adb -s emulator-5554 reboot
