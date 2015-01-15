package org.team3128;

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

    public Alliance _alliance;

    public boolean _armEnabled;

    /**
     * port the xbox controller connected to the drivers' console is on
     */
    public short _controllerPort;
    
    /**
     * update frequency of the motor control code
     */
    public int _motorControlUpdateFrequency;
    
    /**
     * 7-bit address of the tachometer
     */
    public byte _tachI2CAddress;
    
    /**
     * constant for holonomic turning speed
     */
    public double _turningSpeedConstant;
    
    /**
     * constant for holonomic gliding speed
     */
    public double _glidingSpeedConstant;
    
    private static Options _instance;
    
    public static Options instance()
    {
    	if(_instance == null)
    	{	
    		_instance = new Options();
    	}
    	return _instance;
    }
    
    //====================================================================
    // Configuration settings go HERE
    private Options()
    {
    	_alliance = Alliance.BLUE;
    	
    	_armEnabled = false;
    	
    	_controllerPort = 0;
    	
    	_motorControlUpdateFrequency = 75;
    	
    	_tachI2CAddress = (byte) 0b10101110;
    	
    	_turningSpeedConstant = .4;
    	
    	_glidingSpeedConstant = .5/Math.sqrt(2);
    }
}
