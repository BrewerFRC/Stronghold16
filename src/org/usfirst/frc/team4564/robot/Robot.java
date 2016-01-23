
package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends SampleRobot {
	
	DriveTrain dt;
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
    	while (isOperatorControl() && isEnabled()) {
    		Common.debug("setDrive");
    		dt.setDrive(j.leftY(), j.leftX());
    			
    		Timer.delay(1.0 / Constants.REFRESH_RATE);
    		
    		}
    	}
    
    public void test() {
    	
    }
}
