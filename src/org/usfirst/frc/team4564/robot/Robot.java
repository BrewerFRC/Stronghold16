
package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Robot extends SampleRobot {
	Thrower thrower = new Thrower();
	DriveTrain dt;
	Bat bat = new Bat();
	public static Xbox j = new Xbox(0);
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
    	auto.shield.startingPlatform = (int) table.getNumber("platform", 0);
    	auto.shield.targetPlatform = (int) table.getNumber("targetPlatform", 0);
    	auto.shield.defenseType = (int) table.getNumber("defense", 0);
    	auto.shield.selectedAction = (int) table.getNumber("action", 0);
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
    		//Common.debug("setDrive");
    		long time = Common.time();
    		delay = (long)(time + (1000/Constants.REFRESH_RATE));
    		dt.setDrive(j.leftY(), j.leftX());
    		thrower.state.update();
    		bat.update();
    		Common.dashNum("Sonar Distance", bat.getDistance());
    		Common.dashNum("Sonar Volts", bat.sonicRight.getVoltage());
			Common.dashBool("restricted from moving down?", w.winchLimit.get());
			Common.dashNum("allowed to move up?", w.infraRed.getVoltage());
			Common.dashNum("left y output", j.leftY());
			Common.dashNum("Flywheel encoder", thrower.encoder.get() );
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
    		Common.dashNum("Encoder", dt.encoder.get());
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
