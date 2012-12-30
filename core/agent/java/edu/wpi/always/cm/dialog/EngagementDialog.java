package edu.wpi.always.cm.dialog;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.dialog.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.schema.*;

public class EngagementDialog extends AdjacencyPairBase<Object> {

   public EngagementDialog (final SchemaManager schemaManager) {
      super("Hello", null);
      choice("Hi", new DialogStateTransition() {

         @SuppressWarnings("unchecked")
         @Override
         public AdjacencyPair run () {
            try {
               schemaManager.start((Class<? extends Schema>) Class
                     .forName("edu.wpi.always.test.weather.WeatherSchema"));
            } catch (ClassNotFoundException e) {
               e.printStackTrace();
            }
            return null;
         }
      });
   }
}
