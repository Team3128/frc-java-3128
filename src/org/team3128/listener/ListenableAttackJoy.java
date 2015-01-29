package org.team3128.listener;

/**
 * Enum which represents everything that can be read from the controller
 * @author Jamie
 */
public enum ListenableAttackJoy
{
		DOWN1(1, false, 12),
		DOWN2(2, false, 13),
		DOWN3(3, false, 14),
		DOWN4(4, false, 15),
		DOWN5(5, false, 16),
		DOWN6(6, false, 17),
		DOWN7(7, false, 18),
		DOWN8(8, false, 19),
		DOWN9(9, false, 20),
		DOWN10(10, false, 21),
		DOWN11(10, false, 22),
		UP1(1, true, 1),
		UP2(2, true, 2),
		UP3(3, true, 3),
		UP4(4, true, 4),
		UP5(5, true, 5),
		UP6(6, true, 6),
		UP7(7, true, 7),
		UP8(8, true, 8),
		UP9(9, true, 9),
		UP10(10, true, 10),
		UP11(10, true, 11),
		JOYX(0, false, 0),
		JOYY(1, false, 0),
		TWIST(2, false, 0),
		THROTTLE(3, false, 0),
		ALWAYS(0, false, 0);
		
		public boolean isUp;
		
		public int controlNumber;
		
		int oppositeButtonOrdinal;
		
		ListenableAttackJoy(int number, boolean up, int oppositeOrdinal)
		{
			controlNumber = number;
			isUp = up;
			
			oppositeButtonOrdinal = oppositeOrdinal;
		}
}
