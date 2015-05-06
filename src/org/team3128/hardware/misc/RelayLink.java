package org.team3128.hardware.misc;

import edu.wpi.first.wpilibj.Relay;

public class RelayLink
{
	Relay _rel;

    /**
     * Instantiates a new relay.
     *
     * @param rel the relay to be linked
     */
    public RelayLink(Relay rel)
	{
    	_rel = rel;
	}

    /**
     * Sets the relay on.
     */
    public void setOn()
    {
    	_rel.set(Relay.Value.kForward);
    }

    /**
     * Sets the relay off.
     */
    public void setOff()
    {
    	_rel.set(Relay.Value.kReverse);
    }
}
