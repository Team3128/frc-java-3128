package org.team3128;

import java.time.Instant;

public class AutoConfig
{
	public static void initialize(Global global)
	{
		try
		{
			@SuppressWarnings("unused")
			Instant sequenceStartTime = Instant.now();
		
			Log.info("AutoConfig", "Starting Autonomous Sequence...");
			
			global._motorLeftFront.setSpeed(.5);
			Thread.sleep(500);
			global._motorLeftBack.setSpeed(.5);
			Thread.sleep(500);
			global._motorRightFront.setSpeed(.5);
			Thread.sleep(500);
			global._motorRightBack.setSpeed(.5);
		} catch (InterruptedException e)
		{
			return;
		}
	}

}
