package edu.wpi.disco.rt.test;

import static org.junit.Assert.*;
import edu.wpi.disco.rt.realizer.petri.*;
import org.junit.Test;

public class PetriNetRunnerFixture {

   @Test
   public void test () {
      Transition beginning = new Transition();
      Transition middle = new Transition();
      Place b1 = new Place();
      Place b2 = new Place();
      Place b3 = new Place();
      Place b4 = new Place();
      b1.setInput(beginning);
      b2.setInput(beginning);
      middle.addInput(b1);
      middle.addInput(b2);
      b3.setInput(middle);
      b4.setInput(middle);
      PetriNetRunner runner = new PetriNetRunner(beginning);
      runner.run();
      assertTrue(b3.getState() == Place.State.ExecutionSuccessful);
      assertTrue(b4.getState() == Place.State.ExecutionSuccessful);
      assertEquals(0, runner.getFailedPlaces().size());
   }

   @Test
   public void testWithAPlaceThatFails () {
      Transition beginning = new Transition();
      Transition middle = new Transition();
      Transition anotherPath = new Transition();
      Place willFail = new PlaceThatFails();
      Place placeOnAnotherPath = new Place();
      Place p = new Place();
      Place p2 = new Place();
      beginning.addOutput(willFail);
      willFail.addOutput(middle);
      middle.addOutput(p);
      beginning.addOutput(placeOnAnotherPath);
      placeOnAnotherPath.addOutput(anotherPath);
      anotherPath.addOutput(p2);
      PetriNetRunner runner = new PetriNetRunner(beginning);
      runner.run();
      assertEquals(Place.State.ExecutionFailed, willFail.getState());
      assertEquals(Place.State.NotActivated, p.getState());
      assertEquals(Place.State.ExecutionSuccessful, p2.getState());
      assertEquals(1, runner.getFailedPlaces().size());
      assertTrue(runner.getFailedPlaces().contains(willFail));
   }

   static class PlaceThatFails extends Place {

      @Override
      public void run () {
         fail();
      }
   }
}
