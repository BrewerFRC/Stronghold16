package org.usfirst.frc.team4564.robot;

public class Constants {

	// PWM Constants
	public static final int PWM_DRIVE_L = 0;
	public static final int PWM_DRIVE_R = 1;
	public static final int PWM_WINCH_ARM = 4;
	public static final int PWM_THROWER_FLYWHEEL = 5;
	public static final int PWM_THROWER_INT_INTAKE = 6;
	public static final int PWM_THROWER_EXT_INTAKE = 7;
	public static final int PWM_TAPE_WINCH = 8;
	public static final int PWM_TAPE_WINCH_LOCK = 2;
	
	//DIO Port
	//public static final int DIO_LEFT_SONIC_ENABLE = 0;
	public static final int DIO_DRIVE_FR_ENCODER_A = 1;
	public static final int DIO_DRIVE_FR_ENCODER_B = 2;
	public static final int DIO_ARM_WINCH_LOW_LIMIT = 0;

	public static final int DIO_ARM_WINCH_HIGH_LIMIT = 4;
	public static final int DIO_FLYWHEEL_ENCODER_A = 5;
	public static final int DIO_FLYWHEEL_ENCODER_B = 6;
	public static final int DIO_THROWER_BALL_DETECT = 7;
	public static final int DIO_TAPE_WINCH_RETRACT_LIMIT = 8;
	
	//Analog Ports
	//public static final int ANA_GYRO = 0;
	public static final int ANA_SONIC_RIGHT = 0;
	public static final int ANA_ARM_WINCH_POT = 1;
	public static final int ANA_TAPE_WINCH_EXTEND_LIMIT = 2;
	
	//Drive PID constants
	public static final double DRIVE_P = 0.05;
	public static final double DRIVE_I = 0.0;
	public static final double DRIVE_D = -0.05;
	public static final double COUNTS_PER_INCH = 88.95; //83.39;
	
	//Turn PID constants
	public static final double TURN_P = 0.1;
	public static final double TURN_I = 0;
	public static final double TURN_D = 0;
	
	//Gyro constants
	public static double GYRO_P = 0.08; 
	public static double GYRO_I = 0.0001;
	public static double GYRO_D = 0.003;
	public static final double GYRO_SENSITIVITY = 0.00669;
	
	//ArmWinch constants
	public static double ARM_WINCH_POT_LOW_VOLTAGE = 1.0;
	public static double ARM_WINCH_POT_RANGE = 2.8;
	public static double ARM_WINCH_ERROR = 0.1;
	
	//Robot loop speed
	public static final double REFRESH_RATE = 50;
	
	//Sonic loop speed
	public static final int SONIC_REFRESH_RATE = 10;
	


}
