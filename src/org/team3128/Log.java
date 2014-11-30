package org.team3128;

import edu.wpi.first.wpilibj.Watchdog;

public class Log
{
	/**
	 * Log a FATAL error, after which the robot cannot (properly) function
	 * Calling this ACTUALLY STOPS THE ROBOT, so if it is bad but not THAT bad, call Log.recoverable() instead
	 * @param category
	 * @param message
	 */
	public static void fatal(String category, String message)
	{
		log("Fatal", category, message);
		
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		Watchdog.getInstance().kill();
		
	}
	
	/**
	 * Log a failure which affects one function or one thread, however the robot can keep functioning
	 * @param category
	 * @param message
	 */
	public static void recoverable(String category, String message)
	{
		log("Recoverable", category, message);
	}
	
	/**
	 * Log something which should not happen under normal circumstances and probably is a bug, but does not cause anything to break
	 * @param category
	 * @param message
	 */
	public static void unusual(String category, String message)
	{
		log("Unusual", category, message);
	}
	
	public static void info(String category, String message)
	{
		log("Info", category, message);
	}
	
	public static void debug(String category, String message)
	{
		log("Debug", category, message);
	}
	
	private static void log(String severity, String category, String message)
	{
		System.out.println(String.format("[%s] [%s] %s", severity, category, message));
	}
}
