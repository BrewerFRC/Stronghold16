package org.usfirst.frc.team4564.robot;

@SuppressWarnings("unused")
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
	private static final int AUTO_SHOOT_STEP_3_CENTER = 10;	
	private static final int AUTO_SHOOT_STEP_3_RIGHT = 11;
	private static final int AUTO_SHOOT_STEP_3_LEFT = 12;
	private static final int AUTO_SHOOT_STEP_4 = 13;	
	private static final int AUTO_SHOOT_STEP_4_RIGHT = 14;
	private static final int AUTO_SHOOT_STEP_4_LEFT = 15;
	private static final int AUTO_SHOOT_STEP_4_RIGHT_ROTATE = 16;
	private static final int AUTO_SHOOT_STEP_4_LEFT_ROTATE = 17;
	private static final int AUTO_SHOOT_STEP_5 = 18;
	private static final int AUTO_SHOOT_STEP_5_LEFT = 19;
	private static final int AUTO_STOP_ACTION = 20;               // Stop after defense crossing; rotate to 180; lower arm to 0
	private static final int AUTO_COMPLETE = 21;						//autoRun complete; robot stopped.
	private static final int AUTO_GOAL_LINE_APPROACH = 22;
	private static final int AUTO_ROTATE_TO_GOAL = 23;
	private static final int AUTO_AIM_TOWARD_GOAL = 24;
	private static final int AUTO_DRIVE_BY_SHOOTING = 25;
	
	//autoRun parameters loaded from networktables
	public int paramStartingPlatform;
	public int paramDefenseType;	
	public int paramSelectedAction;
	public int paramTargetPlatform;
	
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
	public static final double PLATFORM_WIDTH = 53.0;
	public static final double ABSOLUTE_CASTLE_X = 170.6113;  // X position of the center of the castle
	public static final double ABSOLUTE_OUTERWORKS_Y = 191.0;  // Y position of the outerworks on coutyard side
	public static final double RIGHT_GOAL_LINE_Y = ABSOLUTE_OUTERWORKS_Y - 151.0; // From the center of 5th platform
	public static final double LEFT_GOAL_LINE_Y = ABSOLUTE_OUTERWORKS_Y - 105.0; // From the center of 1st platform
	public static final double BATTER_DEPTH = 68.0;
	public static final double GOAL_DEPTH = 36.6;
	public static final double DISTANCE_TO_BATTER = ABSOLUTE_OUTERWORKS_Y - BATTER_DEPTH; // Distance of outerworks from batter
	public static final double ROBOT_WIDTH = 35.0;
	public static final double ROBOT_LENGTH = 36.5;
	
	//State variables for Shield, Drive, and AutoRun
	public int shieldState = AUTO_INIT;
	private int driveState = NOT_DRIVING;
	private int autoRunState = AUTO_SETUP;

	//Misc variables
	public long driveTime; 					//drive time
	public long detectTime;		     	    //Start time of Shield detect.
	public double shieldDistance;    	    //The ultrasonic sensor distance upon exit of a defense.
	public long lowerTime;
	public double xAbs;              	    //Absolute x position of the robot after clearing a defense. 
	public double yAbs;						//Inches from defensive line to center of robot, courtyard side
	public double xTargetCenter;      		//Inches to center of target platform relative to left wall.
	public double xDistanceToTarget;   		//Inches to target platform relative from center of robot.
	public double xDistanceToCastleCenter;  //Inches to castle center relative to robot center
	public double distance;                 //Temporary variable for calculating drive distances;
	public double heading;					//Temporary variable for calculating heading
	
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
		thrower.state.init();
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
						dt.setDriveSpeed(0.70);
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
						yAbs = 36 + ROBOT_LENGTH/2; 
						break;
				}
				break;
			
			//LOWBAR
			case DEFENSE_LOWBAR: 
				switch(driveState) {

					case NOT_DRIVING:
						arm.setArmPosition(0);
						driveState = LOWER_ARM;
						break;
					
					case LOWER_ARM:
						if (arm.moveCompleted()) {
							dt.setDriveSpeed(0.65);
							driveState = DRIVING;
						}
					case DRIVING:
						if (shieldCrossed()) {
							dt.setDriveSpeed(0.0);
							driveState = DEFENSE_CROSSED;
						}
						break;
					
					case DEFENSE_CROSSED:
						defenseCleared = true;
						yAbs = 25 + ROBOT_LENGTH/2; 
						break;
				}
				break;
				
			//PORTCULLIS
			case DEFENSE_PORTCULLIS:
				switch(driveState) {
				
					case NOT_DRIVING:
						arm.setArmPosition(0);
						Common.debug("driveDefense: Starting PORTCULLIS");
						driveState = LOWER_ARM;
						break;
					
					case LOWER_ARM:
						if (arm.moveCompleted()) {
							Common.debug("driveDefense: Opening Portcullis");
							dt.setDriveSpeed(0.60);
							thrower.state.startPortcullis();
							driveState = DRIVING;
						}
						break;
					case DRIVING:
						if (shieldCrossed()) {
							Common.debug("driveDefense: Defense Crossed");
							arm.setArmPosition(2);
							dt.setDriveSpeed(0.0);
							thrower.state.stopPortcullis();
							driveState = DEFENSE_CROSSED;
						}
						break;
	
					case DEFENSE_CROSSED:
						defenseCleared = true;
						yAbs = 8 + ROBOT_LENGTH/2; 
						break;
				}
				break;
				
			//CHEVAL DE FRISE
			case DEFENSE_CHEVAL_DE_FRISE:
				switch(driveState) {
					case NOT_DRIVING:
						Common.debug("driveDefense: NOT_DRIVING - Starting cheval distance drive.");
						dt.driveDistance(44);
						driveState = DRIVING;
						break;
					
					case DRIVING:
						if (dt.driveComplete()) {
							arm.setArmPosition(0);
							dt.setDriveSpeed(0.1);
							detectTime = Common.time() + 2500; // was 2750
							driveState = LOWER_ARM;
						}
						break;
					case LOWER_ARM:
						if (Common.time() >= detectTime ) {
							dt.setDriveSpeed(.60);
							driveTime = Common.time();
							driveState = DRIVE_STEP_ONE;
						}
						break;
					case DRIVE_STEP_ONE:
						//Wait for robot to begin crossing the defense before raising arms.
						if (Common.time() - driveTime >= 500) {
							arm.setArmPosition(4);
							driveState = DRIVE_STEP_TWO;
							}
						break;
					case DRIVE_STEP_TWO:
						if (shieldCrossed()) {
							dt.setDriveSpeed(0.0);
							driveState = DEFENSE_CROSSED;
						}
						break;
					case DEFENSE_CROSSED:
						defenseCleared = true;
						yAbs = 22 + ROBOT_LENGTH/2; 
						break;
				}
				break;
				
			//MOAT
			case DEFENSE_MOAT:
				switch(driveState) {
					case NOT_DRIVING:
						arm.setArmPosition(4);
						driveState = DRIVING;
						break;
					
					case DRIVING:
						if (arm.moveCompleted()) {
							dt.setDriveSpeed(1.0);
							driveTime = Common.time();
							driveState = DRIVE_STEP_ONE;
						}
						break;
					case DRIVE_STEP_ONE:
						//Drive until shield is crossed or 2.0secs has passed
						if (shieldCrossed() || (Common.time() - driveTime) >= 2250) {
							dt.setDriveSpeed(0.0);
							driveState = DEFENSE_CROSSED;
						}
						break;
					
					case DEFENSE_CROSSED:
						defenseCleared = true;
						yAbs = 48 + ROBOT_LENGTH/2; 
						break;
				}
				break;
				
			//ROCK WALL
			case DEFENSE_ROCK_WALL:
				switch(driveState) {
					case NOT_DRIVING:
						arm.setArmPosition(3);
						dt.setDriveSpeed(0.85);
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
						yAbs = 55 + ROBOT_LENGTH/2; 
						break;
				}
				break;
				
			//RAMPARTS
			case DEFENSE_RAMPARTS:
				switch(driveState) {
					case NOT_DRIVING:
						dt.rotateTo(0);
						arm.setArmPosition(3);
						driveState = PRE_DEFENSE_ALIGN;
						break;
					case PRE_DEFENSE_ALIGN:
						if (dt.driveComplete()) {
							dt.setDriveSpeed(.85);
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
						break;
					case DEFENSE_CROSSED:
						defenseCleared = true;
						yAbs = 36 + ROBOT_LENGTH/2; 
						break;
				} 
				break;
		} 
		if (defenseCleared) {
			xAbs = paramStartingPlatform * PLATFORM_WIDTH - shieldDistance - ROBOT_WIDTH / 2;
		}
		return defenseCleared;
	}
					
	//Execute a full auto routine based on the user provided parameters.
	//Call this method from a robot loop, multiple times per second.
	public void autoRun() {
		// TESTING ONLY *****************
		/*paramStartingPlatform = 1;
		paramDefenseType = DEFENSE_LOWBAR;
		paramTargetPlatform = 2;
		paramSelectedAction = ACTION_UTURN;
		*/
		Common.dashNum("autoRun State", autoRunState);
		//***********************
		switch (autoRunState) {
			case AUTO_SETUP:
				Common.debug("autoRun: AUTO_SETUP");
				if (paramStartingPlatform > 0 && paramDefenseType > 0) {
					Common.debug("autoRun: AUTO_DRIVE - Starting Drive Defense" + paramDefenseType);
					autoRunState = AUTO_DRIVE;
				}
				break;
			case AUTO_DRIVE:
				if (driveDefense(paramDefenseType)) {
					Common.debug("autoRun: AUTO_DRIVE - Completed Drive Defense");
					autoRunState = AUTO_NEXT_ACTION;
				}	
				break;
			case AUTO_NEXT_ACTION:
				switch (paramSelectedAction) {
					case ACTION_STOP:
						Common.debug("autoRun: ACTION_STOP");
						dt.rotateTo(180);
						arm.setArmPosition(0);
						autoRunState = AUTO_STOP_ACTION;
						break;
					case ACTION_UTURN:
						Common.debug("autoRun: ACTION_UTURN");
						dt.rotateTo(uTurnLogic(paramTargetPlatform, paramStartingPlatform));  // Initiate first turn
						Common.dashNum("Turn Logic", uTurnLogic(paramTargetPlatform, paramStartingPlatform));
						Common.dashNum("targetHeading", dt.heading.getTargetHeading());
						autoRunState = AUTO_UTURN_STEP_1;
						break;
					case ACTION_SHOOT:
						autoRunState = AUTO_GOAL_LINE_APPROACH;
						Common.debug("autoRun: ACTION_SHOOT: Starting thrower motor and raising arm");  
						arm.setArmPosition(3);
						thrower.state.prepThrow();
						thrower.state.overrideFlashlight(true);	
						break;
					case ACTION_TOWER_ALIGN:
						Common.debug("autoRun: ACTION_TOWER_ALIGN");
						dt.rotateTo(absoluteTurnLogic(ABSOLUTE_CASTLE_X, xAbs));
						autoRunState = AUTO_SHOOT_STEP_1;
						break;
				}
				break;
			//AUTO UTURN
			case AUTO_UTURN_STEP_1:
				if (dt.driveComplete()) {
					Common.debug("autoRun: UTURN xAbs " + xAbs);
					Common.debug("autoRun: UTURN first turn complete");
					//Calculate absolute x center of target platform.
					xTargetCenter = paramTargetPlatform * PLATFORM_WIDTH - (PLATFORM_WIDTH * .5);
					xDistanceToTarget = Math.abs(xAbs - xTargetCenter);
					Common.debug("autoRun: UTURN distance to target " + xDistanceToTarget);
					dt.driveDistance(xDistanceToTarget);
					autoRunState = AUTO_UTURN_STEP_2;
				}				
				break;
			case AUTO_UTURN_STEP_2:
				if (dt.driveComplete()) {
					Common.debug("autoRun: UTURN Distance drive complete");
					dt.rotateTo(180);
					autoRunState = AUTO_UTURN_STEP_3;
				}
				break;
			case AUTO_UTURN_STEP_3:
				if (dt.driveComplete()) { 
					Common.debug("autoRun: UTURN Second turn complete");
					dt.setDriveSpeed(.6);
					shieldState = DETECT_RETURN;
					autoRunState = AUTO_UTURN_STEP_4;
				}
				break;
			case AUTO_UTURN_STEP_4:
				if (shieldReturn()) {
					Common.debug("autoRun: UTURN Return shield reached");
					autoRunState = AUTO_COMPLETE;
				}
				break;
				
			case AUTO_GOAL_LINE_APPROACH:
				if (paramStartingPlatform == 1) {
					distance = 108 - yAbs;
				} else if (paramStartingPlatform == 2) {
					distance = 139 - yAbs;
				} else if (paramStartingPlatform == 3) {
					distance = 0;
				} else if (paramStartingPlatform == 4) {
					distance = 0;
				} else {
					distance = 151 - yAbs;
				}
				Common.debug("autoRun: Starting goal line approach "  + distance);
				dt.driveDistance(distance);
				autoRunState = AUTO_ROTATE_TO_GOAL;
				break;
				
			case AUTO_ROTATE_TO_GOAL:
				if (dt.driveComplete()) {
					if (paramStartingPlatform == 1) {
						heading = 240;
					} else if (paramStartingPlatform == 2) {
						heading = 240; //240
					} else if (paramStartingPlatform == 3) {
						heading = 208;
					} else if (paramStartingPlatform == 4) {
						heading = 160; //was 168 
					} else {
						heading = 120;
					}
					Common.debug("autoRun: Completed drive, starting turn to goal, and lowering arms "  + heading);
					dt.rotateTo(heading);
					arm.setArmPosition(0);
					autoRunState = AUTO_AIM_TOWARD_GOAL;
				} 
				break;
				
			case AUTO_AIM_TOWARD_GOAL:
				if (dt.driveComplete()) {
					autoRunState = AUTO_DRIVE_BY_SHOOTING;
				}
				break;
				
			case AUTO_DRIVE_BY_SHOOTING:
				if (thrower.state.readyToThrow()) {
					if (Robot.vision.autoAim()) {
						Common.debug("autoRun: Completed turn to goal, starting auto aim");
						Common.debug("autoRun: AUTO_SHOOT Ball thrown");
						autoRunState = AUTO_COMPLETE;
					}
				}
				break;
				
			case AUTO_STOP_ACTION:
				if (dt.driveComplete() && arm.moveCompleted()) {
					Common.debug("autoRun: AUTO_STOP_ACTION Completed");
					autoRunState = AUTO_COMPLETE;
				}
				break;
				
			case AUTO_COMPLETE:
				dt.setDriveSpeed(0.0);
				dt.setTurnSpeed(0.0);
				break;
				
		}
		arm.update();
		dt.autoDrive();
		thrower.state.update();
	}
	
	//Determines right or left turn based on starting platform and target platform, returns a heading.
	public double uTurnLogic(int targetPlatform, int startingPlatform) {
		if (targetPlatform > startingPlatform) {
			return 90;
		} else {
			return 270;
		}
	}
	
	public double absoluteTurnLogic(double target, double current) {
		if (target > current) {
			return 90;
		} else {
			return 270;
		}
	}

	
}
