package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.Talon;

public class Thrower {
	
	static Talon flywheel = new Talon(Constants.PWM_THROWER_FLYWHEEL);
	static Talon internalIntake = new Talon(Constants.PWM_THROWER_INT_INTAKE);
	
	
	public void setFlywheel(double speed){
		flywheel.set(Math.abs(speed));
	}
	public void setInternalIntake(double speed){
		internalIntake.set(speed);
	}
	public void pickUpBall(){
		internalIntake.set(1.00);
		
	}
	
	
	
}
