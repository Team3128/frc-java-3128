package org.team3128.listener.control;


/**
 * Control type for the POV stick/DPAD, which is read as an angle by WPILib.
 * 
 * It can be in the center position, or it can be in one of the four cardinal directions or one of the four subcardinal directions.
 * <pre>
 *      1
 *   8     2
 *  
 * 7    0    3
 *    
 *   6     4
 *      5
 *      </pre>
 * 
 * @author Narwhal
 *
 */
public class POV implements IControl
{
	int indexOnJoystick;
	
	int directionValue;
	
	public int getIndexOnJoystick() {
		return indexOnJoystick;
	}

	public int getDirectionValue() {
		return directionValue;
	}

	/**
	 * Construct a POV control from the angle it should match and its index on the joystick.
	 * 
	 * <pre>
	 *      1
	 *   8     2
	 *  
	 * 7    0    3
	 *    
	 *   6     4
	 *      5
     </pre>
	 * @param directionValue
	 */
	public POV(int indexOnJoystick, int directionValue) {
		super();
		
		if(directionValue < 0 || directionValue > 8)
		{
			throw new IllegalArgumentException("Direction value out of range");
		}
		
		this.indexOnJoystick = indexOnJoystick;
		this.directionValue = directionValue;
	}
	
	/**
	 * Creates a POV control from the value returned by Joystick.getPOV()
	 * @param angle
	 * @return
	 */
	public static POV fromWPILIbAngle(int indexOnJoystick, int angle)
	{
		if(angle < 0)
		{
			return new POV(indexOnJoystick, 0);
		}
		
		int value = 8 - (angle/ 45);
		

		
		return new POV(indexOnJoystick, value);
	}
	
	@Override
	public int hashCode()
	{
		return indexOnJoystick * 100000 + directionValue * 1000 + 19;
	}

	@Override
	public boolean equals(Object object)
	{
		if(object instanceof POV)
		{
			POV otherPOV = (POV)object;
			if(otherPOV.indexOnJoystick == indexOnJoystick)
			{
				if(otherPOV.directionValue == directionValue)
				{
					return true;
				}
			}
		}
		
		return false;
	}

}
