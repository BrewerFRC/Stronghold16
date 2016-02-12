package org.usfirst.frc.team4564.robot;

public class Constants {

	// PWM Constants
	public static final int PWM_DRIVE_L = 0;
	public static final int PWM_DRIVE_R = 1;
	public static final int PWM_WINCH_ARM = 4;

	public static final int PWM_THROWER_FLYWHEEL = 5;
	public static final int PWM_THROWER_INT_INTAKE = 6;
	public static final int PWM_THROWER_EXT_INTAKE = 7;
	public static final int PWM_WINCH_DRIVE_TM = 8;
	public static final int PWM_WINCH_LOCK = 2;
	
	//DIO Port
	public static final int DIO_DRIVE_FR_ENCODER_A = 1;
	public static final int DIO_DRIVE_FR_ENCODER_B = 2;
	public static final int DIO_LEFT_SONIC_ENABLE = 0;
	public static final int DIO_WINCH_EXTEND_LIMIT = 3;
	public static final int DIO_WINCH_RETRACT_LIMIT = 4;
	public static final int DIO_FLYWHEEL_ENCODER_A = 5;
	public static final int DIO_FLYWHEEL_ENCODER_B = 6;
	public static final int DIO_THROWER_BALL_DETECT = 7;
	
	//Analog Ports
	//public static final int ANA_GYRO = 0;
	public static final int ANA_SONIC_RIGHT = 0 ;
	public static final int ANA_GYRO = 1;
	public static final int ANA_WINCH_EXTEND_LIMIT = 2;
	
	//Drive PID constants
	public static final double DRIVE_P = 0.0;
	public static final double DRIVE_I = 0.0;
	public static final double DRIVE_D = 0.0;
	public static final double COUNTS_PER_INCH = 83.39;
	
	//Gyro constants
	public static double GYRO_P = 0.08;
	public static double GYRO_I = 0.0001;
	public static double GYRO_D = 0.003;
	public static final double GYRO_SENSITIVITY = 0.00669;
	
	
	//Robot loop speed
	public static final double REFRESH_RATE = 50;
	
	//Sonic loop speed
	public static final int SONIC_REFRESH_RATE = 10;
	


}
