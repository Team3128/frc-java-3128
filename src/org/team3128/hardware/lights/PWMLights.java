package org.team3128.hardware.lights;

import org.team3128.Log;
import org.team3128.hardware.motor.logic.LightsColor;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.PWM.PeriodMultiplier;

/**
 * Controls a RGB light strip through three PWM outputs, one for each color.
 * 
 * It can also fade the lights on and off.
 * @author Jamie
 *
 */
public class PWMLights
{
	PWM redLights;
	PWM greenLights;
	PWM blueLights;
	
	Thread faderThread;
	
	/**
	 * Construct a PWMLights object from the three PWM ports on the roboRIO it is attached to.
	 * @param redChannel
	 * @param greenChannel
	 * @param blueChannel
	 */
	public PWMLights(int redChannel, int greenChannel, int blueChannel)
	{
		redLights = new PWM(redChannel);
		greenLights = new PWM(greenChannel);
		blueLights = new PWM(blueChannel);
		
		//turn off legacy period scaling
		//gee, thanks WPI for not documenting this ANYWHERE!
		redLights.setPeriodMultiplier(PeriodMultiplier.k1X);
		greenLights.setPeriodMultiplier(PeriodMultiplier.k1X);
		blueLights.setPeriodMultiplier(PeriodMultiplier.k1X);
	}
	
	/**
	 * Set the color of the lights in RGB.
	 * 
	 * If the fader thread is running, calling this function will stop it.
	 * 
	 * @param r
	 * @param g
	 * @param b
	 */
	//there are no short literals in Java, so the function takes ints for convenience.
	public void setColor(LightsColor color)
	{
		//Make sure that the fader thread is not setting these values at the same time.
		shutDownFaderThread();
		
		redLights.setRaw(color.getR());
		greenLights.setRaw(color.getG());
		blueLights.setRaw(color.getB());
	}
	
	/**
	 * Turn the lights off entirely.
	 */
	public void setOff()
	{
		setColor(LightsColor.new4Bit(0, 0, 0));
	}
	
	/**
	 * Start the fader thread, which pulses the lights on and off.
	 * @param r
	 * @param g
	 * @param b
	 * @param decrement how far (11 bit) to decrement each channel's brightness every cycle.
	 * @param period how long to wait between fader adjustment cycles in milliseconds.
	 */
	public void setFader(LightsColor color, int decrement, int period)
	{
		shutDownFaderThread();
		
		faderThread = new Thread(() -> faderLoop(color, decrement, period), "Fader Thread");
		faderThread.start();
	}
	
	/**
	 * Stop the fader thread running, leaving the lights wherever they happen to be.
	 * 
	 * If the fader thread is not running, does nothing.
	 * 
	 * Called by setColor() and setOff()
	 */
	public void shutDownFaderThread()
	{
		if(faderThread != null && faderThread.isAlive())
		{
			faderThread.interrupt();
			try
			{
				faderThread.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Function run in its own thread to fade the lights.
	 * It takes the base color the lights should fade from in 12 bit form as the irst three parameters. 
	 *
	 * @param wavelength
	 */
	private void faderLoop(LightsColor originalColor, int decrement, int period)
	{
		Log.debug("PWMLights", "Fader Thread Starting");
		
		//positive or negative based on whether the lights are getting brighter or darker at the moment
		int direction = -1;
		
		int r = originalColor.getR();
		int g = originalColor.getG();
		int b = originalColor.getB();
		
		while(true)
		{
			r += direction * decrement;
			g += direction * decrement;
			b += direction * decrement;
			
			//check if all of the channels have gone below zero
			if(r < 0 && g < 0 && b < 0)
			{
				direction = 1;

			}
			else if(r > originalColor.getR() && g > originalColor.getG() && b > originalColor.getB())
			{
				direction = 1;
			}
			
			//Put channels back within limits
			r = RobotMath.clampInt(r, 0, originalColor.getR());
			g = RobotMath.clampInt(g, 0, originalColor.getG());
			b = RobotMath.clampInt(b, 0, originalColor.getB());
			
			
			//actually set the values
			redLights.setRaw(r);
			greenLights.setRaw(g);
			blueLights.setRaw(b);
			
			try
			{
				Thread.sleep(period);
			}
			catch (InterruptedException e)
			{
				Log.debug("PWMLights", "Fader Thread Shutting Down");
			}
		}
	}
}
