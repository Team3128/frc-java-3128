package org.team3128.main;

import org.team3128.Log;
import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.hardware.lights.LightsColor;
import org.team3128.hardware.lights.LightsSequence;
import org.team3128.hardware.lights.PWMLights;
import org.team3128.util.GenericSendableChooser;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class MainLightsTest extends MainClass {

	public PWMLights lights;
	
	public Talon testMotor;
	
	public PWM testPWM;
	
	public static final LightsSequence lightsRainbowSequence;
	
	static
	{
		lightsRainbowSequence = new LightsSequence();

		lightsRainbowSequence.addStep(new LightsSequence.Step(LightsColor.red, 500, false));
		lightsRainbowSequence.addStep(new LightsSequence.Step(LightsColor.orange, 500, true));
		lightsRainbowSequence.addStep(new LightsSequence.Step(LightsColor.green, 500, true));
		lightsRainbowSequence.addStep(new LightsSequence.Step(LightsColor.new8Bit(1, 0xff, 0xff), 500, true));
		lightsRainbowSequence.addStep(new LightsSequence.Step(LightsColor.blue, 500, true));
		lightsRainbowSequence.addStep(new LightsSequence.Step(LightsColor.new8Bit(0xFF, 0x7F, 0x7F), 500, true));
		
		lightsRainbowSequence.setRepeat(true);

	}
	public MainLightsTest() {
		lights = new PWMLights(10, 11, 12);
		//testMotor = new Talon(0);
		
		//testPWM = new PWM(0);
		//testPWM.setPeriodMultiplier(PeriodMultiplier.k1X);
		
	}

	@Override
	protected void initializeRobot(RobotTemplate robotTemplate) {


		/*
		lightsTestSequence.addStep(new LightsSequence.Step(LightsColor.new4Bit(0xf, 0, 0), 750, true));
		lightsTestSequence.addStep(new LightsSequence.Step(LightsColor.new8Bit(0xb0, 0x1, 0), 750, true));
		lightsTestSequence.addStep(new LightsSequence.Step(LightsColor.new4Bit(0xf, 0xd, 0), 750, true));
		lightsTestSequence.addStep(new LightsSequence.Step(LightsColor.new4Bit(0, 0xf, 0), 750, true));
		lightsTestSequence.addStep(new LightsSequence.Step(LightsColor.new4Bit(0, 0, 0xf), 750, true));
		lightsTestSequence.addStep(new LightsSequence.Step(LightsColor.new8Bit(0x38, 0, 0xb8), 750, true));
		 */
	}

	@Override
	protected void addAutoPrograms(GenericSendableChooser<CommandGroup> autoChooser) {

	}

	@Override
	protected void initializeDisabled() {

	}

	@Override
	protected void updateDashboard() {

	}

	@Override
	protected void initializeAuto() {

	}

	@Override
	protected void initializeTeleop()
	{
		//testPWM.setRaw(1011);
		//lights.setFader(LightsColor.new4Bit(9, 0xf, 0), 32, 25);
		Log.debug("MainLightsTest", "Starting lights sequence...");
		lights.executeSequence(lightsRainbowSequence);
		//lights.setFader(LightsColor.new4Bit(0xf, 0, 0));
//		testMotor.set(1);
//		try {
//			Field centerPwmField = testMotor.getClass().getSuperclass().getSuperclass().getDeclaredField("m_centerPwm");
//			centerPwmField.setAccessible(true);
//			Log.debug("MainLightsTest", Integer.toString(centerPwmField.getInt(testMotor)));
//
//		} 
//		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
