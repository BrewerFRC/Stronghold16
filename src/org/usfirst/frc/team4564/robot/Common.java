package org.usfirst.frc.team4564.robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Common {

	public static void debug(String a) {
		System.out.println(System.currentTimeMillis()+": " + a);
		
	}
	
	public static void dashStr(String a) {
		SmartDashboard.putString(": ",a);
		
	}
	
	public static void dashNum(int a) {
		SmartDashboard.putNumber(": ", a);
		
	}
}