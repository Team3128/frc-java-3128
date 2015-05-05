package org.team3128.hardware.misc;

import edu.wpi.first.wpilibj.PWM;

/**
 * Controls a RGB light strip through three PWM outputs, one for each color.
 * @author Jamie
 *
 */
public class PWMLights
{
	PWM redLights;
	PWM greenLights;
	PWM blueLights;
	
	/**
	 * Construct a PWMLights object from the three PWM channels it is attached to.
	 * @param redChannel
	 * @param greenChannel
	 * @param blueChannel
	 */
	public PWMLights(int redChannel, int greenChannel, int blueChannel)
	{
		redLights = new PWM(redChannel);
		greenLights = new PWM(greenChannel);
		blueLights = new PWM(blueChannel);
	}
	
	/**
	 * Set the color of the lights in RGB.
	 * This function uses the RoboRIO's 12-bit PWM resolution, so it takes 12 bit colors.
	 * @param r
	 * @param g
	 * @param b
	 */
	//there are no short literals in Java, so the function takes ints for convenience.
	public void setColorFullRGB(int r, int g, int b)
	{
		redLights.setRaw(r);
		greenLights.setRaw(g);
		blueLights.setRaw(b);
	}
	
	/**
	 * Turn the lights off entirely.
	 */
	public void setOff()
	{
		setColorFullRGB(0, 0, 0);
	}
	
	/**
	 * Set the color of the lights in RGB.
	 * This function uses 8 bits for each color, and scales them up for output.
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setColor8BitRGB(int r, int g, int b)
	{
		setColorFullRGB(r * 8, g * 8, b * 8);
	}
}
