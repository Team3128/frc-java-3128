package org.team3128.listener;

//Enum which represents everything that can be read from the controller
public enum Listenable
{
	//This class combines the functions of XControl and ListenerManager.
	//It is constructed with one Joystick object, which confusingly seems
	//to be the wpilib metaphor for an entire controller.
	//It polls the controller at a set interval, and invokes listeners
	//whenever a value they're set for has changed.  Listeners are run on
	//the object's polling thread, and will need to be passed a reference
	//to the listener manager somehow if the need control data
	//You may register the same (shared ptr to a) listener as many
	//times as you like, and each handler will only be invoked
	//once per polling cycle.
		ADOWN, //0
		BDOWN,
		XDOWN,
		YDOWN,
		LBDOWN,
		RBDOWN,
		BACKDOWN,
		STARTDOWN,
		L3DOWN,
		R3DOWN,
		JOY1X,//10
		JOY1Y, 
		TRIGGERS,
		JOY2X,
		JOY2Y,
		AUP, //15
		BUP,
		XUP,
		YUP,
		LBUP,
		RBUP,
		BACKUP,
		STARTUP,
		L3UP,
		R3UP
}
