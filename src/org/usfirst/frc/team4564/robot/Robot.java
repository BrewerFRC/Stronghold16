
package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends SampleRobot {
	
	DriveTrain dt;
	Bat bat = new Bat();
	Xbox j = new Xbox(0);
	
    public Robot() {
    	Common.debug("New driveTrain");
    	dt = new DriveTrain();
    
    }
    
    public void robotInit () {
    	Common.debug("Robot Init...");
    }
    
    public void autonomous() {
        
    }
    
    public void operatorControl() {
    	Common.debug("Starting Teleop...");
    	dt.heading.reset();
    	dt.heading.setTarget(0);
    	while (isOperatorControl() && isEnabled()) {
    		Common.debug("setDrive");
    		dt.baseDrive(j.leftY(), j.leftX());
    		Common.dashNum("Sonar Distance", bat.getRightDistance());
    		dt.setDrive(j.leftY(), j.leftX());
    		Timer.delay(1.0 / Constants.REFRESH_RATE);
    		if (j.whenA()) {
    			dt.heading.setHeadingHold(true);
    		}
    		if (j.whenB()) {
    			dt.heading.setHeadingHold(false);
    		}
    		if (j.whenDpadLeft()) {
    			dt.heading.setTarget(dt.heading.getTarget()-20);
    		}
    		if (j.whenDpadRight()) {
    			dt.heading.setTarget(dt.heading.getTarget()+20);
    		}
    		Common.dashNum("GyroAngle", dt.heading.getAngle());
    		Common.dashNum("GyroHeading", dt.heading.getHeading());
    		Common.dashNum("TargetAngle", dt.heading.getTarget());
    		Common.dashBool("HeadingHold", dt.heading.isHeadingHold());
    		Common.dashNum("Error", dt.heading.getAngle()-dt.heading.getTarget());
    	}
    }
    
    public void test() {
    	
    }
}
