package org.team3128.autonomous.commands;

import java.util.LinkedList;

import org.team3128.drive.TankDrive;
import org.team3128.util.ParticleReport;
import org.team3128.util.RoboVision;
import org.team3128.util.units.Length;

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
	TankDrive drive;
	
	RoboVision visionProcessor;
	
	private static final double DESIRED_CAN_DISTANCE = 2 * Length.yd;
	private static final long TIME_UNTIL_CAN_LOST = 2000;  //if the robot doesn't see a can for this long, it will stop and spin looking for it
	private final static double kPdistance = .05, kPangle = 1.0;
	
	long lastFixTime;
	
    public CmdVisionGoTowardsCan(TankDrive drive, RoboVision visionProcessor)
    {
    	this.drive = drive;
    	this.visionProcessor = visionProcessor;
    }

    protected void initialize()
    {
    	lastFixTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
		LinkedList<ParticleReport> targets = visionProcessor.findSingleTarget(
//	TrashCan		new Range(105, 137), 
        	//	new Range(10, 128),
        	//	new Range(0, 0),
//  TennisBall	new Range(35,70),
			//	new Range(10,175),
			//	new Range(128,255),
				// If you want to change values do it from here
				new Range(0,255),  //Hue
				new Range(34,190),    //Saturation
				new Range(32,255),  //Value
        //trashcan SmartDashboardCode	SmartDashboard.getNumber("aspectRatio",(21.9 * Units.in)/(28.8 * Units.in)),
        	//	SmartDashboard.getNumber("rectangularityScore", 100));
				SmartDashboard.getNumber("rectangularityScore", 70),
				SmartDashboard.getNumber("aspectRatio", 1));
		
		if(!targets.isEmpty())
		{
			
			ParticleReport targetReport = targets.get(0);
			
			double angleError = targetReport.getHeadingAngleOffset();
			
	/*		double distanceError = targetReport.computeDistanceHorizontal(2.6 * Units.in) - DESIRED_CAN_DISTANCE;
	        double forwardPow = RobotMath.makeValidPower(distanceError * kPdistance);
	        
	        double angleError = targetReport.getHeadingAngleOffset();
	        double horizontalPow = RobotMath.makeValidPower(-1 * angleError * kPangle);
	        
	        drive.arcadeDrive(horizontalPow/2, forwardPow/2, 0, false);
	        System.out.println("Horiz: " + horizontalPow + "; For: " + forwardPow + "Distance Error" + distanceError);*/
			
	//commenting out the movign code, the stuff below is it		
	//		drive.arcadeDrive(angleError/64,-.33,1,false);
			/*
	        
	        lastFixTime = System.currentTimeMillis();
			System.out.println("Found");
			
			*/
			System.out.println("Heading Angle= " + angleError);
		}
		
		else if(System.currentTimeMillis() - lastFixTime > TIME_UNTIL_CAN_LOST)
		{
		
	//		drive.arcadeDrive(-.005, 0, 1, false);
			System.out.println("Nothing found");
		}
		
		else {
	//		drive.arcadeDrive(-0.05,0,1,false);
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
