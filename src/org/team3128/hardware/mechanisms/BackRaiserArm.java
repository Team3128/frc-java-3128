package org.team3128.hardware.mechanisms;

import org.team3128.Log;
import org.team3128.util.units.Angle;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.command.Command;

public class BackRaiserArm
{
	CANTalon armMotor;
	
	public BackRaiserArm(CANTalon armMotor) {
		this.armMotor = armMotor;
	}

	//spins of output shaft per spin of the encoder
	static final double GEAR_RATIO = 1 / 10.0;
	
	/**
	 * Get the angle of the arm in degrees.  Zero is entirely back, laying on the robot.
	 * @return
	 */
	public double getAngle()
	{
		//we assume that the arm has been homed and zero on the encoder is zero degrees
		return encDistanceToAngle(armMotor.getPosition());
	}
	
	//CTRE Mag Encoder is read in rotations
	double encDistanceToAngle(double encoderDistance)
	{
		return (encoderDistance * Angle.ROTATIONS) * GEAR_RATIO;
	}
	
	double angleToEncoderDistance(double armAngle)
	{
		return (armAngle / GEAR_RATIO) / Angle.ROTATIONS;
	}
	
	/*
	    *        _
	    *       / \ 
	    *      / _ \
	    *     / | | \
	    *    /  |_|  \
	    *   /    _    \
	    *  /    (_)    \
	    * /_____________\
	    * -----------------------------------------------------
	    * UNTESTED CODE!
	    * Probably won't work.
	    * This class has never been tried on an actual robot.
	    * It may be non or partially functional.
	    * Do not make any assumptions as to its behavior!
	    * Programmers are not responsible if it blows up the western Hemisphere.
	    * And don't blink.  Not even for a second.
	    * -----------------------------------------------------*/
   public class CmdMoveToAngle extends Command
   {
	   int msTillStop;
	   long startTime;

	   double targetAngle;
	   
	   final static double ANGLE_TOLERANCE = 1.0 * Angle.DEGREES;
	   //Constructor that does stuff
	   public CmdMoveToAngle(int msTillStop, double targetAngle)
	   {
		   this.targetAngle = targetAngle;
		   this.msTillStop = msTillStop;
	   }
		@Override
		protected void initialize()
		{
			// Starts the counter until stopping the motors
			startTime = System.currentTimeMillis();
		}

		@Override
		protected void execute() {
			armMotor.set(angleToEncoderDistance(targetAngle));
		}

	@Override
	protected boolean isFinished() {
		
		if(System.currentTimeMillis()-startTime > msTillStop)
		{
			Log.unusual("CmdMoveToAngle", "Time Killed!");
			return true;
		}
		
		else if(Math.abs(getAngle() - targetAngle) < ANGLE_TOLERANCE)
		{
			return true;
		}
		
		return false;
	}

	@Override
	protected void end() {
		//just... leave it running, I guess 
	}

	@Override
	protected void interrupted() {
		armMotor.disableControl();
	}   
   } 
}
