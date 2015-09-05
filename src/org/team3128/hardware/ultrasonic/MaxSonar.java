package org.team3128.hardware.ultrasonic;

import java.util.concurrent.atomic.AtomicInteger;

import org.team3128.Log;
import org.team3128.util.Units;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.SerialPort.StopBits;

/**
 * Class to read the Maxbotics MaxSonar sensor plugged into the RoboRiO RS232 port.
 * It is accurate to a few millimeters and has a max range of 500 cm.
 * 
 * THe distance is updated asynchronously every half-second
 * 
 * @author Jamie
 *
 */
public class MaxSonar extends IUltrasonic 
{

	SerialPort ultrasonicPort;
	
	Thread readerThread;
	
	AtomicInteger distanceMM;
	
	public MaxSonar()
	{
		distanceMM = new AtomicInteger();
		
		ultrasonicPort = new SerialPort(9600, Port.kOnboard, 8, Parity.kNone, StopBits.kOne);
		ultrasonicPort.enableTermination('\r');
		ultrasonicPort.setTimeout(2);
		
		readerThread = new Thread(this::readerLoop);
	}
	
	private void readerLoop()
	{
		while(true)
		{
			String response = ultrasonicPort.readString();
			
			//Response looks like "R1024"
			if(response == null || response.length() < 2)
			{
				Log.recoverable("MaxSonar", "Got bad response from sensor!");
			}
			
			String numberPart = response.substring(1, response.length());
			
			try
			{
				distanceMM.set(Integer.parseInt(numberPart));
				Log.debug("MaxSonar", "Measured distance as " + distanceMM.get() + " mm");
			}
			catch(NumberFormatException ex)
			{
				ex.printStackTrace();
				Log.recoverable("MaxSonar", "Got bad response from sensor, coudn't convert to an integer!");
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public double getDistance()
	{
		return distanceMM.get() * Units.mm;
	}

	@Override
	public double getMaxDistance()
	{
		return 500.0;
	}

}
