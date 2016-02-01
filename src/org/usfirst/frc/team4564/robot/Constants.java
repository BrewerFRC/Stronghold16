package org.usfirst.frc.team4564.robot;

public class Constants {

	// PWM Constants
	public static final int PWM_DRIVE_FR = 0;
	public static final int PWM_DRIVE_FL = 2;
	public static final int PWM_DRIVE_RR = 1;
	public static final int PWM_DRIVE_RL = 3;
	public static final int PWM_WINCH_DRIVE_TM = 5;
	
	//DIO Port
	public static final int DIO_DRIVE_FR_ENCODER_A = 1;
	public static final int DIO_DRIVE_FR_ENCODER_B = 2;
	public static final int DIO_LEFT_SONIC_ENABLE = 0;
	public static final int DIO_WINCH_EXTEND_LIMIT = 3;
	public static final int DIO_WINCH_RETRACT_LIMIT = 4;
	
	//Analog Ports
	//public static final int ANA_GYRO = 0;
	public static final int ANA_SONIC_RIGHT = 0 ;
	public static final int ANA_GYRO = 1;
	
	//Drive PID constants
	public static final double DRIVE_P = 0.0;
	public static final double DRIVE_I = 0.0;
	public static final double DRIVE_D = 0.0;
	
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
