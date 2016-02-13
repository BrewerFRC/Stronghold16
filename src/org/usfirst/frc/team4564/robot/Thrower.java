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
		//States for intake and throw.
		private static final int READY = 0; //Intake running slowly inward; flywheel off; ready for startIntake.
		private static final int INTAKE = 1; //Intake running full speed until ball detected; sets state to BALL_DETECT.
		private static final int BALL_DETECTED = 2; //Ball loaded; intake running slowly; awaiting prepThrow.
		private static final int BACK_OUT = 3; //prepThrow; move ball from flywheel before spin-up; set state to SPIN_UP.
		private static final int SPIN_UP = 4; //Bring flywheel up to speed; waiting 1 seconds.
		private static final int READY_TO_FIRE = 5; //Flywheel spinning at full speed; awaiting throwBall.
		private static final int FIRE = 6; //Intake fullspeed in for .25 seconds; sets state to READY.
		//States for eject.
		private static final int START_EJECT = 10; //Start ejectTimer; set state to EJECT.
		private static final int EJECT = 11; //Set intake backwards full speed unitl ejectTimer is complete; set currentStae to READY.
		
		private Thrower thrower;
		public int currentState;
		private long spinUpTimer;
		private long fireTimer;
		private long ejectTimer;
		
		public ThrowerState(Thrower thrower) {
			currentState = READY;
			this.thrower = thrower;
		}
		
		// Returns true when ball is pressing limit switch.
		public boolean hasBall() {
			return thrower.ballDetect.get(); 
		}
		
		public void startIntake() { 
			if (currentState < BACK_OUT) {
				currentState = INTAKE;
			}
		}
		
		public void prepThrow() {
			if (currentState == BALL_DETECTED) {
				currentState = BACK_OUT;
			}
		}
		
		public void throwBall() {
			if (currentState == READY_TO_FIRE) {
				currentState = FIRE;
			}
		}
		
		public void ejectBall() {
			currentState = START_EJECT;
		}
		
		//Positive speed runs intakes inward.
		private void setIntakeSpeed(double speed) {
			thrower.setInternalIntake(speed);
			thrower.setExternalIntake(speed);
		}
		
		//Positive speed runs flywheel in throwing direction.
		private void setFlywheelSpeed(double speed) {
			thrower.setFlywheel(speed);
		}
		
		
		public int update() {
			switch(currentState) {
				case READY:
					setFlywheelSpeed(0.0);
					setIntakeSpeed(.15);
					break;
				case INTAKE:
					if (hasBall()) {
						currentState = BALL_DETECTED;
					}
					setIntakeSpeed(1.0);
					break;
				case BALL_DETECTED:
					setIntakeSpeed(0.15);
					break;
				case BACK_OUT:
					setIntakeSpeed(-.15);
					if (hasBall() != true ) {
						setIntakeSpeed(0);
						currentState = SPIN_UP;
						spinUpTimer = Common.time() + 1000; //SpinUpTimer for 1 second;
					}
					break;
				case SPIN_UP:
					//TODO: Velocity control
					setFlywheelSpeed(1.0);
					if (Common.time() >= spinUpTimer) {
						currentState = READY_TO_FIRE;
					}
					break;
				case READY_TO_FIRE:
					setFlywheelSpeed(1.0);
					fireTimer = Common.time() + 250; //fireTimer set for .25 seconds.
					break;
				case FIRE:
					setIntakeSpeed(1.0);
					if (Common.time() >= fireTimer) {
						currentState = READY;
					}
					break;
				case START_EJECT:
					ejectTimer = Common.time() + 500; //ejectTimer set for .5 seconds.
					currentState = EJECT;
				case EJECT:
					setIntakeSpeed(-1.0);
					if (Common.time() >= ejectTimer) {
						currentState = READY;
					}
			}
			return currentState;
		}
	}
}
