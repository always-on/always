package edu.wpi.disco.rt.test;

import static org.junit.Assert.*;
import com.google.common.collect.Lists;
import edu.wpi.disco.rt.menu.MenuBehavior;
import org.junit.Test;

public class MenuBehaviorFixture {

   @Test
   public void testEqualityAndHashCode () {
      MenuBehavior b1 = new MenuBehavior(Lists.newArrayList("Hi", "Hello",
            "Good to see you"));
      MenuBehavior b2 = new MenuBehavior(Lists.newArrayList("Hi", "Hello",
            "Good to see you"));
      MenuBehavior b3 = new MenuBehavior(Lists.newArrayList("Hello", "Hi",
            "Good to see you"));
      MenuBehavior b4 = new MenuBehavior(Lists.newArrayList("Hi", "Hello"));
      assertTrue(b1.equals(b1));
      assertTrue(b1.equals(b2));
      assertTrue(b2.equals(b1));
      assertTrue(b1.hashCode() == b2.hashCode());
      assertFalse(b1.equals(b3));
      assertFalse(b3.equals(b1));
      assertFalse(b1.hashCode() == b3.hashCode());
      assertFalse(b1.equals(b4));
      assertFalse(b4.equals(b1));
      assertFalse(b1.hashCode() == b4.hashCode());
   }
}
