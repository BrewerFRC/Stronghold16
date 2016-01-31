
package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Robot extends SampleRobot {
	
	DriveTrain dt;
	Bat bat = new Bat();
	Xbox j = new Xbox(0);
	NetworkTable table;
	
    public Robot() {
    	Common.debug("New driveTrain");
    	dt = new DriveTrain();
    
    }
    
    public void robotInit () {
    	Common.debug("Robot Init...");
    	table = NetworkTable.getTable("dashTable");
    	table.putNumber("gyroP", Constants.GYRO_P);
    	table.putNumber("gyroI", Constants.GYRO_I);
    	table.putNumber("gyroD", Constants.GYRO_D);
    }
    
    public void autonomous() {
        dt.setPIDDrive(true);
        dt.distancePID.setTarget(2);
        while(isAutonomous() && isEnabled()) {
        	dt.pidDrive();
        }
        dt.setPIDDrive(false);
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
    		dt.baseDrive(j.leftY(), j.leftX());
    		bat.update();
    		Common.dashNum("Sonar Distance", bat.getDistance(0));
    		Common.dashNum("Sonar Volts", bat.sonicRight.getVoltage());
    		dt.setDrive(j.leftY(), j.leftX());
    		if (j.whenA()) {
    			dt.heading.setHeadingHold(true);
    		}
    		if (j.whenB()) {
    			dt.heading.setHeadingHold(false);
    		}
    		if (j.whenDpadLeft()) {
    			dt.heading.setTarget(dt.heading.getTarget()-10);
    		}
    		if (j.whenDpadRight()) {
    			dt.heading.setTarget(dt.heading.getTarget()+10);
    		}
    		Constants.GYRO_P = table.getNumber("gyroP", 0);
    		Constants.GYRO_I = table.getNumber("gyroI", 0);
    		Constants.GYRO_D = table.getNumber("gyroD", 0);
    		Common.dashNum("Gyro P", Constants.GYRO_P);
    		Common.dashNum("Gyro I", Constants.GYRO_I);
    		Common.dashNum("Gyro D", Constants.GYRO_D);
    		Common.dashNum("GyroAngle", dt.heading.getAngle());
    		Common.dashNum("GyroHeading", dt.heading.getHeading());
    		Common.dashNum("TargetAngle", dt.heading.getTarget());
    		Common.dashBool("HeadingHold", dt.heading.isHeadingHold());
    		Common.dashNum("Error", dt.heading.getAngle()-dt.heading.getTarget());
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
