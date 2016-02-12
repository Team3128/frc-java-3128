package org.team3128.drive;

import static java.lang.Math.abs;

import org.team3128.Log;
import org.team3128.autonomous.AutoUtils;
import org.team3128.hardware.encoder.distance.IDistanceEncoder;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.util.Direction;
import org.team3128.util.RobotMath;
import org.team3128.util.Units;

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
    
	private MotorGroup armMotors;
	
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
    
    public TankDrive(MotorGroup leftMotors, MotorGroup rightMotors, QuadratureEncoderLink encLeft, QuadratureEncoderLink encRight, double wheelCircumfrence, double wheelBase)
    {
    	this.leftMotors = leftMotors;
    	this.rightMotors = rightMotors;
    	
    	this.encLeft = encLeft;
    	this.encRight = encRight;
    	
    	this.wheelCircumfrence = wheelCircumfrence;
    	this.wheelBase = wheelBase;
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

    	leftMotors.setControlTarget(spdL);
    	rightMotors.setControlTarget(spdR);
    }
    
    /**
     * Drive by providing motor powers for each side.
     * @param powL the left side power.
     * @param powR the right side power.
     */
    public void tankDrive(double powL, double powR)
    {
    	leftMotors.setControlTarget(powL);
    	rightMotors.setControlTarget(powR);
    }
    
	public void clearEncoders()
	{
		encLeft.clear();
		encRight.clear();
	}

	public void stopMovement()
	{
		leftMotors.setControlTarget(0);
		rightMotors.setControlTarget(0);
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
    		enc = RobotMath.cmToRotations((2.0* Math.PI * wheelBase)*(abs(_degs)/360.0), wheelCircumfrence);
    		clearEncoders();
    		
    		sideMotors.setControlTarget(AutoUtils.speedMultiplier * RobotMath.sgn(_degs) * .25);
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
    			leftMotors.setControlTarget(AutoUtils.speedMultiplier * -1 * _power * leftSideDirection);
    		}
    		
    		double speedRight = encRight.getSpeedInRPM();
    		rightSideDirection = RobotMath.sgn(speedRight);
    		if(Math.abs(speedRight) > threshold)
    		{
    			rightMotors.setControlTarget(AutoUtils.speedMultiplier * -1 * _power * rightSideDirection);
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
    			leftMotors.setControlTarget(0);
    		}
    		
    		double rightSpeedRPM = encRight.getSpeedInRPM();
    		if(RobotMath.sgn(rightSpeedRPM) != rightSideDirection || rightSpeedRPM < threshold)
    		{
    			rightSideFinished = true;
    			rightMotors.setControlTarget(0);
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
    		enc = RobotMath.floor_double_int(RobotMath.cmToRotations((Math.PI * wheelBase)*(abs(_degs)/360.0), wheelCircumfrence));
    		clearEncoders();
    		forwardMotors.setControlTarget(AutoUtils.speedMultiplier * RobotMath.sgn(_degs) * .5);
    		backwardMotors.setControlTarget(AutoUtils.speedMultiplier * -1 * RobotMath.sgn(_degs)* .5);
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
    	
    	boolean fullThrottle = false;
    	
    	boolean rightDone = false;
    	
    	boolean leftDone = false;
    	
    	/*
    	 * @param d how far to move.  Accepts negative values.
    	 * @param msec How long the move should take. If set to 0, do not time the move
    	 */
        public CmdMoveForward(double d, int msec, boolean fullSpeed)
        {
        	_cm = d;
        	
        	fullThrottle = fullSpeed;
        	
        	_msec = msec;
        }

        protected void initialize()
        {
        	
    		clearEncoders();
    		enc = abs(RobotMath.cmToRotations(_cm, wheelCircumfrence));
    		int norm = (int) RobotMath.sgn(_cm);
    		startTime = System.currentTimeMillis();
    		if(fullThrottle){
    			leftMotors.setControlTarget(AutoUtils.speedMultiplier*norm);
    			rightMotors.setControlTarget(AutoUtils.speedMultiplier*norm);
    		}else{
    			leftMotors.setControlTarget(AutoUtils.speedMultiplier * .25 * norm);
    			rightMotors.setControlTarget(AutoUtils.speedMultiplier * .25 * norm);
    		}
        }

        // Called repeatedly when this Command is scheduled to run
        protected void execute()
        {
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
   	
   	double powRight;
   	
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
       	
   		enc = abs(RobotMath.cmToRotations(_cm, wheelCircumfrence));
   		int norm = (int) RobotMath.sgn(_cm);
   		powRight = AutoUtils.speedMultiplier * .25 * norm;
       }

       protected void initialize()
       {
   		clearEncoders();
   		startTime = System.currentTimeMillis();
   		
   		leftMotors.setControlTarget(powRight); //both sides start at the same power
   		rightMotors.setControlTarget(powRight);
       }

       // Called repeatedly when this Command is scheduled to run
       protected void execute()
       {
       	//P calculation
       	double error = encLeft.getSpeedInRPM() -  encRight.getSpeedInRPM();
       	powRight += kP * error;
   		rightMotors.setControlTarget(powRight);

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
   public class CmdMoveArm extends Command{
	   
	   long startTime;
	   TankDrive drive;
	   int timeTillStop;
	   boolean moveForward;
	   double dToDrive;
	   //Constructor that does stuff
	   public CmdMoveArm(int timeTillStop, boolean moveForward, double dToDrive,TankDrive x){
		   this.timeTillStop = timeTillStop;
		   drive = x;
		   this.moveForward = moveForward;
		   this.dToDrive = dToDrive;
	   }
	@Override
	protected void initialize() {
		// Starts the counter until stopping the motors
		startTime = System.currentTimeMillis();
		armMotors.setControlTarget(-0.3);
	}

	@Override
	protected void execute() {
		int c = 0;
		for(int i = 0; i < 10000; i++){
			//Does nothing, just counts to wait for the shovel fries to go down
			//Haha I did it like this so it would be C++ geddit?
			c++;
		}
		armMotors.setControlTarget(0);
		if(moveForward){
			drive.new CmdMoveForward(dToDrive*Units.cm,1000,true);
		}else{
			stopMovement();
		}
	//	armMotors.wait();
	}

	@Override
	protected boolean isFinished() {
		if(System.currentTimeMillis()-startTime>timeTillStop){
			return true;
		}
		return false;
	}

	@Override
	protected void end() {
		armMotors.setControlTarget(0);
		stopMovement();
	}

	@Override
	protected void interrupted() {
		armMotors.setControlTarget(0);
	}   
   } 
}