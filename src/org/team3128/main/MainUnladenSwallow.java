package org.team3128.main;

import org.team3128.Log;
import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.autonomous.commands.defencecrossers.CmdGoAcrossLowBar;
import org.team3128.autonomous.commands.defencecrossers.CmdGoAcrossMoat;
import org.team3128.autonomous.commands.defencecrossers.CmdGoAcrossPortcullis;
import org.team3128.autonomous.commands.defencecrossers.CmdGoAcrossRamparts;
import org.team3128.autonomous.commands.defencecrossers.CmdGoAcrossRockWall;
import org.team3128.autonomous.commands.defencecrossers.CmdGoAcrossRoughTerrain;
import org.team3128.autonomous.commands.defencecrossers.CmdGoAcrossShovelFries;
import org.team3128.autonomous.commands.defencecrossers.StrongholdStartingPosition;
import org.team3128.autonomous.programs.UnladenSwallowTestAuto;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.lights.LightsColor;
import org.team3128.hardware.lights.PWMLights;
import org.team3128.hardware.mechanisms.BackRaiserArm;
import org.team3128.hardware.mechanisms.TwoSpeedGearshift;
import org.team3128.hardware.misc.Piston;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.control.POV;
import org.team3128.listener.controller.ControllerExtreme3D;
import org.team3128.util.GenericSendableChooser;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Main class for our 2016 robot, the Unladen Swallow.
 */
public abstract class MainUnladenSwallow extends MainClass
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
	
	public PWMLights lights;
	
	public TwoSpeedGearshift gearshift;
	
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
	
	boolean usingBackCamera;
	
	final static double DRIVE_WHEELS_GEAR_RATIO = 1/((84/20.0) * 3);
	
	//offset from zero degrees for the heading readout
	double robotAngleReadoutOffset;
	
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
	
	public GenericSendableChooser<CommandGroup> defenseChooser;
	public GenericSendableChooser<StrongholdStartingPosition> fieldPositionChooser;
	public GenericSendableChooser<CommandGroup> scoringChooser;
	
	public MainUnladenSwallow()
	{
		defenseChooser = new GenericSendableChooser<>();
		fieldPositionChooser = new GenericSendableChooser<>();
		scoringChooser = new GenericSendableChooser<>();
		
		fieldPositionChooser.addDefault("Far Right (low bar)", StrongholdStartingPosition.FAR_LEFT);
		fieldPositionChooser.addObject("Center Right", StrongholdStartingPosition.CENTER_RIGHT);
		fieldPositionChooser.addObject("Middle", StrongholdStartingPosition.MIDDLE);
		fieldPositionChooser.addObject("Center Left", StrongholdStartingPosition.CENTER_LEFT);
		fieldPositionChooser.addObject("Far Left", StrongholdStartingPosition.FAR_LEFT);


	}

	protected void initializeRobot(RobotTemplate robotTemplate)
	{	
		CameraServer camera = CameraServer.getInstance();
		camera.setQuality(10);
		camera.startAutomaticCapture("cam0");
		
		robotTemplate.addListenerManager(listenerManagerExtreme);
				
        Log.info("MainUnladenSwallow", "Activating the Unladen Swallow");
        Log.info("MainUnladenSwallow", "...but which one, an African or a European?");
	}

	protected void initializeDisabled()
	{
		// clear the motor speed set in autonomous, if there was one (because the robot was manually stopped)
		drive.arcadeDrive(0, 0, 0, false);
	}

	protected void initializeAuto()
	{
		backArm.setLocked();
		backArmMotor.clearIAccum();

	}
	
	protected void initializeTeleop()
	{	
		//-----------------------------------------------------------
		// Drive code, on Logitech Extreme3D joystick
		//-----------------------------------------------------------
		listenerManagerExtreme.addListener(() ->
		{
			double joyX = .5 * listenerManagerExtreme.getRawAxis(ControllerExtreme3D.TWIST);
			double joyY = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYY);
			
			drive.arcadeDrive(joyX, joyY, -listenerManagerExtreme.getRawAxis(ControllerExtreme3D.THROTTLE), listenerManagerExtreme.getRawBool(ControllerExtreme3D.TRIGGERDOWN));
		}, ControllerExtreme3D.TRIGGERUP, ControllerExtreme3D.TWIST, ControllerExtreme3D.JOYY, ControllerExtreme3D.THROTTLE, ControllerExtreme3D.TRIGGERDOWN);
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN7, () ->
		{
			powerDistPanel.clearStickyFaults();
		});
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN2, () -> 
		{
			gearshift.shiftToOtherGear();
		
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
			
			mediumPistonExtensions += 2;
		});
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN11, () ->
		{
			robotAngleReadoutOffset = drive.getRobotAngle();
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

		backArm.setForTeleop();
		backArmMotor.clearIAccum();
		backArmMotor.set(0);
		intakeSpinner.setTarget(0);
		intakeState = IntakeState.STOPPED;
		lights.setColor(LightsColor.blue);
	}

	@Override
	protected void addAutoPrograms(SendableChooser autoChooser)
	{
		autoChooser.addDefault("Stronghold Composite Auto", new UnladenSwallowTestAuto(this));
		//autoChooser.addObject("Test Back Arm", new StrongholdCompositeAuto(this));
		
		//-------------------------------------------------------------------------------

		defenseChooser.addObject("Portcullis", new CmdGoAcrossPortcullis(drive, backArm));
		defenseChooser.addObject("Shovel Fries", new CmdGoAcrossShovelFries(drive, leftIntakePiston, rightIntakePiston));
		defenseChooser.addObject("Moat", new CmdGoAcrossMoat(this));
		defenseChooser.addObject("Rock Wall", new CmdGoAcrossRockWall(drive));
		defenseChooser.addObject("Low Bar", new CmdGoAcrossLowBar(this));
		defenseChooser.addObject("Ramparts", new CmdGoAcrossRamparts(this));
		defenseChooser.addObject("Rough Terrain", new CmdGoAcrossRoughTerrain(this));

		scoringChooser.addDefault("No Scoring", null);
		scoringChooser.addDefault("Encoder-Based (live reckoning) Scoring", null);
		scoringChooser.addDefault("Vision-Targeted Scoring (experimental)", null);

		
		//shift to low gear
		gearshift.shiftToLow();

	}

	@Override
	protected void updateDashboard()
	{
		SmartDashboard.putNumber("Total Current: ", powerDistPanel.getTotalCurrent());
		
		double airLeft /* cm3 */ = TOTAL_STARTING_AIR - (microPistonExtensions * AIR_FOR_MICRO_PISTON) - (mediumPistonExtensions * AIR_FOR_MEDIUM_PISTON);
		
		SmartDashboard.putNumber("Air Left (cm³):", airLeft);
		SmartDashboard.putNumber("Shifts Left:", Math.floor(airLeft / AIR_FOR_MICRO_PISTON));
		SmartDashboard.putNumber("Intake Movements Left:", Math.floor(airLeft / AIR_FOR_MEDIUM_PISTON));
		
		SmartDashboard.putString("Current Gear", gearshift.isInHighGear() ? "High" : "Low");
		
		SmartDashboard.putNumber("Back Arm Angle:", backArm.getAngle());
		Log.debug("MainUnladenSwallow", String.format("Right Drive Enc Distance: %f, Speed: %f", rightDriveEncoder.getDistanceInDegrees(), rightDriveEncoder.getSpeedInRPM()));
		SmartDashboard.putNumber("Left Drive Enc Distance:", leftDriveEncoder.getDistanceInDegrees());
		
		SmartDashboard.putNumber("Robot Heading", drive.getRobotAngle() - robotAngleReadoutOffset);



	}
	
	/**
	 * Command to set the position of the ball intake.
	 * @author Jamie
	 *
	 */
	public class CmdSetIntake extends Command
	{
		boolean setToUp;
		
		
		public CmdSetIntake(boolean up)
		{
			setToUp = up;
		}

		@Override
		protected void initialize()
		{
			
		}

		@Override
		protected void execute() {
			if(setToUp)
			{
				leftIntakePiston.setPistonOn();
				rightIntakePiston.setPistonOn();
			}
			else
			{
				leftIntakePiston.setPistonOff();
				rightIntakePiston.setPistonOff();
			}
		}

		@Override
		protected boolean isFinished() {
			return timeSinceInitialized() > 1;
		}

		@Override
		protected void end() {
			
		}

		@Override
		protected void interrupted() {
			
		}
		
	}
	
	

}
