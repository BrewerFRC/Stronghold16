package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.Timer;
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
	public double detectTime;
	public double currentTime;
	
	public Auto(DriveTrain dt, Bat bat) {
		this.dt = dt;
		this.bat = bat;
	}
	
	public void updateAuto () {
		
		switch (currentState) {
			case 0:
				currentState = 1;
				Common.dashNum("currentState", currentState);
				break;
			
			case 1:
				if ((bat.getDistance() >= 0.0) && (bat.getDistance() <= 15.0)) { 
					detectTime = Timer.getFPGATimestamp();
					currentState = 2;
					Common.dashNum("currentState", currentState);
				} else {
					currentState = 1;
					Common.dashNum("currentState", currentState);
				}
				break;
				
			case 2:
				currentTime = Timer.getFPGATimestamp();
				if ((bat.getDistance() >= 0.0) && (bat.getDistance() <= 15.0)) {
					if(currentTime - detectTime >= 2.0) {
						currentState = 3;
						Common.dashNum("currentState", currentState);
					} else { 
						currentState = 2;
						Common.dashNum("currentState", currentState);
					}
				} else {
					currentState = 1;
					Common.dashNum("currentState", currentState);
					
				}
				break;
				
			case 3:
				if ( bat.getDistance() >= 15.0) {
					detectTime = Timer.getFPGATimestamp();
					currentState = 4;
					Common.dashNum("currentState", currentState);
				} else {
					currentState = 3;
					Common.dashNum("currentState", currentState);
				}	
				break;
				
			case 4:
				currentTime = Timer.getFPGATimestamp();
				if ((bat.getDistance() >= 0) && (bat.getDistance() <= 15.0)) {
					if (currentTime - detectTime >= 2.0) {
						currentState = 5;
						Common.dashNum("currentState", currentState);
					} else {
						currentState = 3;
					}
				} else {
					currentState = 3;
				}
			    break;
		}	
	}
}
