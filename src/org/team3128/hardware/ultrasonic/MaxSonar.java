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
 * Class to read the Maxbotics MaxSonar sensor plugged into the RoboRIO RS232 port.
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
	
	//in whatever unit the sensor sends back
	AtomicInteger distance;
	
	public static enum Resolution
	{
		//inch-resolution sensors, such as the MB1010
		INCH(Units.in, 3, 255),
		CM(Units.cm, 3, 765), //cm-resolution sensors
		MM(Units.mm, 4, 5000); //mm-resolution sensors, such as the MB1013
		
		double conversionFactor;
		int bytesPerResponse; //including the carriage return
		int maxDistance; //distance the sensor reports when it can't see anything (in sensor units)
		
		private Resolution(double conversionFactor, int digitsPerResponse, int maxDistance)
		{
			this.conversionFactor = conversionFactor;
			bytesPerResponse = digitsPerResponse + 2;
			this.maxDistance = maxDistance;
		}
	}
	
	Resolution sensorResolution;
	
	boolean autoPing = true;
	
	/**
	 * 
	 * @param rangingPinDIONumber The DIO pin that the ranging pin on the ultrasonic is connected to.
	 * @param resolution The resolution from the of the sensor as described by the MaxBotix website.  The MB1013 is mm, and the MB1010 is inch.
	 * @param portToUse which serial port to use.  Note that if using onboard, you will need to disable the console out in the webpage.
	 */
	public MaxSonar(int rangingPinDIONumber, Resolution res, Port portToUse)
	{
		distance = new AtomicInteger();
		
		sensorResolution = res; 
		
		ultrasonicPort = new SerialPort(9600, portToUse, 8, Parity.kNone, StopBits.kOne);
		ultrasonicPort.setTimeout(2);
		ultrasonicPort.setReadBufferSize(sensorResolution.bytesPerResponse);
		
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
		Log.debug("MaxSonar", response);
		String numberPart = response.substring(1, response.length() - 1); //remove R character and carriage return
		
		
		try
		{
			int distance = Integer.parseInt(numberPart, 10);
			Log.debug("MaxSonar", "Measured distance as " + (distance * sensorResolution.conversionFactor) + " cm");

			return new Pair<Boolean, Integer>(Boolean.TRUE, distance);
		}
		catch(NumberFormatException ex)
		{
			ex.printStackTrace();
			Log.recoverable("MaxSonar", "Got bad response from sensor, coudn't convert to an integer: " + numberPart + "!");
			return new Pair<Boolean, Integer>(Boolean.FALSE, 0);
		}
		
	}
	
	private void readerLoop()
	{
		while(true)
		{
			Log.debug("MaxSonar", "Waiting for response");			

			String response;
			
			try
			{
				response = ultrasonicPort.readString(sensorResolution.bytesPerResponse);
	
			}
			catch(StringIndexOutOfBoundsException ex)
			{
				ex.printStackTrace();
				continue;
			}
			
			if(readerThread.isInterrupted())
			{
				return;
			}
			
			Pair<Boolean, Integer> result = getDistanceFromResponse(response);
			
			if(result.left == true)
			{
				distance.set(result.right);
			}
			else
			{
				ultrasonicPort.reset();
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
			return distance.get() * sensorResolution.conversionFactor;
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
		return sensorResolution.maxDistance * sensorResolution.conversionFactor;
	}

	@Override
	/**
	 * According to the datasheet, pinging the sensor this way 
	 */
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
