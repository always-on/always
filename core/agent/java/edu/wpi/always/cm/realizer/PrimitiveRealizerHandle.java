package edu.wpi.always.cm.realizer;

import java.util.concurrent.*;

public interface PrimitiveRealizerHandle {
	
	PrimitiveBehavior getBehavior();
	
	boolean isDone();

	boolean isRunning();

	void waitUntilDoneOrCanceled() throws InterruptedException, ExecutionException;

	/**
	 * 
	 * @param timeout
	 *            in milliseconds
	 * @throws TimeoutException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	void waitUntilDoneOrCanceled(long timeout) throws InterruptedException, ExecutionException, TimeoutException;
}
