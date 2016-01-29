package org.usfirst.frc.team4564.robot;

public class Auto {

	DriveTrain dt;
	Bat bat;
	
	public static final int AUTO_INIT = 0;
	public static final int DETECT_APPROACH = 1;
	public static final int APPROACH_WAIT = 2;
	public static final int DETECT_EXIT = 3;
	public static final int EXIT_WAIT = 4;
	public static final int PREPPING_FOR_FIRST_TURN = 5;
	public static final int DETECT_FIRST_TURN = 6;
	public static final int FIRST_TURN_WAIT = 7;
	public static final int PREP_FOR_SECOND_TURN = 8;
	public static final int DETECT_SECOND_TURN = 9;
	public static final int SECOND_TURN_WAIT = 10;
	public static final int DETECT_RETURN = 11;
	public static final int RETURN_WAIT = 12;
	public static final int OPEN_DEFENSE = 13;
	public static final int FINI = 14;
	public int currentState = 0;
	
	public Auto (DriveTrain dt, Bat bat) {
		this.dt = dt;
		this.bat = bat;
	}
	public void updateAuto () {
		switch(currentState) {
		
		case AUTO_INIT:
		Common.debug("Starting Init");
		break;
		
		case DETECT_APPROACH:
		Common.debug("Searching For Wall");
		break;
		
		case APPROACH_WAIT:
		Common.debug("Verifying Approach");
		break;
		
		case DETECT_EXIT:
		Common.debug("Searching For Exit");
		break;
		
		case EXIT_WAIT:
		Common.debug("Verifying Exit");
		break;
		
		case PREPPING_FOR_FIRST_TURN:
		Common.debug("Preparing To Execute Second Turn");
		break;
		
		case DETECT_FIRST_TURN:
		Common.debug("Exectuting First Turn");
		break;
		
		case FIRST_TURN_WAIT:
		Common.debug("Verifying First Turn Completed");
		break;
		
		case PREP_FOR_SECOND_TURN:
		Common.debug("Preping To Exectue Second Turn");
		break;
		
		case DETECT_SECOND_TURN:
		Common.debug("Executing Second Turn");
		break;
		
		case SECOND_TURN_WAIT:
		Common.debug("Verifying Second Turn Completed");
		break;
		
		case DETECT_RETURN:
		Common.debug("Returning to Defense");
		break;
		
		case RETURN_WAIT:
		Common.debug("Verifying Return");
		break;
		
		case OPEN_DEFENSE:
		Common.debug("Opening Defense");
		break;
		}
		
	}
	
}
