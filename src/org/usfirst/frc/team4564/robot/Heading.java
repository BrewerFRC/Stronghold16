package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class Heading {
	private Gyro gyro;
	private PID pid;
	private boolean headingHold;
	
	public Heading(int pin, double p, double i, double d, double sensitivity) {
		pid = new PID(p, i, d, true);
		//PID is dealing with error; an error of 0 is always desired.
		pid.setTarget(0.0);
		gyro = new AnalogGyro(pin);
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
	
	public void setHeading(double heading) {
		//Find the short and long path to the desired heading.
		double changeLeft = (-360 + (heading - getHeading())) % 360;
		double changeRight = (360 - (getHeading() - heading)) % 360;
		double change = (Math.abs(changeLeft) < Math.abs(changeRight)) ? changeLeft : changeRight;
		
		pid.setTarget(gyro.getAngle() + change);
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
			setHeading(getHeading());
		}
	}
	public boolean isHeadingHold() {
		return headingHold;
	}
	
	public double turnRate() {
		if (headingHold) {
			//Return the PID calculation of the shorter path.
			return pid.calc(gyro.getAngle());
		}
		else {
			return 0.0;
		}
	}
}
