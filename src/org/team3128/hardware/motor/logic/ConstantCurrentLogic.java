package org.team3128.hardware.motor.logic;

import org.team3128.hardware.motor.MotorLogic;
import org.team3128.util.VelocityPID;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Motor control logic that tries to hold the motor at a certain current.
 * It uses P only, and needs an estimate of how much current the motor uses at full power.
 * @author Jamie
 *
 */
public class ConstantCurrentLogic extends MotorLogic
{
	private PowerDistributionPanel panel;
	private int motorPort;
	
	//the calculation for this is pretty much the same as velocity PID.
	//so, we can use the same math object
	private VelocityPID pidCalc;
    private double feedForward;
    private double estimatedOutput = 0;
    
    /**
     * Construct ConstantCurrentLogic
     * @param panel PDP object to use
     * @param motorPort the port number that the motor is in on the PDP.
     * @param kP the konstant of proportionality to use in scaling the current
     * @param feedForward the feedforward constant to use, as in how many amps are used by a motor power of 1
     */
    public ConstantCurrentLogic(PowerDistributionPanel panel, int motorPort, double kP, double feedForward)
    {
        this.panel = panel;
        this.motorPort = motorPort;
        pidCalc = new VelocityPID(kP, 0, 0);
        this.feedForward = feedForward;
    }
   
    @Override
    /**
     * Set the current to target in amps.
     */
    public synchronized void setControlTarget(double d)
    {
    	pidCalc.setDesiredVelocity(d);
    	
    	//estimate the motor power needed to get this current
    	//as a starting point for the p control
    	estimatedOutput = feedForward * d;
    }

    @Override
    public double speedControlStep(double dt)
    {
        double currentCurrent = panel.getCurrent(motorPort);
        pidCalc.update(currentCurrent);
        return pidCalc.getOutputAddition() + estimatedOutput;
    }
   
    @Override
    public synchronized void clearControlRun()
    {
    	pidCalc.setDesiredVelocity(0);
    	estimatedOutput = 0;
    	pidCalc.resetIntegral();
    }

    @Override
    public boolean isComplete()
    {
    	return false;
    }
}
