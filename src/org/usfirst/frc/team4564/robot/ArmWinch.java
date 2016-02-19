package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;

public class ArmWinch {
	
	private static final boolean LOW_LIMIT_REACHED = false;
	private static final boolean HIGH_LIMIT_REACHED = false;
	private static boolean slowArm = false;
	private DigitalInput winchLimitLow = new DigitalInput(Constants.DIO_ARM_WINCH_LOW_LIMIT);
	private DigitalInput winchLimitHigh = new DigitalInput(Constants.DIO_ARM_WINCH_HIGH_LIMIT);
	private Talon armMotor = new Talon(Constants.PWM_WINCH_ARM);

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
			setWinchMotor(0);
		}
		else {
			setWinchMotor(0.10);
		}
	}
	
	public void moveDown(){
		if (slowArm) {
			setWinchMotor(-0.25);
		}
		else {
			setWinchMotor(-0.5);
		}
	}
	
	public void stopArm(){
		setWinchMotor(-0.13);
	}
	
	public static void setSlowArm(boolean slow) {
		slowArm = slow;
	}
}
