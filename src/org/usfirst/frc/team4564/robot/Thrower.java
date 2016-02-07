package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class Thrower {
	
	private Talon flywheel = new Talon(Constants.PWM_THROWER_FLYWHEEL);
	private Talon internalIntake = new Talon(Constants.PWM_THROWER_INT_INTAKE);
	
	public Encoder encoder = new Encoder(Constants.DIO_FLYWHEEL_ENCODER_A, Constants.DIO_FLYWHEEL_ENCODER_B, 
			true, EncodingType.k1X);
	
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
		private static int FIRE = 5;
		private Thrower thrower;
		private int currentState;
		
		public ThrowerState(Thrower thrower) {
			currentState = READY;
			this.thrower = thrower;
		}
		
		public void update() {
			switch(currentState) {
				case 0:
					if (Robot.j.whenA()) {
						currentState = INTAKE;
					}
					break;
				case 1:
					thrower.setInternalIntake(1.0);
					if () {
						
					}
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
			}
		}
	}
	
	
	
}
