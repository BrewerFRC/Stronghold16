package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class VisionTracking {
	NetworkTable visionTable;
	DriveTrain dt;
	
	public static final double TARGET_DISTANCE = 49-5;		// Inches from tower for shot alignment, as measured by utlrasonic, batter distance - bumper to wheel distance
	long counter = 0;
	
	public VisionTracking(DriveTrain dt) {
		this.dt = dt;
		this.visionTable = NetworkTable.getTable("visionTracking");
	}

	public boolean autoAim() {
		dt.setHeadingHold(false);		//Aiming requires that heading hold be off
		double distance;				//Distance from tower, measured by ultrasonic.
		double speed;					//The speed at which to move to/from tower to get proper distance for shot
		boolean shoot;					//True when targeting computer issues '999' response
		double turn;					//Turn speed, provided by targeting computer and adjusted for pulse drive
		
		// Process targeting computer data
		double targetingData = visionTable.getNumber("targetTurn", 0);  
		if (targetingData == 999) {
			shoot = true;
			turn = 0;
		} else {
			shoot = false;
			if (counter % 6 < 2) { 		//If not aligned for shot, pulse the turn rate between full power and 1/3 power
				turn = targetingData;
			} else {
				turn = targetingData * 0.3;
			}
			counter++;			
		}
		
		// Drive forward/backward until proper distance from tower
		// ***** Insert Ultrasonic logic here
		distance = Robot.bat.getShooterDistance();   
		double diff = TARGET_DISTANCE - distance;
		// Determine appropriate drive speed based on distance form tower
		if (Math.abs(diff) < 2) {
			speed = 0.0;  //Stop driving, we are within shooting range
		} else {
			if (Math.abs(diff) < 18) {  //Scale speed of approach when we are close
				speed = 0.4;
			} else {
				speed = 0.65;
			}
			if (diff < 0) {  //Determine what direction to travel, either toward or away from tower
				speed = speed * -1;  // Negative value drives toward tower
			}
		}
		
		// Is it time to shoot or keep aiming?
		boolean thrown = false;
		if (shoot && speed == 0.0) {    // If we are aligned and not driving and...
			if (Robot.thrower.state.readyToThrow()) { //...thrower is ready, then take the shot.
				Common.debug("autoAim: taking shot");
				Robot.thrower.state.throwBall();
				thrown = true;
			}
		}
		
		// Move the robot as necessary
		dt.setDriveSpeed(speed);
		dt.setTurnSpeed(turn);

		return thrown;
	}
	
	public void takePicture() {
		Common.debug("AutoAim: taking picture");
		visionTable.putNumber("takePicture", 1);
	}
}
