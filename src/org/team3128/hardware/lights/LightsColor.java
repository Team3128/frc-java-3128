package org.team3128.hardware.lights;

/**
 * Class to represent a color that can be displayed on the robot lights.
 * @author Jamie
 *
 */
public class LightsColor
{
	//actual 11-bit RGB values.
	private int r, g, b;
	
	/**
	 * Highest acceptable value for a single rgb channel
	 */
	public static final int HIGHEST_COLOR_VALUE = 0x7ff;
	
	private LightsColor(int r, int g, int b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	/**
	 * Create a new color from 11-bit values (up to 0x7ff).
	 * @param rChannel
	 * @param gChannel
	 * @param bChannel
	 * @return
	 */
	public static LightsColor new11Bit(int rChannel, int gChannel, int bChannel)
	{
		return new LightsColor(rChannel, gChannel, bChannel);
	}
	
	/**
	 * Create a new color from 8-bit values (up to 0xff).
	 * @param rChannel
	 * @param gChannel
	 * @param bChannel
	 * @return
	 */
	public static LightsColor new8Bit(int rChannel, int gChannel, int bChannel)
	{
		return new LightsColor(rChannel*8, gChannel*8, bChannel*8);
	}
	
	/**
	 * Create a new color from 4-bit shorthand (up to 0xf)
	 * 
	 * This is used as a shorthand for specifying colors, sort of like abbreviating #ffaabb to #fab in HTML/CSS
	 * @param rChannel
	 * @param gChannel
	 * @param bChannel
	 * @return
	 */
	public static LightsColor new4Bit(int rChannel, int gChannel, int bChannel)
	{
		return new LightsColor(rChannel*128, gChannel*128, bChannel*128);
	}
	
	/**
	 * Get the 11-bit red channel value.
	 * @return
	 */
	public int getR()
	{
		return r;
	}
	
	/**
	 * Get the 11-bit green channel value.
	 * @return
	 */
	public int getG()
	{
		return g;
	}
	
	/**
	 * Get the 11-bit blue channel value.
	 * @return
	 */
	public int getB()
	{
		return b;
	}
}
