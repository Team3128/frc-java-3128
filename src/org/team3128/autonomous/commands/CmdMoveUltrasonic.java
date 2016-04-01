package org.team3128.autonomous.commands;

import org.team3128.Log;
import org.team3128.autonomous.AutoUtils;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.ultrasonic.IUltrasonic;
import org.team3128.util.PIDConstants;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.command.Command;

/*        _
 *       / \ 
 *      / _ \
 *     / [ ] \
 *    /  [_]  \
 *   /    _    \
 *  /    (_)    \
 * /_____________\
 * -----------------------------------------------------
 * UNTESTED CODE!
 * This class has never been tried on an actual robot.
 * It may be non or partially functional.
 * Do not make any assumptions as to its behavior!
 * And don't blink.  Not even for a second.
 * -----------------------------------------------------*/

/**
 * Command to move forward or backward to a certain ultrasonic distance.
 */
public class CmdMoveUltrasonic extends Command {

	double _cm;
	
	int _msec;
	
	double _threshold;
	
	long startTime;
	
	static final int NUM_AVERAGES = 5;
	static final double OUTPUT_POWER_LIMIT = .5; //maximum allowed output power
	
	IUltrasonic ultrasonic;
	
	TankDrive drivetrain;
	
	PIDConstants pidConstants;
	
	double prevError = 0;
	double[] rollingAverage;
	
	//index of the next element to be replaced
	int backOfAverageArray = 0;
	/**
	 * @param cm how far on the ultrasonic to move.
	 * @param threshold acceptible threshold from desired distance in cm
	 * @param msec How long the move should take. If set to 0, do not time the move
	 */
    public CmdMoveUltrasonic(IUltrasonic ultrasonic, TankDrive drivetrain, double cm, double threshold, PIDConstants pidConstants, int msec)
    {
    	_cm = cm;
    	
    	if(cm < 0)
    	{
    		throw new IllegalArgumentException("Distance cannot be negative!");
    	}
    	
    	_msec = msec;
    	
    	_threshold = threshold;
    	
    	this.drivetrain = drivetrain;
    	this.ultrasonic = ultrasonic;
    	this.pidConstants = pidConstants;
    	this.drivetrain = drivetrain;
    	
    	rollingAverage = new double[NUM_AVERAGES];
    }

    protected void initialize()
    {
		drivetrain.clearEncoders();
		startTime = System.currentTimeMillis();
    	ultrasonic.setAutoPing(true);

		
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
		if(_msec != 0 && System.currentTimeMillis() - startTime >_msec)
		{
			drivetrain.stopMovement();
			AutoUtils.killRobot("Ultrasonic Move Overtime");
		}
		
		double error = ultrasonic.getDistance() - _cm;
		
		rollingAverage[backOfAverageArray] = error;
		
		++backOfAverageArray;
		if(backOfAverageArray >= NUM_AVERAGES)
		{
			backOfAverageArray = 0;
		}
		
		//calculate average (limited integral / I parameter)
		double averageSum = 0;
		
		for(int index = 0; index < NUM_AVERAGES; ++index)
		{
			averageSum += rollingAverage[index];
		}
		
		
        double output = error * pidConstants.kP + averageSum * pidConstants.kI + pidConstants.kD * (error - prevError);
		
        if(Math.abs(output) > .4)
        {
        	int prevIndex = backOfAverageArray - 1;
        	
        	if(prevIndex < 0)
        	{
        		prevIndex = NUM_AVERAGES - 1;
        	}
        	
        	//if this output was too high, remove it from the history.
        	rollingAverage[prevIndex] = 0;
        	
        	output = RobotMath.sgn(output) * OUTPUT_POWER_LIMIT;
        }
        
        output = RobotMath.clampDouble(output, -OUTPUT_POWER_LIMIT, OUTPUT_POWER_LIMIT);
        
        
        prevError = error;
        
        Log.debug("CmdMoveUltrasonic", "Error: " + error + " Output: " + output);
		drivetrain.tankDrive(output, output);
		
		//sensor reads every 500ms
		try
		{
			Thread.sleep(495);
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
    	
    	int errorsInTolerance = 0;
        for(int index = 0; index < NUM_AVERAGES; ++index)
        {
        	if(rollingAverage[index] != 0)
        	{
	        	if(Math.abs(rollingAverage[index]) < _threshold)
	        	{
	        		++errorsInTolerance;
	        	}
	        	
	        	if(errorsInTolerance >= 2)
	            {
	            	return true;
	            }

        	}
        }
        
        return false;
    }

    // Called once after isFinished returns true
    protected void end()
    {
		drivetrain.stopMovement();
		ultrasonic.setAutoPing(true);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	
    }
}
