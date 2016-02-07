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
	
	public static class ThrowerState {
		private static int READY = 0;
		private static int INTAKE = 1;
		private static int BALL_DETECT = 2;
		private static int PREP_SHOOT = 3;
		private static int SPIN_UP = 4;
		
		private int currentState;
		
	}
	
	
	
}
