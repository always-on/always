package edu.wpi.always.cm.perceptors.async;

import edu.wpi.always.cm.*;

public interface BufferablePerceptor<T extends Perception> extends Perceptor<T> {

   public PerceptorBuffer<T> newBuffer ();
}
