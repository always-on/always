package edu.wpi.always.cm;

import java.util.concurrent.*;

public class Scheduler {
	private final ScheduledExecutorService executor;

	public Scheduler () {
		this.executor = createExecutor();
	}

	/***
	 * 
	 * @param runnable
	 * @param interval
	 * in milliseconds
	 */
	public void schedule (Runnable runnable, long interval) {
		executor.scheduleWithFixedDelay(runnable, 0, interval,
				TimeUnit.MILLISECONDS);
	}
	
	private static ScheduledThreadPoolExecutor createExecutor () {
		return new ScheduledThreadPoolExecutor(Runtime.getRuntime()
				.availableProcessors() * 2) {
			@Override
			protected void afterExecute (Runnable r, Throwable t) {
				super.afterExecute(r, t);

				if (t == null && Future.class.isAssignableFrom(r.getClass())) {
					if (!((Future<?>) r).isDone())
						return;

					try {
						((Future<?>) r).get(1, TimeUnit.MILLISECONDS);
					} catch (CancellationException ce) {
						t = ce;
					} catch (ExecutionException ee) {
						t = ee.getCause();
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					} catch (TimeoutException ex) {
						System.out.print(r);
					}
				}

				if (t != null) {
					System.out.println(t);
					System.out.println(t.getMessage());
					for (StackTraceElement e : t.getStackTrace()) {
						System.out.println(e);
					}
				}
			}
		};
	}


}
