package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;

public class Thrower {
	public ThrowerState state;
	
	private Talon flywheel = new Talon(Constants.PWM_THROWER_FLYWHEEL);
	private Talon internalIntake = new Talon(Constants.PWM_THROWER_INT_INTAKE);
	private Talon externalIntake = new Talon(Constants.PWM_THROWER_EXT_INTAKE);
	//Normally closed
	private DigitalInput ballDetect = new DigitalInput(Constants.DIO_THROWER_BALL_DETECT);
	
	public Encoder encoder = new Encoder(Constants.DIO_FLYWHEEL_ENCODER_A, Constants.DIO_FLYWHEEL_ENCODER_B, 
			true, EncodingType.k1X);
	
	public Thrower() {
		state = new ThrowerState(this);
		encoder.setDistancePerPulse(1/1024);
	}
	
	public double getFlywheelPower() {
		return flywheel.get();
	}
	public void setFlywheel(double speed){
		flywheel.set(Math.abs(speed));
	}
	public double getInternalIntakePower() {
		return internalIntake.get();
	}
	public void setInternalIntake(double speed){
		internalIntake.set(speed);
	}
	public double getExternalIntakePower() {
		return externalIntake.get();
	}
	public void setExternalIntake(double speed) {
		externalIntake.set(speed);
	}
	public void pickUpBall(){
		internalIntake.set(1.00);
		
	}
	
	public static class ThrowerState {
		private static final int READY = 0;
		private static final int INTAKE = 1;
		private static final int BALL_DETECT = 2;
		private static final int PREP_SHOOT = 3;
		private static final int SPIN_UP = 4;
		private static final int FIRE = 5;
		private Thrower thrower;
		private int currentState;
		private long prepStart;
		private long fireStart;
		
		public ThrowerState(Thrower thrower) {
			currentState = READY;
			this.thrower = thrower;
		}
		
		public int update() {
			switch(currentState) {
				case READY:
					if (Robot.j.whenA()) {
						currentState = INTAKE;
					}
					break;
				case INTAKE:
					thrower.setInternalIntake(1.0);
					thrower.setExternalIntake(1.0);
					/*
					if (thrower.encoder.getRate()*60 >= -10) {
						currentState = BALL_DETECT;
					}
					*/
					if (thrower.ballDetect.get()) {
						currentState = BALL_DETECT;
					}
					break;
				case BALL_DETECT:
					thrower.setInternalIntake(-0.15);
					thrower.setExternalIntake(-0.15);
					currentState = PREP_SHOOT;
					break;
				case PREP_SHOOT:
					if (Robot.j.whenB()) {
						prepStart = Common.time();
					}
					if (Common.time() - prepStart > 250) {
						thrower.setInternalIntake(0.0);
						thrower.setExternalIntake(0.0);
						currentState = SPIN_UP;
					}
					break;
				case SPIN_UP:
					//TODO: Velocity control
					thrower.setFlywheel(1.0);
					if (true /*Velocity reached*/) {
						currentState = FIRE;
					}
					break;
				case FIRE:
					if (Robot.j.rightTriggerPressed()) {
						thrower.setInternalIntake(0.15);
						fireStart = Common.time();
					}
					if (Common.time() - fireStart > 500) {
						thrower.setInternalIntake(0.0);
						thrower.setExternalIntake(0.0);
						thrower.setFlywheel(0.0);
					}
					break;
			}
			return currentState;
		}
	}
}
