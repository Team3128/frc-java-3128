@echo off
echo This script sets the main class in use for the team3128 codebase
echo.
set /P teamnumber="Please enter team number of the roborio: "
echo.

echo Testing connection...
echo.
ping -n 1 roborio-%teamnumber%.local >nul

if %errorlevel%==0 (
	echo Connected to Team %teamnumber%'s roboRIO
	echo.
	
	set /P mainclass="Enter the name of the main class you want to use: "
	echo.
	
	tools\plink.exe lvuser@roborio-3128.local echo %mainclass% > ~/AlumNarMainClass.txt
	
	echo. 
	if %errorlevel%==0 (
		echo Success! Main class updated to %mainclass%.
	) ELSE (
		echo Failure!
	)
) ELSE (
	echo Failed to connect.  Exiting...
)

pause