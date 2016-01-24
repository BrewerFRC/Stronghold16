package org.usfirst.frc.team4564.robot;

public class PID {
	//Terms
	private double p;
	private double i;
	private double d;
	
	/* 
	 * Whether or not to cummulate values over time.
	 * True = cummulate, False = raw return value
	 */
	private boolean forward;
	private double target;
	private double min;
	private double max;
	
	private long previousTime;
	private double previousError;
	private double sumError;
	private double output;
	
	public PID(double p, double i, double d, boolean forward) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.forward = forward;
	}
	
	public void setMin(double min) {
		this.min = min;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getTarget() {
		return this.target;
	}
	public void setTarget(double target) {
		this.target = target;
	}
	public void setP(double p) {
		this.p = p;
	}
	public void setI(double i) {
		this.i = i;
	}
	public void setD(double d) {
		this.d = d;
	}
	public void reset() {
		previousTime = Common.time();
		sumError = 0;
		previousError = 0;
	}
	
	public double calc(double input) {
		//Set up time values
		long time = Common.time();
		long deltaTime = time - previousTime;
		previousTime = time;
		
		//Integral calculation
		double error = input - target;
		sumError += error * deltaTime;
		
		//Derivative calculation
		double derivative = (error - previousError) / deltaTime;
		previousError = error;
		
		Common.dashNum("PCalc", p*error);
		Common.dashNum("ICalc", i*sumError);
		//Calculate output
		double output = p*error + i*sumError + d*derivative;
		if (forward) {
			this.output += Math.max(Math.min(output, min), max);
		}
		else {
			this.output = Math.max(Math.min(output, min), max);
		}
		return output;
	}
}
