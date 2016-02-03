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
	private Encoder encoderFR = new Encoder(Constants.DIO_DRIVE_FR_ENCODER_A, Constants.DIO_DRIVE_FR_ENCODER_B, 
			true, EncodingType.k1X);
	
	//Distance PID
	public PID distancePID = new PID(Constants.DRIVE_P, Constants.DRIVE_I, Constants.DRIVE_D, false);
	
	//Gyro definition
	public Heading heading = new Heading(Constants.ANA_GYRO, Constants.GYRO_P, Constants.GYRO_I, Constants.GYRO_D, Constants.GYRO_SENSITIVITY);
	
	public ActionHandler actionHandler = new ActionHandler();
	
	//Accel Curve Speeds
	double driveSpeed = 0;
	double turnSpeed = 0;
	double driveAccel = 0;
	double turnAccel = 0;
	
	//DriveTrain constructor
	public DriveTrain() {
		super(FrontL, FrontR);
		//	setInvertedMotor(RobotDrive.MotorType.kFrontLeft,true);
		//	setInvertedMotor(RobotDrive.MotorType.kFrontLeft,true);
	//super(FrontL, FrontR, BackL, BackR);
       // setInvertedMotor(RobotDrive.MotorType.kFrontLeft,false);
        //setInvertedMotor(RobotDrive.MotorType.kRearLeft,false);
      //  setInvertedMotor(RobotDrive.MotorType.kFrontRight,false);
        //setInvertedMotor(RobotDrive.MotorType.kRearRight,false);
        
	}
	
	public void init() {
		
	}
	
	public void rotateTo(double heading) {
		this.heading.setHeading(heading);
		actionHandler.setTargetReachedFunction(
			() -> Math.abs(this.heading.getTarget() - this.heading.getAngle()) <= 2
		);
	}
	
	public void relTurn(double heading) {
		this.heading.setTarget(this.heading.getTarget() + heading);
		actionHandler.setTargetReachedFunction(
			() -> Math.abs(this.heading.getTarget() - this.heading.getAngle()) <= 2
		);
	}
	
	public void driveDistance(double inches) {
		this.distancePID.setTarget(this.distancePID.getTarget() + inches);
		actionHandler.setTargetReachedFunction(
			() -> Math.abs(this.distancePID.getTarget() - this.encoderFR.getDistance()) <= 2
		);
	}
	
	public boolean driveComplete() {
		return this.actionHandler.isComplete();
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
		double speed = distancePID.calc(encoderFR.getDistance());
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
		Common.dashNum("Drive", drive);
		arcadeDrive(-drive, -turn);
	}

	public void baseDrive(double drive, double turn) {
		drive = driveAccelCurve(drive, driveAccel );
		turn = turnAccelCurve(turn, turnAccel);
		setDrive(drive, turn);
	}

}
