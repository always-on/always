package edu.wpi.always.client;

import edu.wpi.always.PicoRegistry;
import edu.wpi.always.cm.realizer.AudioFileRealizer;
import org.picocontainer.*;

public class ClientRegistry implements PicoRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.addComponent(new UIMessageDispatcherImpl(new TcpConnection(
            "localhost", 11000)));
      container.addComponent(GazeRealizer.class);
      container.addComponent(FaceExpressionRealizer.class);
      container.addComponent(IdleBehaviorRealizer.class);
      container.addComponent(FaceTrackerRealizer.class);
      container.addComponent(SpeechRealizer.class);
      container.addComponent(AudioFileRealizer.class);
      container.addComponent(ClientMenuRealizer.class);
      container.addComponent(ClientMenuPerceptor.class);
      container.as(Characteristics.CACHE).addComponent(
            KeyboardMessageHandler.class);
      // container.as(Characteristics.CACHE).addComponent(RagCalendar.class);
      container.as(Characteristics.CACHE).addComponent(ClientProxy.class);
   }
}
