package org.team3128;

import java.time.Duration;
import java.time.Instant;

public class AutoConfig
{
	public static void initialize(Global global)
	{
		Instant sequenceStartTime = Instant.now();
	
		Log.info("AutoConfig", "Starting Autonomous Sequence...");
		while(true)
		{
			global._rotBk.setControlTarget(90);
			global._rotFl.setControlTarget(90);
			global._rotFr.setControlTarget(90);
	
			global._drvBk.setSpeed(1.0);
			global._drvFl.setSpeed(1.0);
			global._drvFr.setSpeed(1.0);
	
			if(Instant.now().compareTo(sequenceStartTime.plus(Duration.ofMillis(750))) >= 0)
			{
				break;
			}
			else
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	
		//stop motors and wait a little
		while(true)
		{
			global._drvBk.setSpeed(0);
			global._drvFl.setSpeed(0);
			global._drvFr.setSpeed(0);

			if(Instant.now().compareTo(sequenceStartTime.plus(Duration.ofSeconds(2))) >= 0)
			{
				break;
			}
			else
			{
				try
				{
					Thread.sleep(100);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	
		//Stop the arm cocking event
		global._cockArm.cancel();
	
		while(true)
		{
			global._mShooter.setSpeed(-1.0);
	
			if(global._shooterTSensor.get())
			{
				break;
			}
			else
			{
				try
				{
					Thread.sleep(100);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	
		//Re-enable the arm cocking for teleop
		global._cockArm.start();
	
		//Drive forward into goal
		Instant desiredFinishTime = Instant.now().plus(Duration.ofMillis(2500));
		
		while(true)
		{
			global._rotBk.setControlTarget(90+global._gyr.getAngle());
			global._rotFl.setControlTarget(90+global._gyr.getAngle());
			global._rotFr.setControlTarget(90+global._gyr.getAngle());
	
			global._drvBk.setSpeed(1.0);
			global._drvFl.setSpeed(1.0);
			global._drvFr.setSpeed(1.0);
	
			if(Instant.now().compareTo(desiredFinishTime) >= 0)
			{
				break;
			}
			else
			{
				try
				{
					Thread.sleep(100);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	
		//Stop motors
		global._drvBk.setSpeed(0);
		global._drvFl.setSpeed(0);
		global._drvFr.setSpeed(0);
	
		//Do a dance
		global._lights.lightChange(Options.Alliance.RED);
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	
		global._lights.lightChange(Options.Alliance.BLUE);
	}

}
