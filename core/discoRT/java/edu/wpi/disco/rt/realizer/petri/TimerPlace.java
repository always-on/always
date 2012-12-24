package edu.wpi.disco.rt.realizer.petri;

public class TimerPlace extends Place {

   private final int ms;

   public TimerPlace (int milliseconds) {
      this.ms = milliseconds;
   }

   @Override
   public void run () {
      try {
         Thread.sleep(ms);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      done();
   }
}
