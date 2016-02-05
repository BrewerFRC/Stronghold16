package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.Timer;
public class Auto {

	DriveTrain dt;
	Bat bat;
	
	//Sheild Constants
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
	public static final double MIN_SHEILD_DISTANCE = 0;
	public static final double MAX_SHEILD_DISTANCE = 15;
	public static final double VERIFICATION_TIME = 2;
	
	//Gate Variables
	public int currentState = 0;
	public double detectTime;
	public double currentTime;
	
	public Auto(DriveTrain dt, Bat bat) {
		this.dt = dt;
		this.bat = bat;
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
						currentState = PREPPING_FOR_FIRST_TURN;
					} else {
						currentState = DETECT_EXIT;
					}
				} else {
					currentState = DETECT_EXIT;
				}
			    break;
		}
	}

	
	public class Shield {	
		
		public static final int NOT_DRIVING = 0;
		public static final int DRIVING = 1;
		public static final int DEFENSE_CROSSED = 2;
		public static final int TURNING = 3;
		
		public int defenseType = 0;
		public int driveState = 0;
		// Input for turn logic
		public int targetPlatform = 0;
		public int currentPlatform = 0;		
		Shield shield = new Shield();
		DriveTrain drivetrain = new DriveTrain();
		
		public void driveDefense(int defenseType) {
		
			switch(defenseType) {
				case 0:
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
						if (driveState == DEFENSE_CROSSED) {
							dt.rotateTo(turnLogic(targetPlatform, currentPlatform));
							driveState = TURNING;
						}
						if (driveState == TURNING)
						
				break;
			}
		}	
		
		public double turnLogic(int targetPlatform, int currentPlatform) {
			if ( targetPlatform - currentPlatform > 0) {
				return 90;
			} else {
				return -90;
			}
		}
		
		public double moveToPlatform(int currentPlatform, int targetPlatform) {
			public int deltaPlatform = Math.abs(currentPlatform - targetPlatform * 25);
			dt.driveDistance(distanceToTarget);
		}
		
		public boolean defenseComplete() {
			if (currentState == PREPPING_FOR_FIRST_TURN) {
				return true;
			}
		}
	}
}	