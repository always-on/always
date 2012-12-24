package edu.wpi.always.test.cm;

import static org.junit.Assert.*;
import edu.wpi.disco.rt.realizer.petri.*;
import org.junit.*;

public class TransitionPlaceConnectionTests {

   private Place p;
   private Transition t;

   @Before
   public void setUp () {
      p = new Place();
      t = new Transition();
   }

   @Test
   public void addOutputToPlace () {
      p.addOutput(t);
      assertTrue(p.getOutputs().contains(t));
      assertTrue(t.getInputs().contains(p));
   }

   @Test
   public void addInputToPlace () {
      p.setInput(t);
      assertSame(t, p.getInput());
      assertTrue(t.getOutputs().contains(p));
   }

   @Test
   public void removeOutputFromPlace () {
      p.addOutput(t);
      p.removeOutput(t);
      assertFalse(p.getOutputs().contains(t));
      assertFalse(t.getInputs().contains(p));
   }

   @Test
   public void removeInputFromPlace () {
      p.setInput(t);
      p.setInput(null);
      assertNull(p.getInput());
      assertFalse(t.getOutputs().contains(p));
   }

   @Test
   public void replaceInputOnPlace () {
      p.setInput(t);
      Transition t2 = new Transition();
      p.setInput(t2);
      assertSame(t2, p.getInput());
      assertFalse(t.getOutputs().contains(p));
      assertTrue(t2.getOutputs().contains(p));
   }

   @Test
   public void addInputToTransition () {
      t.addInput(p);
      assertTrue(t.getInputs().contains(p));
      assertTrue(p.getOutputs().contains(t));
   }

   @Test
   public void addOutputToTransition () {
      t.addOutput(p);
      assertTrue(t.getOutputs().contains(p));
      assertSame(t, p.getInput());
   }

   @Test
   public void removeInputFromTransition () {
      t.addInput(p);
      t.removeInput(p);
      assertFalse(t.getInputs().contains(p));
      assertFalse(p.getOutputs().contains(t));
   }

   @Test
   public void removeOutputFromTransition () {
      t.addOutput(p);
      t.removeOutput(p);
      assertFalse(t.getOutputs().contains(p));
      assertNull(p.getInput());
   }

   @Test
   public void removingOutputPlaceThatIsNotThere_ShouldNotChangeThatPlace () {
      Transition t2 = new Transition();
      p.setInput(t2);
      t.removeOutput(p);
      assertSame(t2, p.getInput());
   }
}
