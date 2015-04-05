package org.team3128.hardware.misc;

import org.team3128.Options;

/**
 * Controls red and blue lights on the robot, connected by relays to the controller.
 * @author Jamie
 *
 */
public class Lights
{
	RelayLink _redLights;
	RelayLink _blueLights;
	
	
	public Lights(RelayLink redLights, RelayLink blueLights)
	{
		_redLights = redLights;
		_blueLights = blueLights;
	}
	
	/**
	 * Change the light color.  A null argument will turn off the lights.
	 * @param alliance
	 */
	public void lightChange(Options.Alliance alliance)
	{
		if(alliance == null)
		{
			_redLights.setOff();
			_redLights.setOff();
		}
		else if(Options.instance()._alliance == Options.Alliance.BLUE)
	    {
	    	_redLights.setOff();
	    	_blueLights.setOn();
	    }
	    else
	    {
	    	_redLights.setOn();
	    	_blueLights.setOff();
	    }
	}

	void lightChangeToCurrentAlliance()
	{
		lightChange(Options.instance()._alliance);
	}
	
}
