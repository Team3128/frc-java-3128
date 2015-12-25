package org.team3128.listener.controller;

import org.team3128.listener.control.Axis;
import org.team3128.listener.control.Button;

/**
 * Controller object for an Xbox controller being used through the simulator.
 * @author Jamie
 */
public class ControllerSimXbox implements ControllerType
{
	public static final Button ADOWN = new Button(1, false);
	public static final Button BDOWN = new Button(2, false);
	public static final Button XDOWN = new Button(3, false);
	public static final Button YDOWN = new Button(4, false);
	public static final Button LBDOWN = new Button(5, false);
	public static final Button RBDOWN = new Button(6, false);
	public static final Button BACKDOWN = new Button(7, false);
	public static final Button STARTDOWN = new Button(8, false);
	public static final Button L3DOWN = new Button(9, false);
	public static final Button R3DOWN = new Button(10, false);
	
	public static final Button AUP = new Button(1, true);
	public static final Button BUP = new Button(2, true);
	public static final Button XUP = new Button(3, true);
	public static final Button YUP = new Button(4, true);
	public static final Button LBUP = new Button(5, true);
	public static final Button RBUP = new Button(6, true);
	public static final Button BACKUP = new Button(7, true);
	public static final Button STARTUP = new Button(8, true);
	public static final Button L3UP = new Button(9, true);
	public static final Button R3UP = new Button(10, true);

	public static final Axis JOY1X = new Axis(0);
	public static final Axis JOY1Y = new Axis(1);
	public static final Axis JOY2X = new Axis(3);
	public static final Axis JOY2Y = new Axis(4);

		
		private ControllerSimXbox()
		{
			
		}
		
		public static final ControllerSimXbox instance = new ControllerSimXbox();
		
		@Override
		public int getMaxButtonValue()
		{
			return 10;
		}

		@Override
		public int getMaxJoystickValue()
		{
			return 4;
		}
}
