package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Timer;

public class Bat {

	//Constants
	
	AnalogInput sonicRight = new AnalogInput (Constants.ANA_SONIC_RIGHT);
	DigitalOutput sonicRightEnable = new DigitalOutput(Constants.DIO_LEFT_SONIC_ENABLE);
	private static final double VOLTS_PER_INCH = 5.0 / 1024 * 2.54; // Volts per inch constant
	
	public double getRightDistance() {
		Common.debug("ACTIVATING BAT SONAR!!!!");
		return sonicRight.getVoltage() / VOLTS_PER_INCH / 12;
	}	
	
	public void update() {
	sonicRightEnable.set(false);
	getRightDistance();
	Timer.delay(1.0 / Constants.REFRESH_RATE);
	}
}
