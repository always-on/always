package edu.wpi.always.client;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.console.ConsoleRealizersRegistry;
import edu.wpi.disco.rt.ComponentRegistry;
import org.picocontainer.*;

public class DummyClientRegistry implements ComponentRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.addComponent(new UIMessageDispatcher() {

         @Override
         public void send (Message message) {
         }

         @Override
         public void registerReceiveHandler (String messageType,
               MessageHandler handler) {
         }
      });
      new ConsoleRealizersRegistry().register(container);
      container.as(Characteristics.CACHE).addComponent(new MenuPerceptor() {

         @Override
         public void run () {
         }

         @Override
         public MenuPerception getLatest () {
            return null;
         }
      });
      container.as(Characteristics.CACHE).addComponent(new Keyboard() {

         @Override
         public void showKeyboard (String prompt) {
         }

         @Override
         public String getInputSoFar () {
            return "";
         }
      });
      /*
       * container.as(Characteristics.CACHE).addComponent(new CalendarUI() {
       * @Override public void showWeek(LocalDate startDay, CalendarUIListener
       * listener, boolean touchable) { }
       * @Override public void showMonth(LocalDate startDay, CalendarUIListener
       * listener) { }
       * @Override public void showDay(LocalDate day, CalendarUIListener
       * listener, boolean touchable) { } });
       */
   }
}
