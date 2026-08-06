@echo off
cd /d "%~dp0"
setlocal
if not exist out mkdir out
javac --module-path "lib\javafx-sdk-21.0.11\lib" -d out src\module-info.java src\alyoshenka\*.java
if errorlevel 1 goto :err
echo Build OK
goto :eof
:err
echo Build FAILED
endlocal
