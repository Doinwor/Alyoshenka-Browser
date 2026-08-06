@echo off
cd /d "%~dp0"
setlocal
if not exist out\alyoshenka goto :build
java --module-path "lib\javafx-sdk-21.0.11\lib;out" -m alyoshenka/alyoshenka.AlyoshenkaBrowser
goto :eof
:build
echo Please run build.bat first.
endlocal