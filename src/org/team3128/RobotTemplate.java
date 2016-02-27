package org.team3128;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.team3128.listener.ListenerManager;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
 * THIS FILE SHOULD NOT BE MODIFIED
 * --------------------------------
 * It serves as a link to the main class.
 * Events triggered here will be forwarded there.
 *
 * Do not call these functions under any circumstances. Do not modify this
 * class under any circumstances.
 *
 * AUTOGENERATED. DO NOT EDIT UNDER PENALTY OF 42.
 *
 * THIS FILE IS YOUR SOUL.
 */

public class RobotTemplate extends IterativeRobot 
{
	MainClass main;
	ArrayList<ListenerManager> listenerManagers = new ArrayList<ListenerManager>();
	SendableChooser autoChooser;
	
	int dashboardUpdateWavelength = 50;
	
	Thread dashboardUpdateThread;
	
	boolean wasInAutonomous = false;
	
	@Override
    public void robotInit()
    {
        Log.info("RobotTemplate", "Welcome to the FRC Team 3128 No-Longer-Event System version 3.1!");
        Log.info("RobotTemplate", "Initializing Robot...");
        
        if(!constructMainClass())
        {
        	Log.fatal("RobotTemplate", "Could not construct main class!");
        	throw new RuntimeException("Could not construct main class!");
        }
        
        main.initializeRobot(this);
        
        Log.info("RobotTemplate", "Setting Up Autonomous Chooser...");
		autoChooser = new SendableChooser();
        main.addAutoPrograms(autoChooser);
        SmartDashboard.putData("autoChooser", autoChooser);
        
        Log.info("RobotTemplate", "Starting Dashboard Update Thread...");
        dashboardUpdateThread = new Thread(this::updateDashboardLoop, "Dashboard Update Thread");
        dashboardUpdateThread.start();
        
        Log.info("RobotTemplate", "Initialization Done!");
    }
	
    // ARE YOU CHANGING THINGS?

    @Override
    public void disabledInit()
    {
    	//re-construct all of the autonomous programs so they can be run again
    	if(wasInAutonomous)
    	{
    		main.addAutoPrograms(autoChooser);
    	}
    	
    	main.initializeDisabled();
    }
    
    /**
     * This function is run in its own thread to call main.updateDashboard()
     */
    private void updateDashboardLoop()
    {
		Log.info("RobotTemplate", "Dashboard Update Thread starting");
    	while(true)
    	{
    		main.updateDashboard();
    		
    		try
			{
				Thread.sleep(dashboardUpdateWavelength);
			} 
    		catch (InterruptedException e)
			{
    			Log.info("RobotTemplate", "Dashboard Update Thread shutting down");
				return;
			}
    	}
    	
    }
    
    // TURN BACK NOW.
    // YOUR CHANGES ARE NOT WANTED HERE.
    
    /**
     * Add a listener manager to the list of ones to be ticked in teleopPeriodic().
     * @param manager
     */
    public void addListenerManager(ListenerManager manager)
    {
    	listenerManagers.add(manager);
    }
    
    /**
     * Set the wavelength (time between updates) of the dashboard update thread.
     * @param millis
     */
    public void setDashboardUpdateWavelength(int millis)
    {
    	dashboardUpdateWavelength = millis;
    }
    
    // YOU'D BETTER NOT CHANGE ANYTHING
    /**
     * Remove all listeners from every ListenerManager.
     */
    private void resetListeners()
    {
    	for(ListenerManager manager : listenerManagers)
    	{
    		manager.removeAllListeners();
    	}
    }

    @Override
    public void autonomousInit()
    {
        Log.info("RobotTemplate", "Initializing Autonomous...");
        resetListeners();
        main.initializeAuto();
		CommandGroup autoCommand = (CommandGroup) autoChooser.getSelected();
		Log.info("RobotTemplate", "Starting auto program " + autoCommand.getName());
		autoCommand.start();
		wasInAutonomous = true;
        Log.info("RobotTemplate", "Auto Initialization Done!");
    }
   
    @Override
    public void teleopInit()
    {
        Log.info("RobotTemplate", "Initializing Teleop...");
    	resetListeners();
    	main.initializeTeleop();
        Log.info("RobotTemplate", "Teleop Initialization Done!");
    }
    
    @Override
    public void disabledPeriodic()
    {
		Thread.yield();
    }

    @Override
    public void autonomousPeriodic()
    {   
    	Scheduler.getInstance().run();
    }

    // DO YOU REALLY WANT TO MODIFY YOUR SOUL?
    @Override
    public void teleopPeriodic()
    {        
    	for(ListenerManager manager : listenerManagers)
    	{
    		manager.tick();
    	}
    	        
        try
		{
			Thread.sleep(20);
		}
        catch (InterruptedException e)
		{
			return;
		}
    }
    
    
    /**
     * Try to do fancy reflectiony things to construct the main class.  Returns false if they fail.
     * @return
     */
    boolean constructMainClass()
    {
        String mainClassName = null;

        try
        {
        	File mainClassFile = Paths.get(System.getProperty("user.home"), "AlumNarMainClass.txt").toFile();
        	
        	if(!mainClassFile.exists())
        	{
        		Log.fatal("RobotTemplate", "No main class indicator file present!  A blank one has been created.");
        		mainClassFile.createNewFile();
        		return false;
        	}
        	
        	//sheesh, all of this to read one line of text
        	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(mainClassFile)));
        	mainClassName = reader.readLine();
        	reader.close();
        }
        catch(IOException ex)
        {
        	Log.fatal("RobotTemplate", "Unable to read main class file!");
        	ex.printStackTrace();
        	return false;
        }
        
        try
		{
			Class<? extends MainClass> mainClassClass = Class.forName("org.team3128.main." + mainClassName).asSubclass(MainClass.class);
			main = mainClassClass.newInstance();
		}
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
		{
			Log.fatal("RobotTemplate", "Error instantiating main class: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
        catch(ClassCastException ex)
        {
        	Log.fatal("RobotTemplate", "Main class provided was not a subclass of MainClass!");
        	ex.printStackTrace();
        	return false;
        }
        
        return true;
        
    }
}

