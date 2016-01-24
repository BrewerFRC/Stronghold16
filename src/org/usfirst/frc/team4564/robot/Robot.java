
package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends SampleRobot {
	
	DriveTrain dt;
	Bat bat = new Bat();
	Xbox j = new Xbox(0);
	private double target = 0;
	
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
    	dt.getHeading().setHeadingHold(true);
    	while (isOperatorControl() && isEnabled()) {
    		Common.debug("setDrive");
    		dt.baseDrive(j.leftY(), j.leftX());
    		Common.dashNum("Sonar Distance", bat.getRightDistance());
    		Timer.delay(1.0 / Constants.REFRESH_RATE);
    		
    		}
    	}
    public void test() {
    	
    }
}
