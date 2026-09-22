@echo off
echo ========================================================
echo Installing Find My Work - Provider APK to device...
echo ========================================================
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" wait-for-device
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" install -r "app\build\outputs\apk\debug\app-debug.apk"
echo Launching app...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.findmywork/.MainActivity
echo Done!
pause
