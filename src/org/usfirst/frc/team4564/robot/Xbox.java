package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Xbox extends Joystick {
	
	private boolean prevA = false;
	private boolean prevB = false;
	private boolean prevX = false;
	private boolean prevY = false;
	private boolean prevRightBumper = false;
	private boolean prevLeftBumper = false;
	private boolean prevSelect = false;
	private boolean prevStart = false;
	private boolean prevLeftClick = false;
	private boolean prevRightClick = false;
	private boolean prevDpadUp = false;
	private boolean prevDpadDown = false;
	private boolean prevDpadRight = false;
	private boolean prevDpadLeft = false;
	
	public Xbox(int port) {
		super(port);
	}
	
	private double deadzone(double input) {
		if (Math.abs(input) < .2) {
			return (0);
		} else {
				return (input);
			
			}
		}
	public boolean A() {
		return getRawButton(1);
	}
	
	public boolean whenA() {
		if (A()) {
			if (prevA) {
				return false;
			} else {
				prevA = true;
				return true;
			}
		} else {
			prevA = false;
			return false;
		}

	}
	public boolean B() {
		return getRawButton(2);
		}
	
	public boolean whenB() {
		if (B()) {
			if (prevB) {
				return false;
			} else {
				prevB = true;
				return false;
			}
		} else {
			prevB = false;
			return false;
			
		}
	}
		
	public boolean X() {
		return getRawButton(3);
	}
	
	public boolean whenX() {
		if (X()) {
			if (prevX) {
				return false;
			} else {
				prevX = true;
				return true;
			}
		} else {
			prevX = false;
			return false;
		}
	}
	
	public boolean Y() {
		return getRawButton(4);
	}
	
	public boolean whenY() {
		if (Y()) {
			if (prevY) {
				return false;
			} else {
				prevY = true;
				return true;
			}
		} else {
			prevY = false;
			return false;
		}
	}
	
	public boolean rightBumper() {
		return getRawButton(6);
	}
	
	public boolean whenRightBumper() {
		if (rightBumper()) {
			if (prevRightBumper) {
				return false;
			} else {
				prevRightBumper = true;
				return true;
			}
		} else {
			prevLeftBumper = true;
			return true;
		}
	}
	
	public boolean leftBumper() {
		return getRawButton(5);
	}
	
	public boolean whenLeftBumper() {
		if (leftBumper()) {
			if (prevLeftBumper) {
				return false;
			} else {
				prevLeftBumper = true;
				return true;
			}
		}
		
	}
}
	
