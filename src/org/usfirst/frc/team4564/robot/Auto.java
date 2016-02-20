package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.Timer;
public class Auto {

	DriveTrain dt;
	Bat bat;
	Thrower thrower;
	ArmWinch arm;
	
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
	public static final int DRIVING = 1;						//Move robot through or to defense, if moving through defense, wait to see exit.
	public static final int DEFENSE_CROSSED = 2;				//Robot cleared shield and now stopped.
	public static final int LOWER_ARM = 3;						//Wait for arm to reach a position
	public static final int DRIVE_STEP_ONE = 4;		
	public static final int DRIVE_STEP_TWO = 5;		
	public static final int PRE_DEFENSE_ALIGN = 6;		
	public static final int POST_DEFENSE_ALIGN = 7;	

	//autoRun states
	private static final int AUTO_SETUP = 1;					//Prep start of autoRun
	private static final int AUTO_DRIVE = 2;					//Drive through the defense, if one is selected
	private static final int AUTO_NEXT_ACTION = 3;				//Determine what is the next action; UTURN, SHOOT, or STOP
	private static final int AUTO_UTURN_STEP_1 = 4;				//Drive to the return defense, if one is specified
	private static final int AUTO_UTURN_STEP_2 = 5;	
	private static final int AUTO_UTURN_STEP_3 = 6;	
	private static final int AUTO_UTURN_STEP_4 = 7;	
	private static final int AUTO_SHOOT_STEP_1 = 8;				//Drive to tower and shoot
	private static final int AUTO_SHOOT_STEP_2 = 9;		
	private static final int AUTO_SHOOT_STEP_3 = 10;		
	private static final int AUTO_SHOOT_STEP_4 = 11;		
	private static final int AUTO_TOWER_ALIGN_STEP_1 = 12;		
	private static final int AUTO_TOWER_ALIGN_STEP_2 = 13;	
	private static final int AUTO_TOWER_ALIGN_STEP_3 = 14;
	private static final int AUTO_STOP = 15;						//autoRun complete; robot stopped.
	
	//autoRun parameters loaded from networktables
	private int paramStartingPlatform;
	private int paramDefenseType;	
	private int paramSelectedAction;
	private int paramTargetPlatform;
	
	//autoRun Action parameter constants
	private static final int ACTION_STOP = 0;
	private static final int ACTION_UTURN = 1;
	private static final int ACTION_SHOOT = 2;
	private static final int ACTION_TOWER_ALIGN = 3;
	
	//autoRunDefense parameter constants
	private static final int DEFENSE_LOWBAR = 1;
	private static final int DEFENSE_CHEVAL_DE_FRISE = 2;
	private static final int DEFENSE_MOAT = 3;
	private static final int DEFENSE_RAMPARTS = 4;
	private static final int DEFENSE_DRAWBRIDGE = 5;
	private static final int DEFENSE_SALLY_PORT = 6;
	private static final int DEFENSE_ROCK_WALL = 7;
	private static final int DEFENSE_ROUGH_TERRAIN = 8;
	private static final int DEFENSE_PORTCULLIS = 9;

	//Field Dimension constants.
	public static final double PLATFORM_WIDTH = 52.5;
	public static final double ABSOLUTE_CASTLE_X = 170.6113;  // X position of the center of the castle
	public static final double ROBOT_WIDTH = 35.0;
	
	//State variables for Shield, Drive, and AutoRun
	public int shieldState = AUTO_INIT;
	private int driveState = NOT_DRIVING;
	private int autoRunState = AUTO_SETUP;

	//Misc variables
	public long detectTime;		     	    //Start time of Shield detect.
	public double shieldDistance;    	    //The ultrasonic sensor distance upon exit of a defense.
	public double xAbs;              	    //Absolute x position of the robot after clearing a defense. 
	public double xTargetCenter;      		//Inches to center of target platform relative to left wall.
	public double xDistanceToTarget;   		//Inches to target platform relative from center of robot.
	public double xDistanceToCastleCenter;  //Inches to castle center relative to robot center
	
	//Auto constructor
	public Auto(DriveTrain dt, Bat bat, ArmWinch arm, Thrower thrower) {
		this.arm = arm;
		this.dt = dt;
		this.bat = bat;
		this.thrower = thrower;
	}
	
	// Prepare for auto run
	public void init() {
		shieldState = AUTO_INIT;     //set initial shield state
		driveState = NOT_DRIVING;    //set initial drive state
		autoRunState = AUTO_SETUP;   //set initial autoRun state
		// Setup drivetrain
		dt.init();
    		dt.setSafetyEnabled(false);
        	dt.setHeadingHold(true);
		// Get initial auto parameters from network tables.  They'll default to zero if not specified.
		paramStartingPlatform = (int) Robot.table.getNumber("platform", 0);
		paramDefenseType = (int) Robot.table.getNumber("defense", 0);
		paramTargetPlatform = (int) Robot.table.getNumber("targetPlatform", 0);
		paramSelectedAction = (int) Robot.table.getNumber("action", 0);
	}
	
	// Using the ultrasonic sensor, this logic determines when the robot passes through a shield.
	// The value of "shieldDistance" will give the distance that the robot was away from the shield just before exit.
	// Returns true when defense is cleared. 
	// This method is used by driveDefense().
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
				
	// Using the ultrasonic sensor, this logic determines when the robot is within a defense.
	// Use this for the u-turn action
	// Retuuns true when within a shield.
	//***We may not need this function***
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
	// This methods is used to autoRun().
	public boolean driveDefense(int defenseType) {
		
		boolean defenseCleared = false;
	
		switch(defenseType) {
		//ROUGH TERRAIN
			case DEFENSE_ROUGH_TERRAIN:
				switch(driveState) {
				case NOT_DRIVING:
					arm.setArmPosition(3);
					dt.setDriveSpeed(0.6);
					driveState = DRIVING;
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
			//LOWBAR
			case DEFENSE_LOWBAR: 
				switch(driveState) {

					case NOT_DRIVING:
						arm.setArmPosition(0);
						dt.setDriveSpeed(0.3);
						driveState = DRIVING;
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
					arm.setArmPosition(0);
					driveState = LOWER_ARM;
					break;
				
				case LOWER_ARM:
					if (arm.moveCompleted()) {
						dt.setDriveSpeed(.45);
						thrower.setExternalIntake(-1.0);
						driveState = DRIVING;
					}
				case DRIVING:
					if (shieldCrossed()) {
						arm.setArmPosition(2);
						dt.setDriveSpeed(0.0);
						thrower.setExternalIntake(0.0);
						driveState = DEFENSE_CROSSED;
					}
					break;

				case DEFENSE_CROSSED:
					defenseCleared = true;
					break;
					
				}
			//CHEVAL DE FRISE
			case DEFENSE_CHEVAL_DE_FRISE:
				switch(driveState) {
				case NOT_DRIVING:
					arm.setArmPosition(3);
					dt.driveDistance(24);
					driveState = DRIVING;
					break;
				
				case DRIVING:
					if (dt.driveComplete()) {
						dt.setDriveSpeed(0.0);
						driveState = LOWER_ARM;
					}
					break;
				case LOWER_ARM:
					arm.setArmPosition(0);
					driveState = DRIVE_STEP_ONE;
				case DRIVE_STEP_ONE:
					if (arm.moveCompleted()) {
						dt.setDriveSpeed(.3);
						driveState = DRIVE_STEP_TWO;
					}
				case DRIVE_STEP_TWO:
					if (shieldCrossed()) {
						dt.setDriveSpeed(0.0);
						driveState = DEFENSE_CROSSED;
					}
					
				case DEFENSE_CROSSED:
					defenseCleared = true;
					break;
			}
			//MOAT
			case DEFENSE_MOAT:
				switch(driveState) {
				case NOT_DRIVING:
					arm.setArmPosition(3);
					dt.setDriveSpeed(0.75);
					driveState = DRIVING;
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
			//ROCK WALL
			case DEFENSE_ROCK_WALL:
				switch(driveState) {
				case NOT_DRIVING:
					arm.setArmPosition(3);
					dt.setDriveSpeed(0.75);
					driveState = DRIVING;
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
			//RAMPARTS
			case DEFENSE_RAMPARTS:
				switch(driveState) {
				case PRE_DEFENSE_ALIGN:
					dt.rotateTo(330);
					driveState = NOT_DRIVING;
					break;
				case NOT_DRIVING:
					if (dt.driveComplete()) {
						arm.setArmPosition(3);
						dt.setDriveSpeed(.5);
						driveState = DRIVING;
					}
					break;
				case DRIVING:
					if (shieldCrossed()) {
						dt.setDriveSpeed(0.0);
						dt.rotateTo(0);
						driveState = POST_DEFENSE_ALIGN;
					}
					break;
				case POST_DEFENSE_ALIGN:
					if (dt.driveComplete()) {
						driveState = DEFENSE_CROSSED;
					}
				case DEFENSE_CROSSED:
					defenseCleared = true;
					break;
			}
		}
		if (defenseCleared) {
			xAbs = paramStartingPlatform * PLATFORM_WIDTH - shieldDistance - ROBOT_WIDTH / 2;
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
				break;
			case AUTO_NEXT_ACTION:
				switch (paramSelectedAction) {
				case ACTION_STOP:
					autoRunState = AUTO_STOP;
					break;
				case ACTION_UTURN:
					dt.rotateTo(turnLogic(paramTargetPlatform, paramStartingPlatform));  // Initiate first turn
					autoRunState = AUTO_UTURN_STEP_1;
					break;
				case ACTION_SHOOT:
					dt.rotateTo(turnLogic(ABSOLUTE_CASTLE_X, paramStartingPlatform));
					autoRunState = AUTO_SHOOT_STEP_1;
					break;
				case ACTION_TOWER_ALIGN:
					dt.rotateTo(turnLogic(paramTargetPlatform, paramStartingPlatform));  // Initiate first turn
					autoRunState = AUTO_TOWER_ALIGN_STEP_1;
					break;
				}
				break;
			//AUTO UTURN
			case AUTO_UTURN_STEP_1:
				if (dt.driveComplete()) {
					Common.dashNum("Robot Absolute X", xAbs);
					//Calculate absolute x center of target platform.
					xTargetCenter = paramTargetPlatform * PLATFORM_WIDTH - (PLATFORM_WIDTH * .5);
					xDistanceToTarget = xAbs - xTargetCenter;
					dt.driveDistance(xDistanceToTarget);
					autoRunState = AUTO_UTURN_STEP_2;
				}				
				break;
			case AUTO_UTURN_STEP_2:
				if (dt.driveComplete()) {
					dt.rotateTo(180);
					autoRunState = AUTO_UTURN_STEP_3;
				}
				break;
			case AUTO_UTURN_STEP_3:
				if (dt.driveComplete()) { 
					dt.setDriveSpeed(.45);
					shieldState = DETECT_RETURN;
					autoRunState = AUTO_UTURN_STEP_4;
				}
				break;
			case AUTO_UTURN_STEP_4:
				if (shieldReturn()) {
					autoRunState = AUTO_STOP;
				}
				break;
			//AUTO SHOOT
			case AUTO_SHOOT_STEP_1:
				if (dt.driveComplete()) {
					xDistanceToCastleCenter = ABSOLUTE_CASTLE_X - xAbs;
					dt.driveDistance(xDistanceToCastleCenter);
					autoRunState = AUTO_SHOOT_STEP_2;
				}
			case AUTO_SHOOT_STEP_2:
				if (dt.driveComplete()) {
					dt.rotateTo(turnLogic(ABSOLUTE_CASTLE_X, paramStartingPlatform)* -1);
					autoRunState = AUTO_SHOOT_STEP_2;
				}
				break;
			case AUTO_SHOOT_STEP_3:
				if (dt.driveComplete()) {
					thrower.state.prepThrow(); 
					autoRunState = AUTO_SHOOT_STEP_4;
				}
				break;
			case AUTO_SHOOT_STEP_4:
				thrower.state.throwBall();
				autoRunState = AUTO_STOP;
				break;
			//AUTO CASTLE ALIGN
			case AUTO_TOWER_ALIGN_STEP_1:
				if (dt.driveComplete()) {
					xDistanceToCastleCenter = ABSOLUTE_CASTLE_X - xAbs;
					dt.driveDistance(xDistanceToCastleCenter);
					autoRunState = AUTO_TOWER_ALIGN_STEP_2;
				}
				break;
			case AUTO_TOWER_ALIGN_STEP_2:
				if (dt.driveComplete()) {
					dt.rotateTo(turnLogic(ABSOLUTE_CASTLE_X, paramStartingPlatform)* -1);
				}
				break;
			case AUTO_TOWER_ALIGN_STEP_3:
				if (dt.driveComplete()) {
					autoRunState = AUTO_STOP;
				}
				break;
			case AUTO_STOP:
				dt.setDriveSpeed(0.0);
				dt.setHeadingHold(false);
				break;
		}
		arm.update();
		dt.autoDrive();
	}
	
	//Determines right or left turn based on starting platform and target platform.
	public double turnLogic(double target, int startingPlatform) {
		if (target > startingPlatform) {
			return 90;
		} else {
			return -90;
		}
	}
	
	/*
	//Find absolute robot position on field after crossing initial defense relative to left field wall.
	public double xAbs(int startingPlatform, double PLATFORM_WIDTH, double shieldDistance) {
		return (paramStartingPlatform * PLATFORM_WIDTH - shieldDistance - ROBOT_WIDTH / 2);
	}
	
	//Calculate absolute x center of target platform.
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
	*/
	
}
