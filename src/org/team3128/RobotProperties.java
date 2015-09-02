package org.team3128;


/**
 * Class that stores runtime options.
 * Some are set at initialization time in the main classes.
 * @author Jamie
 *
 */
public class RobotProperties
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
    public static double wheelCircumfrence;
    
    /**
     * horizontal distance between wheels in cm
     */
    public static double wheelBase;

    /**
     * Gyro offset for swerve drive code
     */
	public static double gyrBias = 0;

}
