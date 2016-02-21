package org.usfirst.frc.team4564.robot;

import java.util.function.Supplier;

public class ActionHandler {

	private Supplier<Boolean> targetReached;
	private boolean turning;
	private int completeCounter = 0;
	
	public ActionHandler() {
		targetReached = () -> true;
		turning = false;
	}
	
	public void setTargetReachedFunction(Supplier<Boolean> targetReached) {
		if (isComplete()) {
			this.targetReached = targetReached;
		}
	}
	
	public boolean isComplete() {
		if (targetReached.get()) {
		//	completeCounter++;
		//	if (completeCounter >= 5) {
			return true;
		//	}
		}
		else {
			completeCounter = 0;
		}
		return false;
	}
	
	public void setTurning(boolean turning) {
		this.turning = turning;
	}
	
	public boolean isTurning() {
		return turning;
	}
}
