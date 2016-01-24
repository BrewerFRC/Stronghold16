
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
    		dt.baseDrive(j.leftY(), j.leftX());
    		
    		if (j.whenA()) {
    			Common.dashBool("A" , j.whenA());
    		}
    		
    		if (j.whenB()) {
    			Common.dashBool("B" , j.whenB());
    		}
    		
    		if (j.whenX()) {
    			Common.dashBool("X" , j.whenX());
    		}
    		
    		if (j.whenY()) {
    			Common.dashBool("Y" , j.whenY());
    		}
    		
    		if (j.whenRightBumper()) {
    			Common.dashBool("Right Bumper", j.whenRightBumper());
    		}
    		
    		if (j.whenLeftBumper()) {
    			Common.dashBool("Left Bumper", j.whenLeftBumper());
    		}
    		
    		if (j.whenSelect()) {
    			Common.dashBool("Select", j.whenSelect());
    		}
    		
    		if (j.whenStart()) {
    			Common.dashBool("Start", j.whenStart());
    		}
    		
    		if (j.whenLeftClick()) {
    			Common.dashBool("Left Click", j.whenLeftClick());
    		}
    		
    		if (j.whenRightClick()) {
    			Common.dashBool("Right Click", j.whenRightClick());
    		}
    		
    		if (j.whenDpadUp()) {
    			Common.dashBool("dpadUp", j.whenDpadUp());
    		}
    		
    		if (j.whenDpadDown()) {
    			Common.dashBool("dpadDown", j.whenDpadDown());
    		}
    		
    		if (j.whenDpadLeft()) {
    			Common.dashBool("dpadLeft", j.whenDpadLeft());
    		}
    		
    		if (j.whenDpadRight()) {
    			Common.dashBool("dpad right", j.whenDpadRight());
    		}
    		
    		if (j.rightTrigger() >= 0) {
    			Common.dashNum("Right Trigger", j.rightTrigger());
    		}
    		
    		if (j.leftTrigger() >= 0) {
    			Common.dashNum("Left Trigger", j.leftTrigger());
    		}
    		
    		
    		Timer.delay(1.0 / Constants.REFRESH_RATE);
    		
    		}
    	}
    
    public void test() {
    	
    }
}
