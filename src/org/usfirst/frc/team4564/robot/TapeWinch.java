package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Talon;
	
public class TapeWinch {

	public static final boolean RETRACT_LIMIT_REACHED = false;
	public static final boolean EXTEND_LIMIT_REACHED = false;
	public DigitalInput winchLimit = new DigitalInput(Constants.DIO_WINCH_RETRACT_LIMIT);
	public AnalogInput irReflector = new AnalogInput(Constants.ANA_WINCH_EXTEND_LIMIT);
	public Talon tapeMotor = new Talon(Constants.PWM_WINCH_DRIVE_TM);
	public Servo ratchet = new Servo(Constants.PWM_WINCH_LOCK);
	
		
		public double reflectorVoltage() {
			return irReflector.getVoltage();
		}

		public void setWinchMotor(double power){
			
			if (power > 0) {
				if (winchLimit.get() == RETRACT_LIMIT_REACHED) {
					power = 0;
				}
			}
			
			if (power < 0) {
				if (irReflector.getVoltage() > .3){ // 2 is estimate voltage.  higher voltage is absence of reflection
					power = 0;
				}

			}
			tapeMotor.set(power);
		}

		
		public void lockWinch() {
			ratchet.setAngle(90);		
		}
		
		public void unlockWinch() {
			ratchet.setAngle(-90);
		}
 	}

