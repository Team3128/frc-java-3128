package org.team3128.listener;

/**
 * Enum which represents everything that can be read from the controller
 * @author Jamie
 */
public enum Listenable
{
		ADOWN(1, false, 11),
		BDOWN(2, false, 12),
		XDOWN(3, false, 13),
		YDOWN(4, false, 14),
		LBDOWN(5, false, 15),
		RBDOWN(6, false, 16),
		BACKDOWN(7, false, 17),
		STARTDOWN(8, false, 18),
		L3DOWN(9, false, 19),
		R3DOWN(10, false, 20),
		AUP(1, true, 1),
		BUP(2, true, 2),
		XUP(3, true, 3),
		YUP(4, true, 4),
		LBUP(5, true, 5),
		RBUP(6, true, 6),
		BACKUP(7, true, 7),
		STARTUP(8, true, 8),
		L3UP(9, true, 9),
		R3UP(10, true, 10),
		JOY1X(1, false, 0),
		JOY1Y(2, false, 0), 
		TRIGGERS(3, false, 0),
		JOY2X(4, false, 0),
		JOY2Y(5, false, 0);
		
		public boolean isUp;
		
		public int controlNumber;
		
		int oppositeButtonOrdinal;
		
		Listenable(int number, boolean up, int oppositeOrdinal)
		{
			controlNumber = number;
			isUp = up;
			
			oppositeButtonOrdinal = oppositeOrdinal;
		}
}
