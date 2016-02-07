package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.Timer;
public class Auto {

	DriveTrain dt;
	Bat bat;
	Thrower t;
	public Shield shield;
	
	//Sheild Constants
	public static final int AUTO_INIT = 0;
	public static final int DETECT_APPROACH = 1;
	public static final int APPROACH_WAIT = 2;
	public static final int DETECT_EXIT = 3;
	public static final int EXIT_WAIT = 4;
	public static final int DETECT_RETURN = 5;
	public static final int RETURN_WAIT = 6;
	public static final int DETECT_RETURN_EXIT = 7;
	public static final int RETURN_EXIT_WAIT = 8;
	public static final int CROSSED_RETURN_DEFENSE = 9;
	public static final double MIN_SHEILD_DISTANCE = 0;
	public static final double MAX_SHEILD_DISTANCE = 15;
	public static final double VERIFICATION_TIME = 2;
	
	//Auto Variables
	public int currentState = 0;
	public double detectTime;
	public double currentTime;
	public double shieldDistance;
	
	public Auto(DriveTrain dt, Bat bat) {
		this.dt = dt;
		this.bat = bat;
		shield = new Shield();
		this.t = t;
		dt.setPIDDrive(true);
	}
	
	public void updateAuto () {
		
		switch (currentState) {
			case 0:
				currentState = DETECT_APPROACH;
				break;
			
			case 1:
				if ((bat.getDistance() >= MIN_SHEILD_DISTANCE) && (bat.getDistance() <= MAX_SHEILD_DISTANCE)) { 
					detectTime = Timer.getFPGATimestamp();
					currentState = APPROACH_WAIT;
				} else {
					currentState =  DETECT_APPROACH;
				}
				break;
				
			case 2:
				currentTime = Timer.getFPGATimestamp();
				if ((bat.getDistance() >= MIN_SHEILD_DISTANCE ) && (bat.getDistance() <= MAX_SHEILD_DISTANCE )) {
					if(currentTime - detectTime >= VERIFICATION_TIME) {
						currentState = DETECT_EXIT;
					} else { 
						currentState = APPROACH_WAIT;
					}
				} else {
					currentState = DETECT_APPROACH;
				}
				break;
				
			case 3:
				if ( bat.getDistance() >= MAX_SHEILD_DISTANCE) {
					detectTime = Timer.getFPGATimestamp();
					currentState = EXIT_WAIT;
				} else {
					currentState = DETECT_EXIT;
				}	
				break;
				
			case 4:
				currentTime = Timer.getFPGATimestamp();
				if ((bat.getDistance() >= MIN_SHEILD_DISTANCE) && (bat.getDistance() <= MAX_SHEILD_DISTANCE)) {
					if (currentTime - detectTime >= VERIFICATION_TIME) {
						currentState = DETECT_RETURN;
						shieldDistance = bat.getDistance();
					} else {
						currentState = DETECT_EXIT;
					}
				} else {
					currentState = DETECT_EXIT;
				}
			    break;
			    
			case 5:
				if ((bat.getDistance() >= MIN_SHEILD_DISTANCE) && (bat.getDistance() <= MAX_SHEILD_DISTANCE)) { 
					detectTime = Timer.getFPGATimestamp();
					currentState = RETURN_WAIT ;
				} else {
					currentState =  DETECT_RETURN;
				}
				break;
				
			case 6:
				currentTime = Timer.getFPGATimestamp();
				if ((bat.getDistance() >= MIN_SHEILD_DISTANCE) && (bat.getDistance() <= MAX_SHEILD_DISTANCE)) {
					if (currentTime - detectTime >= VERIFICATION_TIME) {
						currentState = DETECT_RETURN_EXIT;
					} else {
						currentState = DETECT_RETURN;
					}
				} else {
					currentState = RETURN_WAIT;
				}
			    break;
			    
			case 7:
				if ( bat.getDistance() >= MAX_SHEILD_DISTANCE) {
					detectTime = Timer.getFPGATimestamp();
					currentState = RETURN_EXIT_WAIT;
				} else {
					currentState = DETECT_RETURN_EXIT;
				}	
				break;
				
			case 8:
				currentTime = Timer.getFPGATimestamp();
				if ((bat.getDistance() >= MIN_SHEILD_DISTANCE) && (bat.getDistance() <= MAX_SHEILD_DISTANCE)) {
					if (currentTime - detectTime >= VERIFICATION_TIME) {
						currentState = CROSSED_RETURN_DEFENSE;
					} else {
						currentState = RETURN_EXIT_WAIT;
					}
				} else {
					currentState = DETECT_RETURN_EXIT;
				}
			    break;
		}
	}

	
	public class Shield {	
		
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
		public static final double PLATFORM_WIDTH = 52.5;
		public static final double ABSOLUTE_CASTLE_X = 170.6113;
		public static final double AUTO_HIGHGOAL_SPEED = 100;
		public int defenseType;
		public int selectedAction;
		public int driveState = 0;
		public int targetPlatform;
		public int target;
		public int startingPlatform;
		DriveTrain drivetrain = new DriveTrain();
		
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
}
		
		public double turnLogic(double target, int startingPlatform) {
			if ( target - startingPlatform > 0) {
				return 90;
			} else {
				return -90;
			}
		}
		
		public double xAbs(int startingPlatform, double PLATFORM_WIDTH, double shieldDistance) {
			return (startingPlatform * PLATFORM_WIDTH - shieldDistance);
		}
		
		public double xPlatformTarget(int targetPlatform, double PLATFORM_WIDTH, double shieldDistance) {
			return (targetPlatform * PLATFORM_WIDTH - (PLATFORM_WIDTH * .5));
		}
		
		public double xCastleTarget(double ABSOLUTE_CASTLE_X, double xABS){
			return (Math.abs(xABS - ABSOLUTE_CASTLE_X));
		}
		
		public double xDrive (double xAbs, double xTarget) {
			return (xAbs - xTarget);
		}
		
		public boolean defenseComplete() {
			if (currentState == DETECT_RETURN) {
				return (true);
			} else { 
				return (false);
			}
		}
		
		public boolean returnDefenseComplete() {
			if (currentState == CROSSED_RETURN_DEFENSE) {
				return (true);
			} else {
				return (false);
			}
		}
	}
}