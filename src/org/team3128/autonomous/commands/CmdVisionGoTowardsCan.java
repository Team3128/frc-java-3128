package org.team3128.autonomous.commands;

import java.util.LinkedList;

import org.team3128.Log;
import org.team3128.util.Direction;
import org.team3128.util.ParticleReport;
import org.team3128.util.RoboVision;
import org.team3128.util.Units;

import com.ni.vision.NIVision.Range;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
 *       /^\ 
 *      / _ \
 *     / [ ] \
 *    /  [_]  \
 *   /    _    \
 *  /    (_)    \
 * /_____________\
 * -----------------------------------------------------
 * UNTESTED CODE!
 * This class has never been tried on an actual robot.
 * It may be non or partially functional.
 * Do not make any assumptions as to its behavior!
 * And don't blink.  Not even for a second.
 * -----------------------------------------------------*/

public class CmdVisionGoTowardsCan extends Command
{
	RoboVision visionProcessor;
	
    public CmdVisionGoTowardsCan(float degs, int msec, Direction dir)
    {
    }

    protected void initialize()
    {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
		LinkedList<ParticleReport> targets = visionProcessor.findSingleTarget(
				new Range(SmartDashboard.getInt("minH", 105), SmartDashboard.getInt("maxH", 137)), 
        		new Range(SmartDashboard.getInt("minS", 5), SmartDashboard.getInt("maxS", 128)),
        		new Range(SmartDashboard.getInt("minV", 0), SmartDashboard.getInt("maxV", 255)),
        		SmartDashboard.getNumber("aspectRatio",(21.9 * Units.in)/(28.8 * Units.in)),
        		SmartDashboard.getNumber("rectangularityScore", 100));
		
		if(!targets.isEmpty())
		{
			
			ParticleReport targetReport = targets.get(0);
			
	        Log.debug("RoboVision", "Target distance: " + targetReport.computeDistance() + " cm target heading angle");
		}
    }



    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {    	
    	return false;
    }

    // Called once after isFinished returns true
    protected void end()
    {
		
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	
    }
}
