package edu.wpi.disco.rt;

import edu.wpi.disco.*;
import edu.wpi.disco.rt.action.*;
import edu.wpi.disco.rt.util.Utils;
import java.awt.Frame;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Every call to Disco should be mediated by this class. It ensures synchronized
 * access to Disco
 * 
 * @author Bahador
 */
public class DiscoSynchronizedWrapper {

   private final Disco disco;
   private final ReentrantLock lock = new ReentrantLock();
   private ConsoleWindow window;

   public DiscoSynchronizedWrapper (Agent agent, String consoleTitle) {
      this(new Interaction(agent, new User("user")).getDisco());
      if ( consoleTitle != null ) {
         window = new ConsoleWindow(disco.getInteraction(), 600, 500, 14);
         window.setExtendedState(Frame.ICONIFIED);
         window.setTitle(consoleTitle);
      }
   }
      
   public DiscoSynchronizedWrapper (Disco disco) {
      this.disco = disco;
      Interaction interaction = disco.getInteraction();
      // TODO this hack is b/c generate actions call Agenda.visit() directly
      //      figure out why
      Utils.setAgendaInteraction(interaction.getExternal().getAgenda(),
            interaction);
      Utils.setAgendaInteraction(interaction.getSystem().getAgenda(),
            interaction);
     
   }

   // TODO remove this
   public Disco getDisco () { return disco; }
   
   public void execute (DiscoAction task) {
      lock.lock();
      try {
         task.execute(disco);
      } finally {
         lock.unlock();
      }
   }

   public <T> T execute (DiscoFunc<T> func) {
      lock.lock();
      try {
         return func.execute(disco);
      } finally {
         lock.unlock();
      }
   }
}
