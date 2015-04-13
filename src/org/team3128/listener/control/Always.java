package org.team3128.listener.control;

/**
 * Hack-ish control listener which fires every tick.  Good for debugging.
 * @author Jamie
 *
 */
public class Always implements IControl
{
	private Always()
	{
		
	}
	
	public static Always instance = new Always();
}
