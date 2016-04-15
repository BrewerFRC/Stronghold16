package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class VisionTracking {
	NetworkTable visionTable;
	DriveTrain dt;
	long counter = 0;
	
	public VisionTracking(DriveTrain dt) {
		this.dt = dt;
		this.visionTable = NetworkTable.getTable("visionTracking");
	}

	public boolean autoAim() {
		double turn = visionTable.getNumber("targetTurn", 0);
		boolean thrown = false;
		if (counter % 6 <= 2) {
			if (turn == 999) {
				Common.debug("autoAim: taking shot");
				Robot.thrower.state.throwBall();
				dt.baseDrive(0, 0);
				thrown = true;
			}
			else {
				Common.debug("autoAim: turning to target");
				dt.baseDrive(-.3, turn);
			}
		}
		else {
			if (turn <= 1.0) {
				dt.baseDrive(-.3, turn * 0.3);
			}
		}
		counter++;
		return thrown;
	}
	
	public void takePicture() {
		Common.debug("AutoAim: taking picture");
		visionTable.putNumber("takePicture", 1);
	}
}
