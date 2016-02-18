package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.Timer;
public class Auto {

	DriveTrain dt;
	Bat bat;
	Thrower t;
	
	//Shield detection states for driving through a defense
	public static final int AUTO_INIT = 0;						// Auto mode has to be initialized
	public static final int DETECT_APPROACH = 1;				// Awaiting for inital Shield detect
	public static final int APPROACH_WAIT = 2;					// Verify shield detect for a set amount of time
	public static final int DETECT_EXIT = 3;					// Within the defense, awaiting exit from Shield
	public static final int EXIT_WAIT = 4;						// Verify shield exit for a set amount of time
	public static final int DEFENSE_CLEARED = 5;				// Verified that defense has been cleared
	
	//Shield detection states for returning through Portcullis or Sally Port
	public static final int DETECT_RETURN = 10;					// Awaiting for initial Shield detect
	public static final int RETURN_WAIT = 11;					// Verify shield detect for a set amount of time
	public static final int IN_DEFENSE = 12;					// Robots within the defense
	
	//Shield related constants
	public static final double MAX_SHIELD_DISTANCE = 20;
	public static final double VERIFICATION_TIME = 300;		//Amount of necessary time to verify shield detect

	//driveDefense states.
	public static final int NOT_DRIVING = 0;					//Ready to start
	public static final int DRIVING = 1;						//Robot moving, waiting to clear Shield
	public static final int DEFENSE_CROSSED = 2;				//Robot cleared shield and now stopped.

	//autoRun states
	private static final int AUTO_SETUP = 1;					//Prep start of autoRun
	private static final int AUTO_DRIVE = 2;					//Drive through the defense, if one is selected
	private static final int AUTO_NEXT_ACTION = 3;				//Determine what is the next action; UTURN, SHOOT, or STOP
	private static final int AUTO_UTURN = 4;					//Drive to the return defense, if one is specified
	private static final int AUTO_SHOOT = 5;					//Drive to tower and shoot
	private static final int AUTO_STOP = 6;						//autoRun complete; robot stopped.
	
	//autoRun parameters loaded from networktables
	private int paramStartingPlatform;
	private int paramDefenseType;	
	private int paramSelectedAction;
	private int paramTargetPlatform;
	private int paramTarget;
	//Action parameter constants
	private static final int ACTION_STOP = 0;
	private static final int ACTION_UTURN = 1;
	private static final int ACTION_SHOOT = 2;
	//Defense parameter constants
	private static final int DEFENSE_LOWBAR = 1;
	private static final int DEFENSE_ROCK_WALL = 2;
	private static final int DEFENSE_MOAT = 3;
	private static final int DEFENSE_PORTCULLIS = 3;

	//State variables
	public int shieldState = AUTO_INIT;
	private int driveState = NOT_DRIVING;
	private int autoRunState = AUTO_SETUP;

	//Field Dimension constants.
	public static final double PLATFORM_WIDTH = 52.5;
	public static final double ABSOLUTE_CASTLE_X = 170.6113;

	//Misc variables
	public long detectTime;		//Start time of Sheild detect
	public double shieldDistance;  //The ultrasonic sensor distance upon exit of a defense
	
	
	//Auto constructor
	public Auto(DriveTrain dt, Bat bat) {
		this.dt = dt;
		this.bat = bat;
		dt.setHeadingHold(true);
	}
	
	// Prepare for auto run
	public void init() {
		shieldState = AUTO_INIT;  //set initial shield state
		driveState = NOT_DRIVING; //set initial drive state
		// Setup drivetrain
    	dt.init();
        dt.setSafetyEnabled(false);
    	dt.heading.setHeadingHold(true);
    	// Get initial auto parameters from network tables.  They'll default to zero if not specified.
    	paramStartingPlatform = (int) Robot.table.getNumber("platform", 0);
    	paramDefenseType = (int) Robot.table.getNumber("defense", 0);
    	paramTargetPlatform = (int) Robot.table.getNumber("targetPlatform", 0);
    	paramSelectedAction = (int) Robot.table.getNumber("action", 0);
	}
	
	// Using the ultrasonic sensor, this logic determines when the robot passes through a shield.
	// The value of "shieldDistance" will give the distance that the robot was away from the shield just before exit.
	// Returns true when defense is cleared. 
	public boolean shieldCrossed() {
		boolean isCleared = false;
		switch (shieldState) {

			case AUTO_INIT:
				shieldState = DETECT_APPROACH;
				break;
			
			case DETECT_APPROACH:
				if (bat.getDistance() <= MAX_SHIELD_DISTANCE) { 
					detectTime = Common.time();
					shieldState = APPROACH_WAIT;
				}
				break;
				
			case APPROACH_WAIT:
				if (bat.getDistance() <= MAX_SHIELD_DISTANCE) {
					if(Common.time() - detectTime >= VERIFICATION_TIME) {
						shieldState = DETECT_EXIT;
					}		
				} else {
					shieldState = DETECT_APPROACH;
				}
				break;
				
			case DETECT_EXIT:
				if ( bat.getDistance() > MAX_SHIELD_DISTANCE) {
					detectTime = Common.time();
					shieldState = EXIT_WAIT;
				} else {
					shieldDistance = bat.getDistance();   //remember our distance from the shield
				}
				break;
				
			case EXIT_WAIT:
				if (bat.getDistance() > MAX_SHIELD_DISTANCE) {
					if (Common.time() - detectTime >= VERIFICATION_TIME) {
						shieldState = DEFENSE_CLEARED;
					} 
				} else {
					shieldState = DETECT_EXIT;
				}
			    break;
			    
			case DEFENSE_CLEARED:
				isCleared = true;
				break;
		}
		return isCleared;
	}
				
	// Using the ultrasone sensor, this logic determines when the robot is within a defense.
	// Use this for the u-turn action
	// Retruns true when within a shield.
	public boolean shieldReturn() {
		boolean inDefense = false;
		switch (shieldState) {

			case DETECT_RETURN:
				if (bat.getDistance() <= MAX_SHIELD_DISTANCE) { 
					detectTime = Common.time();
					shieldState = RETURN_WAIT ;
				}
				break;
				
			case RETURN_WAIT:
				if (bat.getDistance() <= MAX_SHIELD_DISTANCE) {
					if (Common.time() - detectTime >= VERIFICATION_TIME) {
						shieldState = IN_DEFENSE;
					}
				} else {
					shieldState = DETECT_RETURN;
				}
			    break;
			    
			case IN_DEFENSE:
				inDefense = true;
				break;
		}
		return inDefense;
	}

	// Given a defenseType, this method will drive the robot through a defense.
    // It will return true when the defense is cleared.
	public boolean driveDefense(int defenseType) {
		
		boolean defenseCleared = false;
	
		switch(defenseType) {
			//LOW-BAR
			case DEFENSE_LOWBAR: 
				Common.dashNum("Defense Drive State",driveState);
				switch(driveState) {

					case NOT_DRIVING:
						driveState = DRIVING;
						dt.setDriveSpeed(0.6);
						break;
					
					case DRIVING:
						if (shieldCrossed()) {
							dt.setDriveSpeed(0.0);
							driveState = DEFENSE_CROSSED;
						}
						break;
					
					case DEFENSE_CROSSED:
						defenseCleared = true;
						break;
				}
				
			//PORTCULLIS
			case DEFENSE_PORTCULLIS:
				switch(driveState) {
				
				case NOT_DRIVING:
					break;
				
				case DRIVING:
					break;

				case DEFENSE_CROSSED:
					defenseCleared = true;
					break;
					
				}
		}
		return defenseCleared;
	}
				
					
	//Execute an full auto routine based on the user provided parameters.
	//Call this method from a robot loop, multiple times per second.
	public void autoRun() {
		// TESTING ONLY *****************
    	paramStartingPlatform = 5;
    	paramDefenseType = DEFENSE_LOWBAR;
    	paramTargetPlatform = 4;
    	paramSelectedAction = ACTION_UTURN;
    	Common.dashNum("autoRun State", autoRunState);
        //***********************
		switch (autoRunState) {
			case AUTO_SETUP:
				if (paramStartingPlatform > 0 && paramDefenseType > 0) {
					autoRunState = AUTO_DRIVE;
				}
				break;
			case AUTO_DRIVE:
				if (driveDefense(paramDefenseType)) {
					autoRunState = AUTO_NEXT_ACTION;
				}	
				dt.autoDrive();
				break;
			case AUTO_NEXT_ACTION:
				switch (paramSelectedAction) {
				case ACTION_STOP:
					autoRunState = AUTO_STOP;
					break;
				case ACTION_UTURN:
					dt.rotateTo(turnLogic(paramTargetPlatform, paramStartingPlatform));  // Initiate first turn
					autoRunState = AUTO_UTURN;
					break;
				case ACTION_SHOOT:
					autoRunState = AUTO_SHOOT;
					break;
				}
				break;
			case AUTO_UTURN:
				dt.autoDrive();
				if (dt.driveComplete()) {
					driveState = AUTO_STOP;
				}				
				break;
			case AUTO_SHOOT:
				
				break;
			case AUTO_STOP:
				dt.baseDrive(0.0, 0.0);
				break;
		}
	}
				
					
/*					
					if (driveState < DEFENSE_CROSSED) {
						if (driveState == NOT_DRIVING) {
							dt.setDrive(.5,0);
							driveState = DRIVING;
						} 
						if (driveState == DRIVING) {
							dt.setDrive(.5,0);
							if (defenseComplete()) {
								driveState = DEFENSE_CROSSED;
							}
						}
					}
					if (selectedAction == 0) {
						dt.setDrive(0,0);
						driveState = FINI;
					}
					if (selectedAction == 1) {			
						if (driveState == DEFENSE_CROSSED) {
							driveState = TURNING;
							if (dt.rotateTo(turnLogic(targetPlatform, startingPlatform))) {
								driveState = TURN_COMPLETE;
							}
						}
						if (driveState == TURN_COMPLETE) {
							driveState = MOVING_TO_TARGET;
							if (dt.driveDistance(xDrive(xAbs(startingPlatform, PLATFORM_WIDTH, shieldDistance), xPlatformTarget(targetPlatform, PLATFORM_WIDTH, shieldDistance)))) {
								driveState = REACHED_TARGET;
							}
						}	
						if (driveState == REACHED_TARGET) {
							driveState = TURNING;
							if (dt.rotateTo(turnLogic(targetPlatform, startingPlatform))) {
								driveState = OPENING_DEFENSE;
							}	
						}
						if (driveState == OPENING_DEFENSE) {
							if (returnDefenseComplete()) {
								driveState = FINI;
							}	
						}
					if (selectedAction == 2) {
						if (driveState == DEFENSE_CROSSED) {
							driveState = TURNING;
							if (dt.rotateTo(turnLogic(ABSOLUTE_CASTLE_X, startingPlatform))) {
								driveState = TURN_COMPLETE;
							}
						}
						if (driveState == TURN_COMPLETE) {
							driveState = MOVING_TO_TARGET;
							if (dt.driveDistance(xDrive(xAbs(startingPlatform, PLATFORM_WIDTH, shieldDistance), xCastleTarget(ABSOLUTE_CASTLE_X, xAbs(startingPlatform, PLATFORM_WIDTH, shieldDistance))))) {
								driveState = REACHED_TARGET;
							}
						}
						if (driveState == REACHED_TARGET) {
							driveState = TURNING;
							if (dt.rotateTo(turnLogic(target, startingPlatform)* -1)) {
								driveState = ALIGNMENT_COMPLETE;
						}
						if (driveState == ALIGNMENT_COMPLETE) {
							driveState = FIRING;
							t.setFlywheel(AUTO_HIGHGOAL_SPEED);
						}
					}
				}
		break;
			
		}
	}
*/
		
	//Determines right or left turn based on starting platform and target platform.
	public double turnLogic(double target, int startingPlatform) {
		if ( target > startingPlatform) {
			return 90;
		} else {
			return -90;
		}
	}
	
	//Find absolute robot position on field after crossing intial defense.
	public double xAbs(int startingPlatform, double PLATFORM_WIDTH, double shieldDistance) {
		return (startingPlatform * PLATFORM_WIDTH - shieldDistance);
	}
	
	//Drive to selected platform based on starting position.
	public double xPlatformTarget(int targetPlatform, double PLATFORM_WIDTH, double shieldDistance) {
		return (targetPlatform * PLATFORM_WIDTH - (PLATFORM_WIDTH * .5));
	}
	
	//Drive to castle based on starting position.
	public double xCastleTarget(double ABSOLUTE_CASTLE_X, double xABS){
		return (Math.abs(xABS - ABSOLUTE_CASTLE_X));
	}
	
	//Drive to target on field based on current position.
	public double xDrive (double xAbs, double xTarget) {
		return (xAbs - xTarget);
	}
	
}