package org.team3128.autonomous;

import java.time.Instant;

import org.team3128.Global;
import org.team3128.Log;

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
