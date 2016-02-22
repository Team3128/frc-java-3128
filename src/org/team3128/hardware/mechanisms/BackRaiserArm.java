package org.team3128.hardware.mechanisms;

import org.team3128.Log;
import org.team3128.util.units.Angle;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.Command;

public class BackRaiserArm
{
	CANTalon armMotor;
	
	public BackRaiserArm(CANTalon armMotor) {
		this.armMotor = armMotor;
	}

	//spins of output shaft per spin of the encoder
	static final double GEAR_RATIO = 1 / 180.0;
	
	//apparently, even if you invert the sensor and the output shaft of a Talon SRX,
	//that doesn't cover the target position value and you still have to invert it yourself
	static final boolean ARM_INVERTED = true;
	
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
	//see the Talon SRX Software Reference Manual
	double encDistanceToAngle(double encoderDistance)
	{
		return (encoderDistance * Angle.ROTATIONS) * GEAR_RATIO;
	}
	
	double angleToEncoderDistance(double armAngle)
	{
		return (armAngle / GEAR_RATIO) / Angle.ROTATIONS;
	}
	
	/**
	 * Stop the motor from moving
	 */
	public void setLocked()
	{
		armMotor.changeControlMode(TalonControlMode.PercentVbus);
		armMotor.set(0); //enable braking
	}
	
	/**
	 * Set the motor so that it can be controlled in position mode
	 */
	public void setForAutoControl()
	{
		armMotor.changeControlMode(TalonControlMode.Position);
	}
	
	/**
	 * Set the motor so that its speed can be set manually
	 */
	public void setForTeleop()
	{
		armMotor.changeControlMode(TalonControlMode.PercentVbus);
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
	   double targetAngle;
	   
	   final static double ANGLE_TOLERANCE = 4.0 * Angle.DEGREES;
	   //Constructor that does stuff
	   public CmdMoveToAngle(int msTillStop, double targetAngle)
	   {
		   this.targetAngle = targetAngle;
		   this.msTillStop = msTillStop;
	   }
		@Override
		protected void initialize()
		{
			setForAutoControl();
			armMotor.set((ARM_INVERTED ? -1 : 1) * angleToEncoderDistance(targetAngle));
		}

		@Override
		protected void execute() {
			Log.debug("BackRaiserArm", "Moving to encoder count " + angleToEncoderDistance(targetAngle));
		}

	@Override
	protected boolean isFinished() {

		Log.debug("BackRaiserArm", "Position error: " + armMotor.getError());
		if((timeSinceInitialized() * 1000) > msTillStop)
		{
			Log.unusual("CmdMoveToAngle", "Time Killed!");
			setLocked();
			return true;
		}
		
		else if(Math.abs(getAngle() - targetAngle) < ANGLE_TOLERANCE)
		{
			setLocked();
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
