package org.team3128.hardware.ultrasonic;

import java.util.concurrent.atomic.AtomicInteger;

import org.team3128.Log;
import org.team3128.util.Pair;
import org.team3128.util.Units;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.SerialPort.StopBits;

/**
 * Class to read the Maxbotics MaxSonar sensors plugged into the RoboRIO RS232 port.
 * It is accurate to a few millimeters and has a max range of 500 cm.
 * 
 * The distance is updated asynchronously every half-second in auto-ping mode.
 * 
 * @author Jamie
 *
 */
public class MaxSonar extends IUltrasonic 
{

	SerialPort ultrasonicPort;
	
	DigitalOutput rangingPin;
	
	Thread readerThread;
	
	AtomicInteger distanceMM;
	
	double units;
	
	boolean autoPing = true;
	
	/**
	 * 
	 * @param rangingPinDIONumber The DIO pin that the ranging pin on the ultrasonic is connected to.
	 * @param resolution The resolution from the Units class of the sensor as described by the MaxBotix website.  The MB1013 is mm, and the MB1010 is inch.
	 * @param portToUse which serial port to use.
	 */
	public MaxSonar(int rangingPinDIONumber, double resolution, Port portToUse)
	{
		distanceMM = new AtomicInteger();
		
		ultrasonicPort = new SerialPort(9600, portToUse, 8, Parity.kNone, StopBits.kOne);
		ultrasonicPort.enableTermination('\r');
		ultrasonicPort.setTimeout(2);
		
		rangingPin = new DigitalOutput(rangingPinDIONumber);
		rangingPin.set(true);
		
		readerThread = new Thread(this::readerLoop);
		readerThread.start();
	}
	
	private Pair<Boolean, Integer> getDistanceFromResponse(String response)
	{
		//Response looks like "R1024"
		if(response == null || response.length() < 2)
		{
			Log.recoverable("MaxSonar", "Got bad response from sensor!");
			return new Pair<Boolean, Integer>(Boolean.FALSE, 0);
		}
		
		String numberPart = response.substring(1, response.length());
		
		try
		{
			Log.debug("MaxSonar", "Measured distance as " + distanceMM.get() + " mm");

			return new Pair<Boolean, Integer>(Boolean.TRUE, Integer.parseInt(numberPart));
		}
		catch(NumberFormatException ex)
		{
			ex.printStackTrace();
			Log.recoverable("MaxSonar", "Got bad response from sensor, coudn't convert to an integer!");
			return new Pair<Boolean, Integer>(Boolean.FALSE, 0);
		}
		
	}
	
	private void readerLoop()
	{
		while(true)
		{
			String response = ultrasonicPort.readString();
			
			if(readerThread.isInterrupted())
			{
				return;
			}
			
			Pair<Boolean, Integer> result = getDistanceFromResponse(response);
			
			if(result.left == true)
			{
				distanceMM.set(result.right);
			}

		}
	}

	@Override
	/**
	 * Gets the distance.
	 * 
	 */
	public double getDistance()
	{
		if(autoPing)
		{
			return distanceMM.get() * Units.mm;
		}
		else
		{
			rangingPin.set(true);
			String response = ultrasonicPort.readString();
			
			Pair<Boolean, Integer> result = getDistanceFromResponse(response);
			rangingPin.set(false);
			
			//give it some time to reset
			try
			{
				Thread.sleep(10);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			
			return result.right;
		}
	}

	@Override
	public double getMaxDistance()
	{
		return 500.0;
	}

	@Override
	public void setAutoPing(boolean autoPing)
	{
		this.autoPing = autoPing;
		
		//pulling this low disables automatic ranging
		rangingPin.set(autoPing);
		
		if(autoPing)
		{
			if(!readerThread.isAlive())
			{
				readerThread = new Thread(this::readerLoop);
				readerThread.start();
			}
		}
		else
		{
			if(readerThread.isAlive())
			{
				readerThread.interrupt();
				long startTime = System.currentTimeMillis();
				try
				{
					readerThread.join();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				Log.debug("MaxSonar", "Shutting down thread took " + (System.currentTimeMillis() - startTime) + " ms");
				
				ultrasonicPort.reset();
			}
			
			
		}
	}

}
