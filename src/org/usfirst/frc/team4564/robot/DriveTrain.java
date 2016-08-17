package org.usfirst.frc.team4564.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class DriveTrain extends RobotDrive {
	
	//Drive Motors
	private static Talon FrontL = new Talon(Constants.PWM_DRIVE_L);
	private static Talon FrontR = new Talon(Constants.PWM_DRIVE_R);
	
	// Encoder Definitions
	public Encoder encoder = new Encoder(Constants.DIO_DRIVE_FR_ENCODER_A, Constants.DIO_DRIVE_FR_ENCODER_B, 
			false, EncodingType.k1X);
	
	//Distance PID
	public PID distancePID = new PID(Constants.DRIVE_P, Constants.DRIVE_I, Constants.DRIVE_D, false, "distance");
	
	//Gyro definition
	public Heading heading = new Heading(Constants.GYRO_P, Constants.GYRO_I, Constants.GYRO_D, Constants.GYRO_SENSITIVITY);
	
	//Object to handle tracking and controlling current autonomous actions.
	public ActionHandler actionHandler = new ActionHandler();
	
	//Accel Curve Speeds
	double driveSpeed = 0;
	double turnSpeed = 0;
	double driveAccel = .05;
	double turnAccel = 1;
	
	//Auto drive variables
	private boolean driveByPID = false;	  //True if distance based driving enabled, false is driving by set motor power
	private double autoDriveSpeed = 0.0;  //Motor power for non-PID driving
	private double autoTurnSpeed = 0.0;  //Motor power for non-PID turning
	
	
	//DriveTrain constructor
	public DriveTrain() {
		super(FrontR, FrontL);
		init();
	}
	
	// Initialize drivetrain systems
	public void init() {
		encoder.setDistancePerPulse(1.0/Constants.COUNTS_PER_INCH);
		encoder.reset();
		heading.reset();
		distancePID.setMin(-0.60);
		distancePID.setMax(0.60);
		actionHandler = new ActionHandler();
		//setDriveSpeed(0);
		//baseDrive(0, 0);
	}
	
	// Set a target heading for robot to rotate to.
	// Direction of rotation will be shortest path from current heading.
	// Call setPIDDrive(true) to enable and call PIDDrive() from a robot loop to update.
	// Call driveComplete() to determine if turn is completed.
	public boolean rotateTo(double heading) {
		Common.debug("rotateTo: Setting heading to "+ heading);
		Common.debug("rotateTo: driveComplete" + driveComplete());
		distancePID.setTarget(encoder.getDistance());  //Force distancePID to current target current encoder distance
		if (driveComplete()) {
			autoTurnSpeed = 0.0;	//Disable non-PID turn speed
			this.heading.setHeading(heading);
			actionHandler.setTargetReachedFunction(
				() -> {
					boolean complete = Math.abs(this.heading.getTargetAngle() - this.heading.getAngle()) <= 1;
					if (!complete) {
						actionHandler.setTurning(true);
					}
					else if (actionHandler.isTurning()) {
						actionHandler.setTurning(false);
						distancePID.setTarget(encoder.getDistance());
					}
					return complete;
				}
			);
			return true;
		}
		return false;
	}
	
	public boolean relTurn(double degrees) {
		distancePID.setTarget(encoder.getDistance());
		if (driveComplete()) {
			autoTurnSpeed = 0.0;	//Disable non-PID turn speed
			this.heading.relTurn(degrees);
			actionHandler.setTargetReachedFunction(
				() -> {
					boolean complete = Math.abs(this.heading.getTargetAngle() - this.heading.getAngle()) <= 1;
					if (!complete) {
						actionHandler.setTurning(true);
					}
					else if (actionHandler.isTurning()) {
						actionHandler.setTurning(false);
						distancePID.setTarget(encoder.getDistance());
					}
					return complete;
				}
			);
			
			return true;
		}
		return false;
	}
	
	public boolean driveDistance(double inches) {
		distancePID.setTarget(encoder.getDistance());  //Force distancePID to start relative to current encoder distance
		if (driveComplete()) {
			driveByPID = true;     //Note that we are performing distance drive
			autoDriveSpeed = 0.0;  //Disable non-PID drive speed
			this.distancePID.setTarget(this.encoder.getDistance() + inches);
			actionHandler.setTargetReachedFunction(
				() -> {
					return Math.abs(this.distancePID.getTarget() - this.encoder.getDistance()) <= 2;
				}
			);
			return true;
		}
		return false;
	}
	
	// Has last requested PID drive action completed?
	public boolean driveComplete() {
		return this.actionHandler.isComplete();
	}
	
	// Enable or disable heading when using pidDrive.
	public void setHeadingHold(boolean headingHold) {
		if (headingHold) {
			//Clear any residual PID accumulators
			distancePID.reset();
			heading.resetPID();
			// Turn on heading hold
			heading.setHeadingHold(true);
		}
		else {
			heading.setHeadingHold(false);
		}
	}
	
	// Set a non-PID based driving speed
	public void setDriveSpeed(double speed) {
		driveByPID = false;
		autoDriveSpeed = speed;
	}
	
	// Set a non-PID based turning speed
	public void setTurnSpeed(double speed) {
		setHeadingHold(false);
		autoTurnSpeed = speed;
	}
	
	// Process both distance and heading PIDs and then power robot drive accordingly.
	// Call this method repeatedly from the robot loop.
	public void autoDrive() {
		double speed;
		// Determine driving speed
		if  (actionHandler.isTurning()) {	// If doing a PID Turn, set speed to zero 
			speed = 0.0;
		} else if (driveByPID) {  // Distance drive is enabled, so calculate speed based on distance PID
			distancePID.update();
			speed = -distancePID.calc(encoder.getDistance());
			// Scale the speed to overcome motor deadzone
			if (speed >= .01) {
				speed = speed + 0.15;
			} else if (speed  <= .01) {
				speed = speed - 0.15;
			}
		} else {  // Otherwise, drive based on the set motor drive speed
			speed = autoDriveSpeed;
		}
		// Determine turn rate
		double turn;
		if (heading.isHeadingHold()) {
			heading.update();
			turn = heading.turnRate();
		} else {
			turn = autoTurnSpeed;
		}
		// Drive the robot
		baseDrive(speed, turn);

		//Common.dashNum("DrivePID", speed);
		//Common.dashNum("DriveTarget", this.distancePID.getTarget());
		//Common.dashNum("DriveError", this.distancePID.getTarget() - this.encoder.getDistance());
		//Common.dashNum("TurnPID", turn);
		//Common.dashNum("Gyro Current Angle", heading.getAngle());
		//Common.dashNum("Gyro Target Angle", heading.getTargetAngle());
		//Common.dashNum("Gyro Current Heading", heading.getHeading());
		//Common.dashNum("Gyro Target Heading", heading.getTargetHeading());
		//Common.dashNum("TurnError", heading.getTargetAngle() - heading.getAngle());
	}
	
	//target = target speed (desired speed), driveSpeed = current speed
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
	
	// Set drive motors.  
	private void setDrive(double drive, double turn) {
		Common.dashNum("dt.setDrive() Power", drive);
		Common.dashNum("dt.setDrive() Turn", turn);
		arcadeDrive(-drive, -turn);
	}

	// Drive the robot using acceleration curves
	public void baseDrive(double drive, double turn) {
		drive = driveAccelCurve(drive, driveAccel );
		turn = turnAccelCurve(turn, turnAccel);
		setDrive(drive, turn);
	}

}
