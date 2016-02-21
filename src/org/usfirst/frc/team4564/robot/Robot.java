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
	String teamName = "Orange Chaos";
	String blank = "blank";
	
    public void robotInit () {
    	table = NetworkTable.getTable("dashTable");
    	dt = new DriveTrain();
    	auto = new Auto (dt, bat, arm, thrower);
    }
    
    public void disabled() {
    	ArmWinch.setSlowArm(false);
    }
    
    public void autonomous() {
    	Common.debug("Starting Auto...");
        auto.init();
        while(isAutonomous() && isEnabled()) {
           	//Loop delay timer
        	long time = Common.time();
    		long delay = (long)(time + (1000/Constants.REFRESH_RATE));
        	//if (auto.shieldCrossed()) {
        	//	dt.setDriveSpeed(0);
        	//} else { 
        	//	dt.setDriveSpeed(0.5);
        	//}
    		//auto.driveDefense(1);
        	auto.autoRun();
        	dt.autoDrive();
        	/*boolean complete = dt.driveComplete();
        	Common.dashBool("DriveComplete", complete);
        	if (complete && !driven) {
        		System.out.println("Drive");
        		dt.driveDistance(20);
        		driven = true;
        	}
        	*/

        	bat.update();
    		//Common.dashNum("GyroAngle", dt.heading.getAngle());
     		Common.dashNum("Sonic", bat.getDistance());
	   		//Common.dashNum("TargetAngle", dt.heading.getTargetAngle());
        	Common.dashNum("Shield Crossed State", auto.shieldState);
        	Common.dashStr("Name", teamName);
        	//Common.dashNum("Last Shield Distance", auto.shieldDistance);
        	//Common.dashNum("Sonar", bat.getDistance());

    		
    		//Delay timer
    		double wait = (delay-Common.time())/1000.0;
    		if (wait < 0) {
    			wait = 0;
    		}
    		Timer.delay(wait);
        }
        dt.setHeadingHold(false);
    }
    
    public void operatorControl() {
    	Common.debug("Starting Teleop...");
    	//dt.heading.reset();
    	//dt.heading.setHeadingHold(false);
    	dt.init(); 
    	dt.setHeadingHold(false);
    	long delay = 0;
    	thrower.state.currentState = 0;
    	while (isOperatorControl() && isEnabled()) {
    		//Calculate loop timer.
    		long time = Common.time();
    		delay = (long)(time + (1000/Constants.REFRESH_RATE));
    		
    		//Drivetrain  
    		dt.baseDrive(-j.leftY(), j.leftX());
    		
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
     		w.setWinchMotor(j.rightY());
     		if (j.whenSelect()) {
     			w.unlockWinch();
     		}
     		if (j.start()) {
     			w.lockWinch();
     		}
     		
     		//ArmWinch

     		if (j.rightBumper()) {
     			arm.moveUp();
     		}
     		if (j.leftBumper()) {
     			arm.moveDown();    
     		}

     		arm.update();
     		Common.dashNum("Sonic", bat.getDistance());
     		Common.dashBool("Servo Lock:   ", w.lock);
     		Common.dashNum("Shield State", auto.shieldState);
        	Common.dashStr("Name", teamName);
        	Common.dashStr("blank",blank);
     		//Common.dashNum("Potentiometer values", arm.getPotentiometerPosition());
     		//Common.dashNum("Potentiometer Target", arm.target);
     		//Common.dashNum("Reflector Volatge", w.reflectorVoltage());
    		/*
    		bat.update();
    		//Common.dashNum("Distance", dt.encoder.getDistance());
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
    		Common.dashNum("GyroHeading", dt.heading.getHeading());
    		Common.dashBool("HeadingHold", dt.heading.isHeadingHold());
    		Common.dashNum("Error", dt.heading.getAngle()-dt.heading.getTarget());
    		Common.dashNum("Encoder", dt.encoder.get());*/
     		//Common.dashNum("Flywheel encoder", thrower.encoder.get() );
    		//Common.dashNum("Thrower State", thrower.state.currentState);
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
