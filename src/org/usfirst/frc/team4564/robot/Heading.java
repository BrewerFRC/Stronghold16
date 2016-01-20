package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.Gyro;

public class Heading {
	private Gyro gyro;
	private PID pid;
	private double targetHeading;
	private boolean headingHold;
	
	public Heading(int pin, double p, double i, double d, double sensitivity) {
		pid = new PID(p, i, d, true);
		//PID is dealing with error; an error of 0 is always desired.
		pid.setTarget(0.0);
		targetHeading = 0.0;
		gyro = new Gyro(pin);
	}
	
	public void reset() {
		gyro.reset();
		resetPID();
	}
	public void resetPID() {
		pid.reset();
	}
	
	public boolean setTarget(double target) {
		//Disallow target set when heading is not enabled.
		if (headingHold) {
			pid.setTarget(target);
			return true;
		}
		return false;
	}
	
	public double getHeading() {
		//Partial degrees, 0.01 accuracy
		return (gyro.getAngle()*100) % 360 / 100;
	}
	
	public void headingHold() {
		if (headingHold) {
			reset();
			headingHold = false;
		}
		else {
			resetPID();
			headingHold = true;
			//Set target angle to current heading.
			targetHeading = getHeading();
		}
	}
	public boolean isHeadingHold() {
		return headingHold;
	}
	
	public double turnRate(double input) {
		if (headingHold) {
			//Find the short and long path to the desired heading.
			double changeLeft = (-360 + (targetHeading - input)) % 360;
			double changeRight = (360 - (input - targetHeading)) % 360;
			
			//Return the PID calculation of the shorter path.
			return pid.calc((changeLeft < changeRight) ? changeLeft : changeRight);
		}
		else {
			return 0.0;
		}
	}
}
