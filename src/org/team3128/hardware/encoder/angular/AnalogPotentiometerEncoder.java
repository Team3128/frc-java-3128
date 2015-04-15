package org.team3128.hardware.encoder.angular;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 * Represents a variable resistor/potentiometer encoder. 
 * @author Kian, Jamie
 *
 */
public class AnalogPotentiometerEncoder implements IAngularEncoder, Sendable
{
	private AnalogInput enc;
	private double degreesPerVolt;
    private final double offset;
    
    private ITable table;
    
    /**
     * 
     * @param chan
     * @param off offset in degrees
     * @param voltsAtEndOfTravel the voltage when the encoder is at the end of its travel
     * @param travelLength the length in degrees of the travel
     */
    public AnalogPotentiometerEncoder(int chan, int off, double voltsAtEndOfTravel, double travelLength)
    {
		enc = new AnalogInput(chan);
		offset = off;
		
		degreesPerVolt = travelLength / voltsAtEndOfTravel;
	}
	
	@Override
	public double getAngle() {
		
		return (getRawValue() * degreesPerVolt) + offset;
	}

	@Override
	public double getRawValue()
	{
		return enc.getVoltage();
	}

	@Override
	public boolean canRevolveMultipleTimes()
	{
		return false;
	}

	@Override
	public void initTable(ITable subtable)
	{
		table = subtable;
		updateTable();
	}
	
	/**
	 * Call this to update the angle stored in the ITable
	 */
	private void updateTable()
	{
		table.putNumber("Angle", getAngle());
	}

	@Override
	public ITable getTable()
	{
		return table;
	}

	@Override
	public String getSmartDashboardType()
	{
		return "Potentiometer";
	}

}
