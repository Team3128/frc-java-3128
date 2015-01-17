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
			Thread.sleep(10);

		} catch (InterruptedException e)
		{
			return;
		}
	}

}
