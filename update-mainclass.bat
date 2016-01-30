@echo off
echo This script sets the main class in use for the team3128 codebase
echo.
set /P teamnumber="Please enter team number of the roborio: "
echo.

rem enable substituting variables inside if statements
setlocal enabledelayedexpansion

set roborioAddress=roborio-%teamnumber%-FRC.local

echo Testing connection...
echo.
ping -n 1 %roborioAddress% >nul

if %errorlevel%==0 (
	echo Connected to Team %teamnumber%'s roboRIO
	echo.
	
	set /P mainclass="Enter the name of the main class you want to use: "
	echo.
	
	tools\plink.exe lvuser@%roborioAddress% "echo !mainclass! > ~/AlumNarMainClass.txt"
	
	echo. 
	if !errorlevel!==0 (
		echo Success. Main class updated to !mainclass!.
	) ELSE (
		echo Failure.
	)
) ELSE (
	echo Failed to connect.  Exiting...
)

pause