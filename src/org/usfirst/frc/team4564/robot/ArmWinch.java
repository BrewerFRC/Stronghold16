package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;

public class ArmWinch {
	private DigitalInput winchLimitLow = new DigitalInput(Constants.DIO_ARM_WINCH_LOW_LIMIT);
	private DigitalInput winchLimitHigh = new DigitalInput(Constants.DIO_ARM_WINCH_HIGH_LIMIT);
	private AnalogInput potentiometer = new AnalogInput(Constants.ANA_ARM_WINCH_POT);
	private Talon armMotor = new Talon(Constants.PWM_WINCH_ARM);
	
	private static final boolean LOW_LIMIT_REACHED = false;
	private static final boolean HIGH_LIMIT_REACHED = false;
	private static boolean slowArm = false;
	private static boolean autoControl = false;
	private double target;
	private double speed;
	
	public void updateAutoControl() {
		if (!autoControl) {
			return;
		}
		if (!(Math.abs(getPotentiometerPosition() - target) <= Constants.ARM_WINCH_ERROR)) {
			stopArm();
			autoControl = false;
			return;
		}
		if (getPotentiometerPosition() > target) {
			moveDown();
		}
		else {
			moveUp();
		}
	}
	
	public void update() {
		setWinchMotor(speed);
	}

	public double getPotentiometerPosition() {
		return potentiometer.getVoltage();
	}
	
	public void setArmPosition(int position) {
		autoControl = true;
		if (position < 0) {
			position = 0;
		}
		if (position > 4) {
			position = 4;
		}
		target = Constants.ARM_WINCH_POT_LOW_VOLTAGE + Constants.ARM_WINCH_POT_RANGE / 4 * position;
	}
	
	public void setWinchMotor(double power){
		if (power > 0) {
			if (winchLimitHigh.get() == HIGH_LIMIT_REACHED) {
				power = 0;
			}
		}
		if (power < 0) {
			if (winchLimitLow.get() == LOW_LIMIT_REACHED) {
				power = 0;
			}
		}
		armMotor.set(power);
	}
		
	public void moveUp(){
		if (slowArm) {
			speed = 0;
		}
		else {
			speed = 0.10;
		}
	}
	
	public void moveDown(){
		if (slowArm) {
			speed = -0.25;
		}
		else {
			speed = -0.5;
		}
	}
	
	public void stopArm(){
		speed = -0.13;
	}
	
	public static void setSlowArm(boolean slow) {
		slowArm = slow;
	}
}
