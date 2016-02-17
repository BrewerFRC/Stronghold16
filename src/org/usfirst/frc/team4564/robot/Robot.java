package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Robot extends SampleRobot {
	Thrower thrower = new Thrower();
	DriveTrain dt;
	Bat bat = new Bat();
	public static Xbox j = new Xbox(0);
	public static NetworkTable table;
	Auto auto;
	TapeWinch w = new TapeWinch();
	ArmWinch arm = new ArmWinch();
	
    public void robotInit () {
    	Common.debug("Robot Init...");
    	table = NetworkTable.getTable("dashTable");
    	dt = new DriveTrain();
    	auto = new Auto (dt, bat);
    }
    
    public void autonomous() {
        auto.init();
		dt.setPIDDrive(true);
        //dt.driveDistance(2);
		dt.rotateTo(45);
        while(isAutonomous() && isEnabled()) {
        	//Loop delay timer
        	long time = Common.time();
    		delay = (long)(time + (1000/Constants.REFRESH_RATE));
        	dt.pidDrive();
        	//bat.update();
        	//auto.updateGate();
        	//Common.dashNum("currentState", auto.currentState);
        	//Common.dashNum("Sonar", bat.getDistance());
        	//Common.dashBool("IsComplete", dt.driveComplete());
    		
    		//Delay timer
    		double wait = (delay-Common.time())/1000.0;
    		if (wait < 0) {
    			wait = 0;
    		}
    		Timer.delay(wait);
        }
        dt.setPIDDrive(false);
    }
    
    public void operatorControl() {
    	Common.debug("Starting Teleop...");
    	dt.heading.reset();
    	dt.heading.setHeadingHold(false);
    	dt.init(); 
    	long delay = 0;
    	thrower.state.currentState = 0;
    	while (isOperatorControl() && isEnabled()) {
     		Common.dashNum("Flywheel encoder", thrower.encoder.get() );
    		//Calculate loop timer.
    		long time = Common.time();
    		delay = (long)(time + (1000/Constants.REFRESH_RATE));
    		
    		//Drivetrain  
    		dt.baseDrive(j.leftY(), j.leftX());
    		
    		//Thrower / Intake
    		if (j.whenA()) {
    			if (thrower.state.hasBall()) {
    				thrower.state.prepThrow();
    			} else {
        			thrower.state.startIntake();
    			}
    		}
    		if (j.rightTrigger() == 1.0) {
    			thrower.state.throwBall();
    		}
    		if (j.whenB()) {
    			thrower.state.ejectBall();
    		}
     		if (j.whenX()) {
     			thrower.state.togglePortcullis();
     		}
     		
     		thrower.state.update();
     		
     		//TapeWinch
        	Common.dashNum("Reflector Volatge", w.reflectorVoltage());
     		w.setWinchMotor(j.rightY());
     		if (j.whenSelect()) {
     			w.unlockWinch();
     		}
     		
     		//ArmWinch
     		if (j.dpadUp()) {
     			arm.moveUp();
     		} else if (j.dpadDown()) {
     			arm.moveDown();     			
     		} else {
     			arm.stopArm();
     		}
     		Common.dashNum("Distance", dt.encoder.getDistance());
     		Common.dashNum("Sonic", bat.getDistance());
    		/*
    		bat.update();
    		Common.dashNum("Sonar Distance", bat.getDistance());
    		Common.dashNum("Sonar Volts", bat.sonicRight.getVoltage());
			Common.dashBool("raw output from limit switch", w.winchLimit.get());
			Common.dashNum("raw output from infrared", w.infraRed.getVoltage());
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
    		Common.dashNum("Encoder", dt.encoder.get());*/
    		
    		Common.dashNum("Thrower State", thrower.state.currentState);
    		//Common.dashNum("Front Right motor value", DriveTrain.FrontR.get());
    		//Common.dashNum("Front Left motor value", DriveTrain.FrontL.get());
    		//Common.dashNum("Winch (tape) motor value", w.tapeMotor.get());
    		//Common.dashNum("Flywheel (thrower) motor value", thrower.getFlywheelPower());
    		//Common.dashNum("Intake motor value", thrower.getInternalIntakePower());
    		//Common.dashNum("Drivetrain Encoder value", dt.encoder.get());
    		//Common.dashNum("Gyro Angle", dt.heading.getAngle());
    		//Common.dashNum("Gyro Heading", dt.heading.getHeading());
    		//Common.dashNum("Sonar Distance", bat.getDistance());
			//Common.dashBool("Limit Switch Value", w.winchLimit.get());
			//Common.dashNum("Infrared Value", w.infraRed.getVoltage());
			//Common.dashNum("Raw Left joystick Y value", j.leftY());
			//Common.dashNum("Raw Left joystick X value", j.leftX());
			//Common.dashNum("Raw Right joystick Y value", j.rightY());
			//Common.dashNum("Raw Left trigger value", j.leftTrigger());
			//Common.dashNum("Raw Right trigger value", j.rightTrigger());
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
