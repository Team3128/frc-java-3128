package org.team3128;

import org.team3128.util.Units;

/**
 * Singleton class that stores runtime options
 * @author Jamie
 *
 */
public class Options
{
    public enum Alliance
    {
    	RED,
    	BLUE
    };

    public static Alliance alliance = Alliance.BLUE;
    
    public static boolean armEnabled = false;

    /**
     * port the xbox controller connected to the drivers' console is on
     */
    public static short controllerPort = 0;
    
    /**
     * update frequency of the motor control code
     */
    public static int motorControlUpdateFrequency = 75;
    
    /**
     * 7-bit address of the tachometer
     */
    public static byte tachI2CAddress = (byte) 0b11111110;
    
    /**
     * constant for holonomic turning speed
     */
    public static double turningSpeedConstant = .4;
    
    /**
     * constant for holonomic gliding speed
     */
    public static double glidingSpeedConstant = .5/Math.sqrt(2);
    
    /**
     * circumfrence of wheels in cm
     */
    public static double wheelCircumfrence = 6 * Units.INCH * Math.PI;
    
    /**
     * centimeters moved per wheel degree
     */
    public static double cmMovedPerDegree = wheelCircumfrence / 360;
    
    /**
     * degrees moved per linear centimeter
     */
    public static double degreesPercm = 360 / wheelCircumfrence;
    
    /**
     * horizontal distance between wheels in cm
     */
    public static double wheelBase = 24.5 * Units.INCH;;

    /**
     * Gyro offset for swerve drive code
     */
	public static double gyrBias = 0;
	
	/**
	 * Multiplier for teleop arm speed
	 */
	public static double armSpeedMultiplier = .8;
}
