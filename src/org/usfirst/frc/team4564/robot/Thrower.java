package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class Thrower {
	
	static Talon flywheel = new Talon(Constants.PWM_THROWER_FLYWHEEL);
	static Talon internalIntake = new Talon(Constants.PWM_THROWER_INT_INTAKE);
	
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
	
	
	
}
