package org.team3128.main;

import org.team3128.Log;
import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.autonomous.commands.defencecrossers.CmdGoAcrossMoat;
import org.team3128.autonomous.commands.defencecrossers.CmdGoAcrossPortcullis;
import org.team3128.autonomous.commands.defencecrossers.CmdGoAcrossShovelFries;
import org.team3128.autonomous.programs.FlyingSwallowTestAuto;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.mechanisms.BackRaiserArm;
import org.team3128.hardware.misc.Piston;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.control.POV;
import org.team3128.listener.controller.ControllerExtreme3D;
import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Main class for our 2016 robot, the Flying Swallow.
 */
public class MainFlyingSwallow extends MainClass
{
	
	public ListenerManager listenerManagerExtreme;
	public Joystick joystick;
	
	public MotorGroup leftMotors;
	public MotorGroup rightMotors;
	public MotorGroup intakeSpinner;
	public MotorGroup innerRoller;

	public QuadratureEncoderLink leftDriveEncoder;
	public QuadratureEncoderLink rightDriveEncoder;
	public PowerDistributionPanel powerDistPanel;
	
	public CANTalon backArmMotor;
	public BackRaiserArm backArm;
	
	public TankDrive drive;
	
	Piston leftGearshiftPiston, rightGearshiftPiston;
	Piston leftIntakePiston, rightIntakePiston;
	Compressor externalCompressor;
	
	//Air tracker code
	final static int NUMBER_OF_AIR_TANKS = 2;
	final static double AIR_PER_AIR_TANK = 574 ; //cm3
	final static double AIR_FOR_MICRO_PISTON = AIR_PER_AIR_TANK / 140; //140 extensions/retractions per tank at 40 psi
	final static double AIR_FOR_MEDIUM_PISTON = AIR_PER_AIR_TANK / 40; //40 extensions/retractions per tank at 40 psi
	final static double TOTAL_STARTING_AIR = NUMBER_OF_AIR_TANKS * AIR_PER_AIR_TANK;
	
	double microPistonExtensions = 0;
	double mediumPistonExtensions = 0;
	
	boolean inHighGear;
	boolean usingBackCamera;
	
	final static double DRIVE_WHEELS_GEAR_RATIO = 1/((84/20.0) * 3);
	
	enum IntakeState
	{
		STOPPED(0),
		INTAKE(1),
		OUTTAKE(-1);
		public final double motorPower;
		
		private IntakeState(double motorPower)
		{
			this.motorPower = motorPower;
		}
	}
	
	IntakeState intakeState;
	
	
	public MainFlyingSwallow()
	{
		joystick = new Joystick(0);
		listenerManagerExtreme = new ListenerManager(joystick/*, new Joystick(1)*/);	
		powerDistPanel = new PowerDistributionPanel();
		
		leftDriveEncoder = new QuadratureEncoderLink(0,	1, 128, false);
		rightDriveEncoder = new QuadratureEncoderLink(2, 3, 128, true);
		
		leftMotors = new MotorGroup();
		leftMotors.addMotor(new Talon(8));
		leftMotors.addMotor(new Talon(9));
		leftMotors.invert();
		//leftMotors.setSpeedScalar(1.25);
		
		
		rightMotors = new MotorGroup();
		rightMotors.addMotor(new Talon(0));
		rightMotors.addMotor(new Talon(1));
		
		intakeSpinner = new MotorGroup();
		intakeSpinner.addMotor(new Victor(2));
		
		innerRoller = new MotorGroup();
		innerRoller.addMotor(new Victor(3));
		innerRoller.invert();
	
		drive = new TankDrive(leftMotors, rightMotors, leftDriveEncoder, rightDriveEncoder, 7.65 * Length.in * Math.PI, DRIVE_WHEELS_GEAR_RATIO, 26.125 * Length.in);
		//
		leftGearshiftPiston = new Piston(new Solenoid(2), new Solenoid(5),true,false);
		rightGearshiftPiston = new Piston(new Solenoid(0), new Solenoid(7),true,false);

		leftIntakePiston = new Piston(new Solenoid(4), new Solenoid(3),true,false);
		rightIntakePiston = new Piston(new Solenoid(1), new Solenoid(6),true,false);
		externalCompressor = new Compressor();
		externalCompressor.stop();
		
		
		backArmMotor = new CANTalon(0);
		
		backArmMotor.setEncPosition(0);
		backArmMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		backArmMotor.setForwardSoftLimit(0);
		backArmMotor.enableForwardSoftLimit(true);

		backArm = new BackRaiserArm(backArmMotor);
	}

	protected void initializeRobot(RobotTemplate robotTemplate)
	{	
		CameraServer camera = CameraServer.getInstance();
		camera.setQuality(10);
		camera.startAutomaticCapture("cam0");
		
		robotTemplate.addListenerManager(listenerManagerExtreme);
		
		inHighGear = false;
		

        Log.info("MainFlyingSwallow", "Activating the Flying Swallow");
        Log.info("MainFlyingSwallow", "...but which one, an African or a European?");
	}

	protected void initializeDisabled()
	{
	}

	protected void initializeAuto()
	{
		backArmMotor.changeControlMode(TalonControlMode.Position);
		backArmMotor.clearIAccum();

	}
	
	protected void initializeTeleop()
	{	
		//-----------------------------------------------------------
		// Drive code, on Logitech Extreme3D joystick
		//-----------------------------------------------------------
		listenerManagerExtreme.addListener(() ->
		{
			double joyX = .4 * listenerManagerExtreme.getRawAxis(ControllerExtreme3D.TWIST);
			double joyY = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYY);
			
			drive.arcadeDrive(joyX, joyY, -listenerManagerExtreme.getRawAxis(ControllerExtreme3D.THROTTLE), listenerManagerExtreme.getRawBool(ControllerExtreme3D.TRIGGERDOWN));
		}, ControllerExtreme3D.TRIGGERUP, ControllerExtreme3D.TWIST, ControllerExtreme3D.JOYY, ControllerExtreme3D.THROTTLE, ControllerExtreme3D.TRIGGERDOWN);
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN7, () ->
		{
			powerDistPanel.clearStickyFaults();
		});
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN2, () -> 
		{
			leftGearshiftPiston.setPistonInvert();
			rightGearshiftPiston.setPistonInvert();;
			++microPistonExtensions;
			inHighGear = !inHighGear;
		
		});
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN8, () -> 
		{
			externalCompressor.start();
		});
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN9, () -> 
		{
			externalCompressor.stop();
			
			//reset air counter
			mediumPistonExtensions = 0;
			microPistonExtensions = 0;
		});
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN10, () ->
		{
			leftIntakePiston.setPistonInvert();
			rightIntakePiston.setPistonInvert();
			
			++mediumPistonExtensions;
		});
		
		listenerManagerExtreme.addListener(() -> 
		{
			intakeSpinner.setTarget(IntakeState.OUTTAKE.motorPower);
			innerRoller.setTarget(-1);

		}, new POV(0, 8), new POV(0, 1), new POV(0, 2));
		
		listenerManagerExtreme.addListener(() -> 
		{
			intakeSpinner.setTarget(IntakeState.STOPPED.motorPower);
			innerRoller.setTarget(0);

		}, new POV(0, 0));
		
		listenerManagerExtreme.addListener(() -> 
		{
			intakeSpinner.setTarget(IntakeState.INTAKE.motorPower);
			innerRoller.setTarget(0.5);

		}, new POV(0, 4), new POV(0, 5), new POV(0, 6));
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN5, () -> {
			backArmMotor.set(.5);	
		});
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN6, () -> {
			backArmMotor.set(-.5);	
		});
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.UP6, () -> {
			backArmMotor.set(0);	
		});

		listenerManagerExtreme.addListener(ControllerExtreme3D.UP5, () -> {
			backArmMotor.set(0);	
		});
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN11, () -> {
			backArmMotor.setEncPosition(0);	
			backArmMotor.enableForwardSoftLimit(true);
		});

		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN12, () -> {
			backArmMotor.enableForwardSoftLimit(false);
		});

		backArmMotor.changeControlMode(TalonControlMode.PercentVbus);
		backArmMotor.clearIAccum();
		backArmMotor.set(0);
		intakeSpinner.setTarget(0);
		intakeState = IntakeState.STOPPED;
		
	}

	@Override
	protected void addAutoPrograms(SendableChooser autoChooser)
	{
		autoChooser.addDefault("Test Back Arm", new FlyingSwallowTestAuto(this));
		autoChooser.addObject("Go Across Portcullis", new CmdGoAcrossPortcullis(drive, backArm));
		autoChooser.addObject("Go Across Shovel Fries", new CmdGoAcrossShovelFries(drive, leftIntakePiston, rightIntakePiston));
		autoChooser.addObject("Go Across Moat", new CmdGoAcrossMoat(this));
		autoChooser.addObject("Go Across Portcullis", new CmdGoAcrossPortcullis(drive, backArm));
		autoChooser.addObject("Go Across Portcullis", new CmdGoAcrossPortcullis(drive, backArm));
		autoChooser.addObject("Go Across Portcullis", new CmdGoAcrossPortcullis(drive, backArm));


		
		//shift to low gear
		shiftToLowGear();

	}

	@Override
	protected void updateDashboard()
	{
		//SmartDashboard.putNumber("Total Current: ", powerDistPanel.getTotalCurrent());
		
		double airLeft /* cm3 */ = TOTAL_STARTING_AIR - (microPistonExtensions * AIR_FOR_MICRO_PISTON) - (mediumPistonExtensions * AIR_FOR_MEDIUM_PISTON);
		
		SmartDashboard.putNumber("Air Left (cm³):", airLeft);
		SmartDashboard.putNumber("Shifts Left:", Math.floor(airLeft / AIR_FOR_MICRO_PISTON));
		SmartDashboard.putNumber("Intake Movements Left:", Math.floor(airLeft / AIR_FOR_MEDIUM_PISTON));
		
		SmartDashboard.putString("Current Gear", inHighGear ? "High" : "Low");
		
		SmartDashboard.putNumber("Back Arm Angle:", backArm.getAngle());
		SmartDashboard.putNumber("Left Drive Enc Distance:", leftDriveEncoder.getDistanceInDegrees());
		SmartDashboard.putNumber("Right Drive Enc Distance:", rightDriveEncoder.getDistanceInDegrees());
		
		SmartDashboard.putNumber("POV:", joystick.getPOV());



	}
	
	void shiftToHighGear()
	{
		rightGearshiftPiston.setPistonOff();
		leftGearshiftPiston.setPistonOff();
		inHighGear = true;
	}
	
	
	void shiftToLowGear()
	{
		rightGearshiftPiston.setPistonOn();
		leftGearshiftPiston.setPistonOn();
		inHighGear = false;
	}
	
	public class CmdUpshift extends Command
	{

		@Override
		protected void initialize() {
			shiftToHighGear();
			
		}

		@Override
		protected void execute() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected boolean isFinished() {
			return timeSinceInitialized() > .5;
		}

		@Override
		protected void end() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void interrupted() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class CmdDownshift extends Command
	{

		@Override
		protected void initialize() {
			shiftToLowGear();
			
		}

		@Override
		protected void execute() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected boolean isFinished() {
			return timeSinceInitialized() > .5;
		}

		@Override
		protected void end() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void interrupted() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
