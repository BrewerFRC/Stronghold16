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
		boolean thrown = false;
		if (counter % 6 <= 2) {
			double turn = visionTable.getNumber("targetTurn", 0);
			if (turn == 999) {
				Robot.thrower.state.throwBall();
				dt.baseDrive(0, 0);
				thrown = true;
			}
			else {
				dt.baseDrive(0, turn);
			}
		}
		else {
			dt.baseDrive(0, 0);
		}
		counter++;
		return thrown;
	}
	
}
