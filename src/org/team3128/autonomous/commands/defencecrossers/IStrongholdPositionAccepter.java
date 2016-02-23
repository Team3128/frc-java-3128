package org.team3128.autonomous.commands.defencecrossers;


/**
 * Interface for second-stage Stronghold auto programs which take their position on the field as an argument
 * @author Jamie
 *
 */
public interface IStrongholdPositionAccepter
{
	public void setFieldPosition(StrongholdStartingPosition position);
}
