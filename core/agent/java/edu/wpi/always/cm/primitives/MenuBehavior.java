package edu.wpi.always.cm.primitives;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.realizer.PrimitiveBehavior;
import edu.wpi.disco.rt.util.Utils;
import java.util.*;

public class MenuBehavior extends PrimitiveBehavior {

   private final List<String> items;
   private final boolean twoColumn;

   public MenuBehavior (List<String> items) {
      this(items, false);
   }

   public MenuBehavior (List<String> items, boolean twoColumn) {
      this.items = Collections.unmodifiableList(Lists.newArrayList(items));
      this.twoColumn = twoColumn;
   }

   @Override
   public Resource getResource () {
      return AgentResources.MENU;
   }

   @Override
   public int hashCode () {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((items == null) ? 0 : items.hashCode());
      result = prime * result + (twoColumn ? 1231 : 1237);
      return result;
   }

   @Override
   public boolean equals (Object obj) {
      if ( this == obj )
         return true;
      if ( obj == null )
         return false;
      if ( getClass() != obj.getClass() )
         return false;
      MenuBehavior other = (MenuBehavior) obj;
      if ( items == null ) {
         if ( other.items != null )
            return false;
      } else if ( !items.equals(other.items) )
         return false;
      if ( twoColumn != other.twoColumn )
         return false;
      return true;
   }

   public List<String> getItems () {
      return items;
   }

   public boolean isTwoColumn () {
      return twoColumn;
   }

   @Override
   public String toString () {
      return (twoColumn ? "Menu2(" : "MENU(") + Utils.listify(items) + ')';
   }
}
