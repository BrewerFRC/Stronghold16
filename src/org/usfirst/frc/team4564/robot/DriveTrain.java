package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class DriveTrain extends RobotDrive {
		
	//Drive Motors
	static Talon FrontR = new Talon(Constants.PWM_DRIVE_FR);
	static Talon FrontL = new Talon(Constants.PWM_DRIVE_FL);
	static Talon BackR = new Talon(Constants.PWM_DRIVE_RR);
	static Talon BackL = new Talon(Constants.PWM_DRIVE_RL);
	
	// Encoder Definitions
	private Encoder encoder_FR = new Encoder(Constants.DIO_DRIVE_FR_ENCODER_A, Constants.DIO_DRIVE_FR_ENCODER_B, 
			true, EncodingType.k1X);
	
	//Distance PID
	public PID distancePID = new PID(Constants.DRIVE_P, Constants.DRIVE_I, Constants.DRIVE_D, false);
	
	//Gyro definition
	public Heading heading = new Heading(Constants.ANA_GYRO, Constants.GYRO_P, Constants.GYRO_I, Constants.GYRO_D, Constants.GYRO_SENSITIVITY);
	
	//Accel Curve Speeds
	double driveSpeed = 0;
	double turnSpeed = 0;
	double driveAccel = 0;
	double turnAccel = 0;
	
	//DriveTrain constructor
	public DriveTrain() {
		super(FrontR, FrontL, BackR, BackL);
        setInvertedMotor(RobotDrive.MotorType.kFrontLeft,true);
        setInvertedMotor(RobotDrive.MotorType.kRearLeft,true);
        setInvertedMotor(RobotDrive.MotorType.kFrontRight,true);
        setInvertedMotor(RobotDrive.MotorType.kRearRight,true);
        
	}
	
	public void init() {
		
	}
	
	public void setPIDDrive(boolean pidDrive) {
		if (pidDrive) {
			distancePID.reset();
			heading.resetPID();
			heading.setHeadingHold(true);
		}
		else {
			heading.setHeadingHold(false);
		}
	}
	
	public void pidDrive() {
		double speed = distancePID.calc(encoder_FR.getDistance());
		double turn = heading.turnRate();
		setDrive(speed, turn);
	}
	
	 public double driveAccelCurve(double target, double driveAccel) {
		 if (Math.abs(driveSpeed - target) > driveAccel) {
	            if (driveSpeed > target) {
	                driveSpeed = driveSpeed - driveAccel;
	            } else {
	                driveSpeed = driveSpeed + driveAccel;
	            }
	        } else {
	            driveSpeed = target;
	        }
	        return driveSpeed;
	 }
	 
	 public double turnAccelCurve(double target, double turnAccel) {
		 if (Math.abs(turnSpeed - target) > turnAccel) {
	    		if (turnSpeed > target) {
	    			turnSpeed = turnSpeed - turnAccel;
	    		} else {
	    			turnSpeed = turnSpeed + turnAccel;
	    		}
	    	} else {
	    		turnSpeed = target;
	    	}
	    return turnSpeed;
		}
	
	public void setDrive(double drive, double turn) {
		if (heading.isHeadingHold()) {
			turn = heading.turnRate();
		}
		Common.dashNum("Turn", turn);
		System.out.println(turn);
		arcadeDrive(drive, turn);
	}

	public void baseDrive(double drive, double turn) {
		drive = driveAccelCurve(drive, driveAccel );
		turn = turnAccelCurve(turn, turnAccel);
		setDrive(drive, turn);
	}
}
