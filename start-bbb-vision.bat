@echo off
echo Starting coprocessor vision program...
echo.

set TARGET_HOSTNAME=frc-beaglebone
set USER=alarm
set PASSWORD=alarm
set SCRIPT=start-vision.sh
set GRIPDIR=/home/alarm
set GRIPFILE=active.grip
set CONNECTION_ARGS="%USER%@%TARGET_HOSTNAME%"

rem "alarm" comes from Arch Linux ARM

echo Testing connection...
echo.
ping -n 1 frc-beaglebone >nul

if %errorlevel% equ 0 (
	echo Connected to the coprocessor
	echo.
	
	echo Copying %GRIPFILE% to coprocessor...
	
	tools\pscp.exe -pw %PASSWORD% "vision\%GRIPFILE%" "%CONNECTION_ARGS%:%GRIPDIR%"
	
	echo Starting GRIP script on coprocessor
	echo -------------------------------------------------------
	echo.
	
	tools\plink.exe -ssh -pw %PASSWORD% %CONNECTION_ARGS% "%GRIPDIR%/%SCRIPT% %GRIPFILE%"
	
	echo. 
 ) ELSE (
	echo Failed to connect.  Exiting...
 )

pause