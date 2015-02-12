package org.team3128.listener.controller;

import org.team3128.listener.control.Axis;
import org.team3128.listener.control.Button;

/**
 * Class to define the buttons and axes of a Logitech attack joystick
 * @author Jamie
 */
public class ControllerExtreme3D implements ControllerType
{
		public static final Button TRIGGERDOWN = new Button(1, false);
		public static final Button DOWN2 = new Button(2, false);
		public static final Button DOWN3 = new Button(3, false);
		public static final Button DOWN4 = new Button(4, false);
		public static final Button DOWN5 = new Button(5, false);
		public static final Button DOWN6 = new Button(6, false);
		public static final Button DOWN7 = new Button(7, false);
		public static final Button DOWN8 = new Button(8, false);
		public static final Button DOWN9 = new Button(9, false);
		public static final Button DOWN10 = new Button(10, false);
		public static final Button DOWN11 = new Button(11, false);
		public static final Button DOWN12 = new Button(12, false);
		
		public static final Button TRIGGERUP = new Button(1, true);
		public static final Button UP2 = new Button(2, true);
		public static final Button UP3 = new Button(3, true);
		public static final Button UP4 = new Button(4, true);
		public static final Button UP5 = new Button(5, true);
		public static final Button UP6 = new Button(6, true);
		public static final Button UP7 = new Button(7, true);
		public static final Button UP8 = new Button(8, true);
		public static final Button UP9 = new Button(9, true);
		public static final Button UP10 = new Button(10, true);
		public static final Button UP11 = new Button(11, true);
		public static final Button UP12 = new Button(11, true);
		

		public static final Axis JOYX = new Axis(0);
		public static final Axis JOYY = new Axis(1);
		public static final Axis TWIST = new Axis(2);
		public static final Axis THROTTLE = new Axis(3);

		
		private ControllerExtreme3D()
		{
			
		}
		
		public static final ControllerExtreme3D instance = new ControllerExtreme3D();

		@Override
		public int getMaxButtonValue()
		{
			return 12;
		}

		@Override
		public int getMaxJoystickValue()
		{
			return 3;
		}
}
