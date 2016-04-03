package org.team3128.hardware.misc;

import edu.wpi.first.wpilibj.AnalogInput;

/**
 * Interface for the Modern Robotics Optical Distance Sensor
 * @author Narwhal
 *
 */
public class MROpticalDistanceSensor {

	AnalogInput sensorInput;
	
	public MROpticalDistanceSensor(int analogPort)
	{
		sensorInput = new AnalogInput(analogPort);
	}
	
	public double getRaw()
	{
		return sensorInput.getVoltage();
	}
}
