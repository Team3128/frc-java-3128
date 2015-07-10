package org.team3128.main;

import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.hardware.lights.PWMLights;
import org.team3128.hardware.motor.logic.LightsColor;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class MainLightsTest extends MainClass {

	public PWMLights lights;
	
	public Talon testMotor;
	
	public PWM testPWM;
	
	public MainLightsTest() {
		lights = new PWMLights(0, 1, 2);
		//testMotor = new Talon(0);
		
		//testPWM = new PWM(0);
		//testPWM.setPeriodMultiplier(PeriodMultiplier.k1X);
	}

	@Override
	protected void initializeRobot(RobotTemplate robotTemplate) {


	}

	@Override
	protected void addAutoPrograms(SendableChooser autoChooser) {

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
		lights.setColor(LightsColor.new4Bit(9, 0, 9));
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
