package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.CameraServer;
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
	ArmWinch arm = new ArmWinch();
	TapeWinch tape = new TapeWinch(arm);
	String teamName = "Orange Chaos";
	String blank = "blank";
	
    public void robotInit () {
    	table = NetworkTable.getTable("dashTable");
    	dt = new DriveTrain();
    	auto = new Auto (dt, bat, arm, thrower);
    	//Enable USB camera
    	CameraServer server = CameraServer.getInstance();
    	server.setQuality(50);
    	server.startAutomaticCapture("cam1");
    }
    
    public void disabled() {
    	while (isDisabled()) {
    		smartDebug();
    		Timer.delay(0.02);
    	}
    }
    
    public void autonomous() {
    	Common.debug("Starting Auto...");
        auto.init();
        while (isAutonomous() && isEnabled()) {
           	//Loop delay timer
        	long time = Common.time();
    		long delay = (long)(time + (1000/Constants.REFRESH_RATE));
    		
    		//Perform autorun
        	auto.autoRun();

        	smartDebug();
    		
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
    	dt.init(); 
    	dt.setHeadingHold(false);
    	thrower.state.init();
    	boolean driveToggle = false;  //When true, robot will drive from backwards orientation
    	boolean climbDrive = false;  //When true, robot will move forward at predetermined speed for climbing.
    	//Setup robot loop timer
    	long delay = 0;
    	while (isOperatorControl() && isEnabled()) {
    		//Calculate loop timer.
    		long time = Common.time();
    		delay = (long)(time + (1000/Constants.REFRESH_RATE));
    		
    		//Drivetrain
    		if (j.whenLeftClick()) {  //Toggle drive direction
    			driveToggle = !driveToggle;
    		}
     		if (j.dpadUp()) {
     			dt.baseDrive(.35, 0);
     		} else {
	    		if (driveToggle) { // Drive backwards
	    			dt.baseDrive(j.leftY(), j.leftX());
	    		}
	    		else {  //Drive forwards
	    			dt.baseDrive(-j.leftY(), j.leftX());
	    		}
     		}
    		//Thrower / Intake
    		if (j.whenA()) {
    			if (thrower.state.hasBall()) {  //If we have the ball then prep to throw
    				thrower.state.prepThrow();
    			} else {
        			thrower.state.startIntake(); //Otherwise set to intake ball
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
     		//Comment
     		//TapeWinch
     		tape.setWinchMotor(j.rightY());
     		if (j.whenSelect()) {
     			tape.unlockWinch();
     		}
     		if (j.whenRightClick()) {
     			tape.lockWinch();
     		}
     		//ArmWinch
     		if (j.rightBumper()) {
     			arm.moveUp();
     		}
     		if (j.leftBumper()) {
     			arm.moveDown();    
     		}
     		arm.update();
        	
     		smartDebug();
     		
        	//Loop wait
    		double wait = (delay-Common.time())/1000.0;
    		if (wait < 0) {
    			wait = 0;
    		}
    		Timer.delay(wait);
    	}
    }
    
    public void test() {
    	
    }
    
    public void smartDebug() {
    	//Drive Train
    	Common.dashNum("Drive Encoder", dt.encoder.getDistance());
    	
    	//Heading
    	Common.dashNum("Angle", dt.heading.getAngle());
    	Common.dashNum("Heading", dt.heading.getHeading());
    	Common.dashNum("Target Angle", dt.heading.getTargetAngle());
    	Common.dashNum("Target Heading", dt.heading.getTargetHeading());
    	
    	//Thrower
    	Common.dashNum("Thrower State", thrower.state.getCurrentState());
    	
    	//Arm Winch
    	Common.dashBool("Slow ArmWinch", arm.getSlowArm());
    	Common.dashNum("Arm Potentiometer", arm.getPotentiometerVoltage());
    	Common.dashNum("Arm Pot Target", arm.target);
    	
    	//Tape Winch
    	Common.dashNum("Tape IR Voltage", tape.reflectorVoltage());
    	Common.dashBool("Tape Locked", tape.lock);
    	
    	//Autonomous
    	Common.dashNum("Starting Platform", auto.paramStartingPlatform);
    	Common.dashNum("Target Platform", auto.paramTargetPlatform);
    	Common.dashNum("Defense Type", auto.paramDefenseType);
    	Common.dashNum("Selected Action", auto.paramSelectedAction);
    	Common.dashNum("Auto Sheild State", auto.shieldState);
    }
}
