package org.usfirst.frc.team4564.robot;
import java.util.Calendar;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Common {

	public static void debug(String a) {
		System.out.println(System.currentTimeMillis()+": " + a);
	}
	
	public static void dashStr(String title, String a) {
		SmartDashboard.putString(title + ": ",a);
	}
	
	public static void dashNum(String title, int a) {
		SmartDashboard.putNumber(title + ": ", a);
	}
	public static void dashNum(String title, double a) {
		SmartDashboard.putNumber(title + ": ", a);
	}
	
	public static long time() {
		return Calendar.getInstance().getTimeInMillis();
	}
}