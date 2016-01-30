package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.Timer;
public class Auto {

	DriveTrain dt;
	Bat bat;
	Heading h;
	
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
	public void updateAuto () {
		if (currentState == 0) {
			Common.debug("Starting Init");
			currentState++;
		} else {
			
		}
		
		if (currentState == 1) {
			Common.debug("Searching For Wall");
			if ((bat.getDistance() >= 0) && (bat.getDistance() <= 2.0)) { 
				detectTime = Timer.getFPGATimestamp();
				Common.debug("Panel Detected");
				currentState = 2;
			} else {
				currentState = DETECT_APPROACH;
			}
			
		} else {
		
		}
		
		if (currentState == 2) {
			Common.debug("Verifying Approach");
			currentTime = Timer.getFPGATimestamp();
			if ((currentTime - detectTime == 6000) && (bat.getDistance() >= 0) && (bat.getDistance() <= 2.0))  {
				Common.debug("Panel Verified");
				currentState = 3;
			} else {
				currentState = 2;
			}
			
	//	Common.debug("Searching For Exit");
	//	Common.debug("Verifying Exit");
	//	Common.debug("Preparing To Execute Second Turn");
	//	Common.debug("Exectuting First Turn");
	//	Common.debug("Verifying First Turn Completed");
	//	Common.debug("Preping To Exectue Second Turn");
	//	Common.debug("Executing Second Turn");
	//	Common.debug("Verifying Second Turn Completed");
	//	Common.debug("Returning to Defense");
	//	Common.debug("Verifying Return");
	//	Common.debug("Opening Defense");
		
		}
		
	}
	
}
