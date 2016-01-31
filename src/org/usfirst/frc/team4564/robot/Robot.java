
package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends SampleRobot {
	
	DriveTrain dt;
	Bat bat = new Bat();
	Xbox j = new Xbox(0);
	Auto auto = new Auto (dt, bat);
	Winch w = new Winch();
    public Robot() {
    	Common.debug("New driveTrain");
    	dt = new DriveTrain();
    
    }
    
    public void robotInit () {
    	Common.debug("Robot Init...");
    }
    
    public void autonomous() {
        dt.setPIDDrive(true);
        dt.distancePID.setTarget(2);
        dt.setSafetyEnabled(false);
        auto.currentState = 0;
        while(isAutonomous() && isEnabled()) {
        	dt.pidDrive();
        	bat.update();
        	auto.updateAuto();
        	Common.dashNum("Sonar", bat.getDistance());
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
    		dt.baseDrive(j.leftY(), j.leftX());
    		bat.update();
    		Common.dashNum("Sonar Distance", bat.getDistance());
    		Common.dashNum("Sonar Volts", bat.sonicRight.getVoltage());
			Common.dashBool("restricted from moving down?", w.winchLimit.get());
			Common.dashNum("allowed to move up?", w.infraRed.getVoltage());
			Common.dashNum("right y output", j.leftY());
    		dt.setDrive(j.leftY(), j.leftX());
    		w.setWinchMotor(j.rightY());
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
    		Common.dashNum("GyroAngle", dt.heading.getAngle());
    		Common.dashNum("GyroHeading", dt.heading.getHeading());
    		Common.dashNum("TargetAngle", dt.heading.getTarget());
    		Common.dashBool("HeadingHold", dt.heading.isHeadingHold());
    		Common.dashNum("Error", dt.heading.getAngle()-dt.heading.getTarget());
    		double wait = (delay-Common.time())/1000.0;
    		Common.dashNum("Wait", wait);
    		Common.dashNum("Delay", delay);
    		if (wait < 0) {
    			wait = 0;
    		}
    		Timer.delay(wait);
    		Common.dashNum("DeltaTime", Common.time() - time);
    	}
    }
    
    public void test() {
    	
    }
}
