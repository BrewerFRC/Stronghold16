package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;

public class ArmWinch {
	private DigitalInput winchLimitLow = new DigitalInput(Constants.DIO_ARM_WINCH_LOW_LIMIT);
	private DigitalInput winchLimitHigh = new DigitalInput(Constants.DIO_ARM_WINCH_HIGH_LIMIT);
	public AnalogInput potentiometer = new AnalogInput(Constants.ANA_ARM_WINCH_POT);
	private Talon armMotor = new Talon(Constants.PWM_WINCH_ARM);
	
	private static final boolean LOW_LIMIT_REACHED = false;
	private static final boolean HIGH_LIMIT_REACHED = false;
	private static final double STOP_POWER = -0.13;
	private static final double ARM_POT_LOW = 1.66; //old 3.58 - Voltage at lowest arm position.
	private static final double ARM_POT_HIGH = 0.48; //0.38; //old 2.2 - Voltage at highest arm position.
	private static final double ARM_ALLOWABLE_ERROR = 0.05;  //Amount of error arm can be off from target before completing move.
	private static final double ARM_CLIMB_POT = 0.48; //Arm position for climbing.
	private static boolean slowArm = false;
	private static boolean autoControl = false;
	public double target;
	private double speed;

	public double getPotentiometerVoltage() {
		return potentiometer.getVoltage();
	}
	
	//Sets target arm position with 0 being the lowest and 6 being the highest
	public void setArmPosition(int position) {
		autoControl = true;
		if (position < 0) {
			position = 0;
		}
		if (position > 6) {
			position = 6;
		}
		target = ARM_POT_LOW + (ARM_POT_HIGH - ARM_POT_LOW) / 6 * position;
		Common.debug("Arm setArmPosition: Set arm target:" + target);
	}
	
	public void setForClimb() {
		if (getPotentiometerVoltage() < ARM_CLIMB_POT) {
			autoControl = true;
			target = ARM_CLIMB_POT;
		}
	}
	
	public void setWinchMotor(double power){
		if (power > 0) {
			if (winchLimitHigh.get() == HIGH_LIMIT_REACHED) {
				power = STOP_POWER;
				autoControl = false;
			}
		}
		if (power < 0) {
			if (winchLimitLow.get() == LOW_LIMIT_REACHED) {
				power = STOP_POWER;
				autoControl = false;
			}
		}
		armMotor.set(power);
	}
		
	private void calcUp(){
		if (slowArm) {
			speed = 0;
		}
		else {
			speed = 0.10;
		}
	}
	
	private void calcDown(){
		if (slowArm) {
			speed = -0.35;
		}
		else {
			speed = -0.5;
		}
	}
	
	private void calcStop(){
		speed = STOP_POWER;
	}
	
	public void moveUp() {
		autoControl = false;
		calcUp();
	}
	
	public void moveDown() {
		autoControl = false;
		calcDown();
	}
	
	public void stopArm() {
		autoControl = false;
		calcStop();
	}
	
	public boolean getSlowArm() {
		return slowArm;
	}
	
	public static void setSlowArm(boolean slow) {
		slowArm = slow;
	}
	
	public boolean moveCompleted() {
		return !autoControl;
	}
	
	public void update() {
		if (autoControl) {
			if ((Math.abs(getPotentiometerVoltage() - target) <= ARM_ALLOWABLE_ERROR)) {
				Common.debug("Arm update: Stopping Arm at:" + getPotentiometerVoltage());
				stopArm();
				return;
			}
			if (getPotentiometerVoltage() < target) {
				calcDown();
			}
			else {
				calcUp();
			}
		}
		setWinchMotor(speed);
		speed = STOP_POWER;
	}
}
