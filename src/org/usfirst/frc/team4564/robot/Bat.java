package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalOutput;
public class Bat {
	// 10 cycles per second update rate
	int cycleDelay = (int)Constants.REFRESH_RATE/10;
	int currentCycle = 0;
	//Constants
	AnalogInput sonicRight = new AnalogInput (Constants.ANA_SONIC_RIGHT);
	DigitalOutput sonicRightEnable = new DigitalOutput(Constants.DIO_LEFT_SONIC_ENABLE);
	private static final double CORRECTION = 1 / 1.04;
	private static final double VOLTS_PER_INCH = 5.0 / 1024 * 2.54 * CORRECTION; // Volts per inch constant
	private static final double OFFSET = 3.25;
	
	public double getDistance() {
		return sonicRight.getVoltage() / VOLTS_PER_INCH - OFFSET;
	}	
	
	public void update() {
		currentCycle++;
		if (currentCycle >= cycleDelay) {
			sonicRightEnable.set(true);
			sonicRightEnable.set(false);
			currentCycle = 0;
		}
	}
}
