package edu.wpi.always.cm.primitives.console;

import com.google.gson.Gson;
import edu.wpi.always.cm.primitives.MenuBehavior;
import edu.wpi.always.cm.realizer.SingleRunPrimitiveRealizer;

public class ConsoleMenuRealizer extends
      SingleRunPrimitiveRealizer<MenuBehavior> {

   public ConsoleMenuRealizer (MenuBehavior params) {
      super(params);
   }

   @Override
   protected void singleRun () {
      Gson gson = new Gson();
      String json = gson.toJson(getParams().getItems());
      System.out.println("Menus for user: " + json);
   }
}
