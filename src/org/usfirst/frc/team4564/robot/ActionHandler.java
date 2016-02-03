package org.usfirst.frc.team4564.robot;

import java.util.function.Supplier;

public class ActionHandler {

	private Supplier<Boolean> targetReached;
	
	public ActionHandler() {
		targetReached = () -> true;
	}
	
	public void setTargetReachedFunction(Supplier<Boolean> targetReached) {
		if (isComplete()) {
			this.targetReached = targetReached;
		}
	}
	
	public boolean isComplete() {
		return targetReached.get();
	}
}
