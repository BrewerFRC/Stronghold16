package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.Timer;
public class Auto {

	DriveTrain dt;
	Bat bat;
	Thrower t;
	public Shield shield;
	
	//Shield detection states for driving through a defense
	public static final int AUTO_INIT = 0;						// Auto mode has to be initialed
	public static final int DETECT_APPROACH = 1;				// Awaiting for inital Shield detect
	public static final int APPROACH_WAIT = 2;					// Verify shield detect for a set amount of time
	public static final int DETECT_EXIT = 3;					// Within the defense, awaiting exit from Shield
	public static final int EXIT_WAIT = 4;						// Verify shield exit for a set amount of time
	public static final int DEFENSE_CLEARED = 5;				// Verified that defense has been cleared
	//Shield detection states for returning through Portcullis or Sally Port
	public static final int DETECT_RETURN = 10;					// Awaiting for initial Shield detect
	public static final int RETURN_WAIT = 11;					// Verify shield detect for a set amount of time
	public static final int IN_DEFENSE = 12;					// Robots within the defense
	//driveState constants.
	public static final int NOT_DRIVING = 0;
	public static final int DRIVING = 1;
	public static final int DEFENSE_CROSSED = 2;
	public static final int TURNING = 3;
	public static final int TURN_COMPLETE = 4;
	public static final int MOVING_TO_TARGET = 5;
	public static final int REACHED_TARGET = 6;
	public static final int MOVING_TO_OPEN = 7;
	public static final int OPENING_DEFENSE = 8;
	public static final int ALIGNMENT_COMPLETE = 9;
	public static final int FIRING = 10;
	public static final int FINI = 11;
	//Field Dimension constants.
	public static final double PLATFORM_WIDTH = 52.5;
	public static final double ABSOLUTE_CASTLE_X = 170.6113;
	//Auto action constants.
	public static final double AUTO_HIGHGOAL_SPEED = 100;
	//driveDefense variables
	public int defenseType;
	public int selectedAction;
	public int driveState = 0;
	public int targetPlatform;
	public int target;
	public int startingPlatform;

	
	
	
	
	
	//public static final double MIN_SHIELD_DISTANCE = 0;
	public static final double MAX_SHIELD_DISTANCE = 15;
	public static final double VERIFICATION_TIME = 300;		//Amount of necessary time to verify panel detect
	
	//updateAuto variables
	public int shieldState = AUTO_INIT;
	public long detectTime;		//Start time of Sheild detect
	//public long currentTime;
	public double shieldDistance;  //The ultrasonic sensor distance upon exit of a defense
	
	//Auto constructor
	public Auto(DriveTrain dt, Bat bat) {
		this.dt = dt;
		this.bat = bat;
		shield = new Shield();
		dt.setPIDDrive(true);
	}
	
	// Prepare for auto run
	public void init() {
		shieldState = DETECT_APPROACH;
		// Setup drivetrain
    	dt.init();
        dt.setSafetyEnabled(false);
    	dt.heading.setHeadingHold(true);
    	// Get initial auto parameters from network tables.  They'll default to zero if not specified.
    	shield.startingPlatform = (int) Robot.table.getNumber("platform", 0);
    	shield.targetPlatform = (int) Robot.table.getNumber("targetPlatform", 0);
    	shield.defenseType = (int) Robot.table.getNumber("defense", 0);
    	shield.selectedAction = (int) Robot.table.getNumber("action", 0);
	}
	
	// Using the ultrasonic sensor, this logic determines when the robot passes through a shield.
	// The value of "shieldDistance" will give the distance that the robot is away from the shield.
	// Returns true when defense is cleared. 
	public boolean shieldCross () {
		boolean isCleared = false;
		switch (shieldState) {

			case AUTO_INIT:
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
				if (bat.getDistance() <= MAX_SHIELD_DISTANCE) {
					if (Common.time() - detectTime >= VERIFICATION_TIME) {
						shieldState = DEFENSE_CLEARED;
					} else {
						shieldState = DETECT_EXIT;
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

	
		

		
		//Execute Auto based upon startingPlatform, defenseType, and selectedAction.
		public void driveDefense(int defenseType) {
		
			switch(defenseType) {
				case 0:
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

		
		//Determines right or left turn based on starting position and target.
		public double turnLogic(double target, int startingPlatform) {
			if ( target - startingPlatform > 0) {
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
		
		//Have we crossed the initial defense?
		public boolean defenseComplete() {
			if (shieldState == DETECT_RETURN) {
				return (true);
			} else { 
				return (false);
			}
		}
		// Have we crossed the defense when returning?
		public boolean returnDefenseComplete() {
			if (shieldState == CROSSED_RETURN_DEFENSE) {
				return (true);
			} else {
				return (false);
			}
		}
	}
}