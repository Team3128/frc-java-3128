package org.team3128.listener.controller;

import org.team3128.listener.control.Axis;
import org.team3128.listener.control.Button;

/**
 * Controller object for a Saitek X55 flight simulator joystick.
 * @author Jamie
 */
public class ControllerSaitekX55
{
		public static final Button TRIGGERDOWN = new Button(1, false);
		public static final Button ADOWN = new Button(2, false);
		public static final Button BDOWN = new Button(3, false);
		public static final Button CDOWN = new Button(4, false);
		public static final Button DDOWN = new Button(5, false);
		public static final Button LEVERDOWN = new Button(6, false);
		public static final Button HAT1TOPDOWN = new Button(7, false);
		public static final Button HAT1RIGHTDOWN = new Button(8, false);
		public static final Button HAT1BOTTOMDOWN = new Button(9, false);
		public static final Button HAT1LEFTDOWN = new Button(10, false);
		public static final Button HAT2TOPDOWN = new Button(11, false);
		public static final Button HAT2RIGHTDOWN = new Button(12, false);
		public static final Button HAT2BOTTOMDOWN = new Button(13, false);
		public static final Button HAT2LEFTDOWN = new Button(14, false);
		
		public static final Button TRIGGERUP = new Button(1, true);
		public static final Button AUP = new Button(2, true);
		public static final Button BUP = new Button(3, true);
		public static final Button CUP = new Button(4, true);
		public static final Button DUP = new Button(5, true);
		public static final Button LEVERUP = new Button(6, true);
		public static final Button HAT1TOPUP = new Button(7, true);
		public static final Button HAT1RIGHTUP = new Button(8, true);
		public static final Button HAT1BOTTOMUP = new Button(9, true);
		public static final Button HAT1LEFTUP = new Button(10, true);
		public static final Button HAT2TOPUP = new Button(11, true);
		public static final Button HAT2RIGHTUP = new Button(12, true);
		public static final Button HAT2BOTTUMUP = new Button(13, true);
		public static final Button HAT2LEFTUP = new Button(14, true);
		

		public static final Axis JOYX = new Axis(0);
		public static final Axis JOYY = new Axis(1);
		public static final Axis TWIST = new Axis(2);

		
		private ControllerSaitekX55()
		{
			
		}
		
		public static final ControllerSaitekX55 instance = new ControllerSaitekX55();
}
