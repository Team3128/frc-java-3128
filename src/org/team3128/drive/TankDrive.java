package org.team3128.drive;

import static java.lang.Math.abs;

import org.team3128.Log;
import org.team3128.autonomous.AutoUtils;
import org.team3128.hardware.encoder.distance.IDistanceEncoder;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.util.Direction;
import org.team3128.util.RobotMath;
import org.team3128.util.units.Angle;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Class which represents a tank drive on a robot.
 * 
 * Also provides commands for autonomous movement.
 * @author Jamie
 *
 */
public class TankDrive
{
	private MotorGroup leftMotors;
    	
    private MotorGroup rightMotors;
    
	private QuadratureEncoderLink encLeft;
	private QuadratureEncoderLink encRight;
	
    
    /**
     * circumference of wheels in cm
     */
    public final double wheelCircumfrence;
    
    /**
     * horizontal distance between wheels in cm
     */
    public final double wheelBase;
    
    /**
     * Ratio between turns of the wheels to turns of the encoder
     */
    private double gearRatio;
    
    public double getGearRatio()
	{
		return gearRatio;
	}

	public void setGearRatio(double gearRatio)
	{
		this.gearRatio = gearRatio;
	}

	/**
     * 
     * @param leftMotors The motors on the left side of the robot
     * @param rightMotors The motors on the riht side of the robot.
     * @param encLeft The encoder on the left motors
     * @param encRight The encoder on the right motors
     * @param wheelCircumfrence The circumference of the wheel
     * @param gearRatio The gear ratio of the turns of the wheels per turn of the encoder shaft
     * @param wheelBase The diagonal distance between one front wheel and the opposite back wheel.
     */
    public TankDrive(MotorGroup leftMotors, MotorGroup rightMotors, QuadratureEncoderLink encLeft, QuadratureEncoderLink encRight, double wheelCircumfrence, double gearRatio, double wheelBase)
    {
    	this.leftMotors = leftMotors;
    	this.rightMotors = rightMotors;
    	
    	this.encLeft = encLeft;
    	this.encRight = encRight;
    	
    	this.wheelCircumfrence = wheelCircumfrence;
    	this.wheelBase = wheelBase;
    	this.gearRatio = gearRatio;
    	
    	if(gearRatio <= 0)
    	{
    		throw new IllegalArgumentException("Invalid gear ratio");
    	}
    }
    
	//threshold below which joystick movements are ignored.
	final static double thresh = 0.2;
	
	
	/**
	 * Update the motor outputs with the given control values.
	 * @param joyX horizontal control input
	 * @param joyY vertical control input
	 * @param throttle throttle control input scaled between 1 and -1 (-.8 is 10 %, 0 is 50%, 1.0 is 100%)
	 */
    public void arcadeDrive(double joyX, double joyY, double throttle, boolean fullSpeed)
    {
    	
        double spdL, spdR;
    	//read joystick values
    	joyX = Math.abs(joyX) > thresh ? -1 * joyX : 0.0;
    	
       	joyY = Math.abs(joyY) > thresh ? -1 * joyY : 0.0;
    	
    	if(!fullSpeed)
    	{
    		joyY *= .65;
    	}
    	else
    	{
    		joyY *= 1;
    	}
    	
    	//scale from 1 to -1 to 1 to 0
    	throttle =  ( throttle + 1) / 2;

    	if(throttle < .3)
    	{
    		throttle = .3;
    	}
    	else if(throttle > .8)
    	{
    		throttle = 1;
    	}
    	
    	joyY *= throttle;
    	joyX *= throttle;
    	
    	spdR = RobotMath.makeValidPower(joyY + joyX);
    	spdL = RobotMath.makeValidPower(joyY - joyX);
    	
    	//Log.debug("TankDrive", "x1: " + joyX + " throttle: " + throttle + " spdR: " + spdR + " spdL: " + spdL);

    	leftMotors.setTarget(spdL);
    	rightMotors.setTarget(spdR);
    }
    
    /**
     * Drive by providing motor powers for each side.
     * @param powL the left side power.
     * @param powR the right side power.
     */
    public void tankDrive(double powL, double powR)
    {
    	leftMotors.setTarget(powL);
    	rightMotors.setTarget(powR);
    }
    
	public void clearEncoders()
	{
		encLeft.clear();
		encRight.clear();
	}

	public void stopMovement()
	{
		leftMotors.setTarget(0);
		rightMotors.setTarget(0);
	}
	
	/**
	 * Get the estimated angle that the robot has turned since the encoders were last reset, based on the relative distances of each side.
	 * 
	 * Range: [0, 360)
	 * 0 degrees is straight ahead.
	 * @return
	 */
	public double getRobotAngle()
	{
		double leftDist = encDistanceToCm(encLeft.getDistanceInDegrees());
		double rightDist = encDistanceToCm(encRight.getDistanceInDegrees());
		
		double difference = leftDist - rightDist;
		
		//if the right side has moved more than the left, than the value will be negative.
		//this is OK, normalizeAngle() takes care of that.
		return RobotMath.normalizeAngle((difference / (2 * Math.PI * wheelBase)) * Angle.ROTATIONS);
	}
	
	/**
	 * Convert cm of robot movement to encoder movement in degrees
	 * @param cm
	 * @param wheelCircumference the circumference of the wheels
	 * @return
	 */
	double cmToEncDegrees(double cm)
	{
		return (cm * 360) / (wheelCircumfrence * gearRatio);
	}
	
	/**
	 * Convert cm of robot movement to encoder rotations
	 * @param cm
	 * @param wheelCircumference the circumference of the wheels
	 * @return
	 */
	double encDistanceToCm(double encDistance)
	{
		return (encDistance / 360) * wheelCircumfrence * gearRatio;
	}
	
	
	
    /**
     * Command to to an arc turn in the specified amount of degrees.
     * 
     * Sets the opposite motors from the direction provided, so turning LEFT would set the RIGHT motors.
     * 
     * NOTE: currently requires that the front or back wheels be omni wheels for accurate turning.
     */
    public class CmdArcTurn extends Command {

    	float _degs;
    	
    	int _msec;
    	
    	long startTime;
    	
    	/**
    	 * rotations that the move will take
    	 */
    	double enc;
    	
    	QuadratureEncoderLink sideEncoder;
    	
    	QuadratureEncoderLink otherSideEncoder;
    	
    	MotorGroup sideMotors;
    	
    	MotorGroup otherSideMotors;
    	
    	/**
    	 * @param degs how far to turn in degrees.  Accepts negative values.
    	 * @param msec How long the move should take. If set to 0, do not time the move.
    	 */
        public CmdArcTurn(float degs, int msec, Direction dir)
        {
        	_degs = degs;
        	
        	_msec = msec;
        	
        	if(dir == Direction.RIGHT)
        	{
        		sideEncoder = encLeft;
        		otherSideEncoder = encRight;
        		
        		sideMotors = leftMotors;
        		otherSideMotors = rightMotors;
        	}
        	else
        	{
        		sideEncoder = encRight;
        		otherSideEncoder = encLeft;
        		
        		sideMotors = rightMotors;
        		otherSideMotors = leftMotors;
        	}
        }

        protected void initialize()
        {
    		enc = cmToEncDegrees((2.0* Math.PI * wheelBase)*(abs(_degs)/360.0));
    		clearEncoders();
    		
    		sideMotors.setTarget(AutoUtils.speedMultiplier * RobotMath.sgn(_degs) * .25);
    		startTime = System.currentTimeMillis();
        }

        // Called repeatedly when this Command is scheduled to run
        protected void execute()
        {
    		if(_msec != 0 && System.currentTimeMillis() - startTime >_msec)
    		{
    			stopMovement();
    			AutoUtils.killRobot("Arc Turn Overtime");
    		}
    		
    		//otherSideMotors.setControlTarget(-1 * RobotMath.getEstCIMPowerForRPM(otherSideEncoder.getSpeedInRPM()));
    		
        }

        // Make this return true when this Command no longer needs to run execute()
        protected boolean isFinished()
        {
        	//System.out.println(sideEncoder.getDistance());
            return Math.abs(sideEncoder.getDistanceInDegrees()) >= enc;
        }

        // Called once after isFinished returns true
        protected void end()
        {
    		stopMovement();
        }

        // Called when another command which requires one or more of the same
        // subsystems is scheduled to run
        protected void interrupted()
        {
        	
        }
    }
    
    /*
     *       /^\ 
     *      / _ \
     *     / | | \
     *    /  |_|  \
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
     * Command to stop the robot
     */
    public class CmdBrake extends Command 
    {
    	long startTime;
    	
    	long _msec;
    	
    	double _power;
    	
    	boolean timedOut = false;
    	
    	boolean leftSideFinished = false;
    	
    	//sgn of the left side speed
    	double leftSideDirection;
    	
    	boolean rightSideFinished = false;
    	
    	//sgn of the right side speed
    	double rightSideDirection;
    	
    	
    	/**
    	 * 
    	 * @param power Motor power to break with.
    	 * @param msec
    	 */
        public CmdBrake(double power, int msec)
        {
        	_power = power;
        	
        	if(power <= 0)
        	{
        		throw new IllegalArgumentException("The power must be greater than 0!");
        	}
        	
        	_msec = msec;
        }

        protected void initialize()
        {
    		startTime = System.currentTimeMillis();
    		
    		double speedLeft = encLeft.getSpeedInRPM();
    		leftSideDirection = RobotMath.sgn(speedLeft);
    		if(Math.abs(speedLeft) > threshold)
    		{
    			leftMotors.setTarget(AutoUtils.speedMultiplier * -1 * _power * leftSideDirection);
    		}
    		
    		double speedRight = encRight.getSpeedInRPM();
    		rightSideDirection = RobotMath.sgn(speedRight);
    		if(Math.abs(speedRight) > threshold)
    		{
    			rightMotors.setTarget(AutoUtils.speedMultiplier * -1 * _power * rightSideDirection);
    		}
        }

        // Called repeatedly when this Command is scheduled to run
        protected void execute()
        {
        	//System.out.println(encLeft.getSpeedInRPM() + " " + encRight.getSpeedInRPM());
    		if(_msec != 0 && (System.currentTimeMillis() > startTime + _msec))
    		{
    			//one of these commands timing out is not neccesarily fatal
    			Log.unusual("CmdBrake", "Timed Out!");
    			timedOut = true;
    		}
    		
    		double leftSpeedRPM = encLeft.getSpeedInRPM();
    		if(RobotMath.sgn(leftSpeedRPM) != leftSideDirection || leftSpeedRPM < threshold)
    		{
    			leftSideFinished = true;
    			leftMotors.setTarget(0);
    		}
    		
    		double rightSpeedRPM = encRight.getSpeedInRPM();
    		if(RobotMath.sgn(rightSpeedRPM) != rightSideDirection || rightSpeedRPM < threshold)
    		{
    			rightSideFinished = true;
    			rightMotors.setTarget(0);
    		}
        }
        
        final static double threshold = 20; // RPM

        // Make this return true when this Command no longer needs to run execute()
        protected boolean isFinished()
        {
            return timedOut || (leftSideFinished && rightSideFinished);
        }

        // Called once after isFinished returns true
        protected void end()
        {
    		stopMovement();
        }

        // Called when another command which requires one or more of the same
        // subsystems is scheduled to run
        protected void interrupted()
        {
        	
        }
    }
    
    /**
     * Command to to an arc turn in the specified amount of degrees.
     * 
     * Sets the opposite motors from the direction provided, so turning LEFT would set the RIGHT motors.
     */
    public class CmdInPlaceTurn extends Command {

    	float _degs;
    	
    	int _msec;
    	
    	long startTime;
    	
    	/**
    	 * rotations that the move will take
    	 */
    	double enc;
    	
    	IDistanceEncoder sideEncoder;
    	
    	MotorGroup forwardMotors;
    	
    	MotorGroup backwardMotors;
    	
    	/**
    	 * @param degs how far to turn in degrees.  Accepts negative values.
    	 * @param msec How long the move should take. If set to 0, do not time the move
    	 */
        public CmdInPlaceTurn(float degs, int msec, Direction dir)
        {
        	_degs = degs;
        	
        	_msec = msec;
        	
        	if(dir == Direction.RIGHT)
        	{
        		sideEncoder = encLeft;
        		forwardMotors = leftMotors;
        		backwardMotors = rightMotors;
        	}
        	else
        	{
        		sideEncoder = encRight;
        		forwardMotors = rightMotors;
        		backwardMotors = leftMotors;
        	}
        }

        protected void initialize()
        {
    		enc = RobotMath.floor_double_int(cmToEncDegrees((Math.PI * wheelBase)*(abs(_degs)/360.0)));
    		clearEncoders();
    		forwardMotors.setTarget(AutoUtils.speedMultiplier * RobotMath.sgn(_degs) * .5);
    		backwardMotors.setTarget(AutoUtils.speedMultiplier * -1 * RobotMath.sgn(_degs)* .5);
    		startTime = System.currentTimeMillis();
        }

        // Called repeatedly when this Command is scheduled to run
        protected void execute()
        {
    		if(_msec != 0 && System.currentTimeMillis() - startTime >_msec)
    		{
    			stopMovement();
    			AutoUtils.killRobot("Arc Turn Overtime");
    		}
        }

        // Make this return true when this Command no longer needs to run execute()
        protected boolean isFinished()
        {    	
            return Math.abs(sideEncoder.getDistanceInDegrees()) >= enc;
        }

        // Called once after isFinished returns true
        protected void end()
        {
    		stopMovement();
        }

        // Called when another command which requires one or more of the same
        // subsystems is scheduled to run
        protected void interrupted()
        {
        	
        }
    }
    
    
    
    /**
     * Command to move forward the given amount of centimeters
     */
    
    
    public class CmdMoveForward extends Command {

    	double _cm;
    	
    	int _msec;
    	
    	long startTime;
    	
    	/**
    	 * rotations that the move will take
    	 */
    	double enc;
    	
    	double power;
    	    	
    	boolean rightDone = false;
    	
    	boolean leftDone = false;
    	
    	/**
    	 * @param d how far to move.  Accepts negative values.
    	 * @param msec How long the move should take. If set to 0, do not time the move
    	 */
        public CmdMoveForward(double d, int msec, boolean fullSpeed)
        {
        	_cm = d;
        	
        	power = fullSpeed ? 1 : .50;
        	
        	_msec = msec;
        }
        
    	/**
    	 * @param d how far to move.  Accepts negative values.
    	 * @param msec How long the move should take. If set to 0, do not time the move
    	 */
        public CmdMoveForward(double d, int msec, double power)
        {
        	_cm = d;
        	
        	this.power = power;
        	
        	_msec = msec;
        }

        protected void initialize()
        {
        	
    		clearEncoders();
    		enc = abs(cmToEncDegrees(_cm));
    		int norm = (int) RobotMath.sgn(_cm);
    		startTime = System.currentTimeMillis();
			leftMotors.setTarget(norm * power);
			rightMotors.setTarget(norm * power);
        }

        // Called repeatedly when this Command is scheduled to run
        protected void execute()
        {
    		if(_msec != 0 && (timeSinceInitialized() * 1000) >_msec)
    		{
    			stopMovement();
    			AutoUtils.killRobot("Move Overtime");
    		}
        }

        // Make this return true when this Command no longer needs to run execute()
        protected boolean isFinished()
        {
        	leftDone = Math.abs(encLeft.getDistanceInDegrees()) > enc;
        	rightDone = Math.abs(encRight.getDistanceInDegrees()) > enc;
        	
            return leftDone || rightDone;
        }

        // Called once after isFinished returns true
        protected void end()
        {
    		stopMovement();
        	
        	if(leftDone && encRight.getDistanceInDegrees() > 0)
        	{
        		Log.debug("CmdMoveForward", "The right side went at " + ((encRight.getDistanceInDegrees() * 100.0) / encRight.getDistanceInDegrees()) + "% of the left side");
        	}
        	else if(encLeft.getDistanceInDegrees() > 0)
        	{
        		Log.debug("CmdMoveForward", "The left side went at " + ((encLeft.getDistanceInDegrees() * 100.0) / encLeft.getDistanceInDegrees()) + "% of the right side");
        	}
        }

        // Called when another command which requires one or more of the same
        // subsystems is scheduled to run
        protected void interrupted()
        {
        	
        }
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
    * This class has never been tried on an actual robot.
    * It may be non or partially functional.
    * Do not make any assumptions as to its behavior!
    * And don't blink.  Not even for a second.
    * -----------------------------------------------------*/

   /**
    * Command to move forward the given amount of centimeters.
    * 
    * It uses a feedback loop to make the robot drive straight.
    */
   public class CmdMoveStraightForward extends Command {

   	double _cm;
   	
   	int _msec;
   	
   	long startTime;
   	
   	double kP;
   	
   	/**
   	 * rotations that the move will take
   	 */
   	double enc;
   	
   	boolean rightDone = false;
   	
   	boolean leftDone = false;
   	
   	double pow;
   	
   	/**
   	 * @param d how far to move.  Accepts negative values.
   	 * @param kP The konstant of proportion.  Scales how the feedback affects the wheel speeds.
   	 * @param msec How long the move should take. If set to 0, do not time the move
   	 */
       public CmdMoveStraightForward(double d, double kP, int msec)
       {
       	_cm = d;
       	
       	_msec = msec;
       	
       	this.kP = kP;
       	
   		enc = abs(cmToEncDegrees(_cm));
   		int norm = (int) RobotMath.sgn(_cm);
   		pow = AutoUtils.speedMultiplier * .25 * norm;
       }

       protected void initialize()
       {
   		clearEncoders();
   		startTime = System.currentTimeMillis();
   		
   		leftMotors.setTarget(pow); //both sides start at the same power
   		rightMotors.setTarget(pow);
       }

       // Called repeatedly when this Command is scheduled to run
       protected void execute()
       {
       	//P calculation
       	double error = encLeft.getSpeedInRPM() -  encRight.getSpeedInRPM();
       	pow += kP * error;
   		rightMotors.setTarget(pow);

   		if(_msec != 0 && System.currentTimeMillis() - startTime >_msec)
   		{
   			stopMovement();
   			AutoUtils.killRobot("Move Overtime");
   		}
       }

       // Make this return true when this Command no longer needs to run execute()
       protected boolean isFinished()
       {
       	leftDone = Math.abs(encLeft.getDistanceInDegrees()) > enc;
       	rightDone = Math.abs(encRight.getDistanceInDegrees()) > enc;
       	
           return leftDone || rightDone;
       }

       // Called once after isFinished returns true
       protected void end()
       {
   		stopMovement();

       	Log.debug("CmdMoveStraightForward", "The right side went at " + ((encRight.getDistanceInDegrees() * 100.0) / encRight.getDistanceInDegrees()) + "% of the left side");
       }

       // Called when another command which requires one or more of the same
       // subsystems is scheduled to run
       protected void interrupted()
       {
       	
       }
   }
   
}