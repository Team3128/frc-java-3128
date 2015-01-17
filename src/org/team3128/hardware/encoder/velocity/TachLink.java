package org.team3128.hardware.encoder.velocity;

import java.util.concurrent.locks.ReentrantLock;

import org.team3128.Options;
import edu.wpi.first.wpilibj.I2C;

public class TachLink implements IVelocityEncoder
{
	private static I2C _tachConnection;
	
	// lock this mutex whenever you want to use _tachConnection
	private static ReentrantLock _tachConnMutex;
	
	private int _tachNumber;
	
	private int _toothNumber;
	
	final static int MEASURMENTS_PER_SECOND = 1;
	
	public TachLink(int tachNumber, int toothNumber)
	{
		if(tachNumber >= 8)
		{
			throw new IllegalArgumentException("tachometer number must be between 1 and 7");
		}
		
		_tachNumber = tachNumber;
		
		//init static variables if this is the first use of the class
		if(_tachConnection == null)
		{
			_tachConnection = new I2C(I2C.Port.kOnboard, Options.instance()._tachI2CAddress);
			
			_tachConnMutex = new ReentrantLock();
		}
		
		_toothNumber = toothNumber;
	}
	
	/**
	 * From the tachometer, get the number of teeth it's seen in the last measurement period
	 * 
	 * Time-wise, this is a relatively expensive operation, so don't call this function unnecessarily.
	 */
	public short getRaw()
	{
		_tachConnMutex.lock();
		
		//write tach number
		_tachConnection.transaction(new byte[]{(byte) _tachNumber}, 1, new byte[0], 0);
		
		//read raw value
		byte[] result = new byte[4];
		_tachConnection.transaction(new byte[0], 0, result, 4);
		short rawValue = 0;
		
		//combine 4 bytes to make a short
		for(int counter = 0; counter < 4; ++counter)
		{
			rawValue |= result[3 - counter] << counter;
		}
		
		_tachConnMutex.unlock();
		
		return rawValue;
	}
	
	/**
	 * From the tachometer, get the RPM of the gear.
	 * 
	 * Time-wise, this is a relatively expensive operation, so don't call this function unnecessarily.
	 */
	@Override
	public double getSpeedInRPM()
	{
		int rawValue = getRaw();
		
		return (60 * MEASURMENTS_PER_SECOND) * (rawValue / _toothNumber);
	}
	
	
	
	
}
