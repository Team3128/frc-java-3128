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
     * update frequency of the listener manager
     */
    public int _listenerManagerUpdateFrequency;
    
    /**
     * update frequency of the motor control code
     */
    public int _motorControlUpdateFrequency;
    
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
    	
    	_controllerPort = 1;
    	
    	_listenerManagerUpdateFrequency = 75;
    	
    	_motorControlUpdateFrequency = 75;
    }
}
