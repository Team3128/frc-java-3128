package org.team3128.listener;

/**
 * Enum which represents everything that can be read from the controller
 * @author Jamie
 */
public enum Listenable
{
		ADOWN(1, false),
		BDOWN(2, false),
		XDOWN(3, false),
		YDOWN(4, false),
		LBDOWN(5, false),
		RBDOWN(6, false),
		BACKDOWN(7, false),
		STARTDOWN(8, false),
		L3DOWN(9, false),
		R3DOWN(10, false),
		AUP(1, true),
		BUP(2, true),
		XUP(3, true),
		YUP(4, true),
		LBUP(5, true),
		RBUP(6, true),
		BACKUP(7, true),
		STARTUP(8, true),
		L3UP(9, true),
		R3UP(10, true),
		JOY1X(1, false),
		JOY1Y(2, false), 
		TRIGGERS(3, false),
		JOY2X(4, false),
		JOY2Y(5, false);
		
		public boolean isUp;
		
		public int controlNumber;
		
		Listenable(int number, boolean up)
		{
			controlNumber = number;
			isUp = up;
		}
}
