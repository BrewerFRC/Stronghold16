package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;

public class ArmWinch {
	
	public static final boolean LOW_LIMIT_REACHED = false;
	public static final boolean HIGH_LIMIT_REACHED = false;
	public DigitalInput winchLimitLow = new DigitalInput(Constants.DIO_ARM_WINCH_LOW_LIMIT);
	public DigitalInput winchLimitHigh = new DigitalInput(Constants.DIO_ARM_WINCH_HIGH_LIMIT);
	public Talon armMotor = new Talon(Constants.PWM_WINCH_ARM);

	
		
		public void setWinchMotor(double power){
			
			/*if (power > 0) {
				if (winchLimitHigh.get() == HIGH_LIMIT_REACHED) {
					power = 0;
				}
			}
			
			if (power < 0) {
				if (winchLimitLow.get() == LOW_LIMIT_REACHED) {
					power = 0;
				}
			}*/
			
			armMotor.set(power);
		}
		
		public void moveUp(){
			setWinchMotor(.10);
		}
		
		public void moveDown(){
			setWinchMotor(-.35);
		}
		
		public void stopArm(){
			setWinchMotor(-.13);
		}

}
