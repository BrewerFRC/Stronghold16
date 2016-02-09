
package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends SampleRobot {
	Thrower thrower = new Thrower();
	DriveTrain dt;
	Bat bat = new Bat();
	Xbox j = new Xbox(0);
	NetworkTable table;
	Auto auto;
	Winch w = new Winch();
    public Robot() {
    	Common.debug("New driveTrain");
    	dt = new DriveTrain();
    	auto = new Auto (dt, bat);
    }
    
    public void robotInit () {
    	Common.debug("Robot Init...");
    	table = NetworkTable.getTable("dashTable");
    	table.putNumber("gyroP", Constants.GYRO_P);
    	table.putNumber("gyroI", Constants.GYRO_I);
    	table.putNumber("gyroD", Constants.GYRO_D);
    	table.putNumber("driveP", Constants.DRIVE_P);
    	table.putNumber("driveI", Constants.DRIVE_I);
    	table.putNumber("driveD", Constants.DRIVE_D);
    }
    
    public void autonomous() {
        dt.setSafetyEnabled(false);
        //auto.currentState = 0;
        long delay = 0;
        while(isAutonomous() && isEnabled()) {
        	long time = Common.time();
    		delay = (long)(time + (1000/Constants.REFRESH_RATE));
        	dt.pidDrive();
        	if (j.whenA()) {
        		dt.setPIDDrive(true);
        		dt.driveDistance(6);
        	}
        	if (j.whenB()) {
        		dt.setPIDDrive(false);
        	}
        	//bat.update();
        	//auto.updateGate();
        	Common.dashNum("currentState", auto.currentState);
        	Common.dashNum("Sonar", bat.getDistance());
        	Common.dashBool("IsComplete", dt.driveComplete());
        	dt.heading.setPID(table.getNumber("gyroP", 0), table.getNumber("gyroI", 0), table.getNumber("gyroD", 0));
    		dt.distancePID.setP(table.getNumber("distanceP", 0));
    		dt.distancePID.setI(table.getNumber("distanceI", 0));
    		dt.distancePID.setD(table.getNumber("distanceD", 0));
    		
    		double wait = (delay-Common.time())/1000.0;
    		if (wait < 0) {
    			wait = 0;
    		}
    		Timer.delay(wait);
        }
        dt.setPIDDrive(false);
        Common.dashNum("Sonar", bat.getDistance());
    }
    
    public void operatorControl() {
    	Common.debug("Starting Teleop...");
    	dt.heading.reset();
    	dt.heading.setTarget(0);
    	long delay = 0;
    	while (isOperatorControl() && isEnabled()) {
    		Common.debug("setDrive");
    		long time = Common.time();
    		delay = (long)(time + (1000/Constants.REFRESH_RATE));/*
    		dt.setDrive(j.leftY(), j.leftX());
    		bat.update();
    		Common.dashNum("Sonar Distance", bat.getDistance());
    		Common.dashNum("Sonar Volts", bat.sonicRight.getVoltage());
			Common.dashBool("raw output from limit switch", w.winchLimit.get());
			Common.dashNum("raw output from infrared", w.infraRed.getVoltage());
			Common.dashNum("left y output", j.leftY());
    		dt.setDrive(j.leftY(), j.leftX());
    		w.setWinchMotor(j.rightY());
    		if (j.whenY()) {
    			dt.heading.setHeadingHold(true);
    		}
    		if (j.whenX()) {
    			dt.heading.setHeadingHold(false);
    		}
    		if (j.whenDpadLeft()) {
    			dt.heading.setTarget(dt.heading.getTarget()-10);
    		}
    		if (j.whenDpadRight()) {
    			dt.heading.setTarget(dt.heading.getTarget()+10);
    		}
    		
    		thrower.setFlywheel(j.leftTrigger());
    		if (j.B()){	// pulls ball back as long as held down
    			thrower.setInternalIntake(-0.25);
    		}
    		if (j.A()){ //throw into shooter & pick up ball
    			thrower.setInternalIntake(0.85);
    		}
    		if (j.whenStart()){ //reset
    			thrower.setInternalIntake(-0.85);
    		}
    		
    		
    		dt.heading.setPID(table.getNumber("gyroP", 0), table.getNumber("gyroI", 0), table.getNumber("gyroD", 0));
    		dt.distancePID.setP(table.getNumber("distanceP", 0));
    		dt.distancePID.setI(table.getNumber("distanceI", 0));
    		dt.distancePID.setD(table.getNumber("distanceD", 0));
    		Common.dashNum("Gyro P", Constants.GYRO_P);
    		Common.dashNum("Gyro I", Constants.GYRO_I);
    		Common.dashNum("Gyro D", Constants.GYRO_D);
    		Common.dashNum("GyroAngle", dt.heading.getAngle());
    		Common.dashNum("GyroHeading", dt.heading.getHeading());
    		Common.dashNum("TargetAngle", dt.heading.getTarget());
    		Common.dashBool("HeadingHold", dt.heading.isHeadingHold());
    		Common.dashNum("Error", dt.heading.getAngle()-dt.heading.getTarget());
    		Common.dashNum("Encoder", dt.encoder.get());*/
    		
    		
    		////TEST////
    		dt.setDrive(j.leftY(), j.leftX());
    		bat.update();
    		w.setWinchMotor(j.rightY());
    		thrower.setFlywheel(j.leftTrigger()); //set flywheel to trigger
    		thrower.setInternalIntake(j.rightTrigger()); //set intake to trigger
    		if (j.select()) {
    			thrower.setFlywheel(-1 * j.leftTrigger()); //set flywheel to negative of trigger value
    			thrower.setInternalIntake(-1 * j.rightTrigger()); //set intake to negative of trigger value
    		}
    		
    		if (j.whenA()) { //set flywheel to shooting speed
    			thrower.setFlywheel(.85);
    		}
    		
    		if (j.whenB()) { //set intake to pickup speed and throw into shooter speed
    			thrower.setInternalIntake(.25);
    		}
    		
    		if (j.X()) { //set intake to throwout speed
    			thrower.setInternalIntake(-.85);
    			}
    		
    		if (j.whenY()) //reset
    			thrower.setInternalIntake(0);
    			thrower.setFlywheel(0);
    		
    		Common.dashNum("Front Right motor value", DriveTrain.FrontR.get());
    		Common.dashNum("Front Left motor value", DriveTrain.FrontL.get());
    		Common.dashNum("Winch (tape) motor value", w.tapeMotor.get());
    		Common.dashNum("Flywheel (thrower) motor value", Thrower.flywheel.get());
    		Common.dashNum("Intake motor value", Thrower.internalIntake.get());
    		Common.dashNum("Drivetrain Encoder value", dt.encoder.get());
    		Common.dashNum("Gyro Angle", dt.heading.getAngle());
    		Common.dashNum("Gyro Heading", dt.heading.getHeading());
    		Common.dashNum("Sonar Distance", bat.getDistance());
			Common.dashBool("Limit Switch Value", w.winchLimit.get());
			Common.dashNum("Infrared Value", w.infraRed.getVoltage());
			Common.dashNum("Raw Left joystick Y value", j.leftY());
			Common.dashNum("Raw Left joystick X value", j.leftX());
			Common.dashNum("Raw Right joystick Y value", j.rightY());
			Common.dashNum("Raw Left trigger value", j.leftTrigger());
			Common.dashNum("Raw Right trigger value", j.rightTrigger());
			////END OF TEST////
    		
    	
    		double wait = (delay-Common.time())/1000.0;
    		if (wait < 0) {
    			wait = 0;
    		}
    		Timer.delay(wait);
    	}
    }
    
    public void test() {
    	
    }
}
