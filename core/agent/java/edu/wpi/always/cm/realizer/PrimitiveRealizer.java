package edu.wpi.always.cm.realizer;

/**
 * A PrimitiveRealizer's run() method will be called periodically for it to do
 * something (if needed). Do not hold the thread. A PrimitiveRealizer should not
 * require preconditions on the state of the Resource, i.e., it should be able
 * to do its work from any preexisting condition. Any run of run() may be the
 * last run! You cannot count on it being called again in any way.
 * 
 * @author Bahador
 * @param <T>
 */
public interface PrimitiveRealizer<T extends PrimitiveBehavior> extends
      Runnable {

   T getParams ();

   void shutdown ();

   void addObserver (PrimitiveRealizerObserver observer);

   void removeObserver (PrimitiveRealizerObserver observer);
}
