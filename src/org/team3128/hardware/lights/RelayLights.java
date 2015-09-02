package org.team3128.hardware.lights;

import org.team3128.RobotProperties;
import org.team3128.hardware.misc.RelayLink;

/**
 * Controls red and blue lights on the robot, connected by relays to the controller.
 * @author Jamie
 *
 */
public class RelayLights
{
	RelayLink _redLights;
	RelayLink _blueLights;
	
	
	public RelayLights(RelayLink redLights, RelayLink blueLights)
	{
		_redLights = redLights;
		_blueLights = blueLights;
	}
	
	/**
	 * Change the light color.  A null argument will turn off the lights.
	 * @param alliance
	 */
	public void lightChange(RobotProperties.Alliance alliance)
	{
		if(alliance == null)
		{
			_redLights.setOff();
			_redLights.setOff();
		}
		else if(RobotProperties.alliance == RobotProperties.Alliance.BLUE)
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
		lightChange(RobotProperties.alliance);
	}
	
}
