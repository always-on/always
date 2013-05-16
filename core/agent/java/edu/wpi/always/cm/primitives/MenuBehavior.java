package edu.wpi.always.cm.primitives;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.realizer.PrimitiveBehavior;
import edu.wpi.disco.rt.util.Utils;
import java.util.*;

public class MenuBehavior extends PrimitiveBehavior {

   private final List<String> items;
   private final boolean twoColumn, extension;

   /**
    * Due to the extension menu {@link #equals(Object)} is defined so that an empty
    * non-extension menu behavior is <em>not</em> equal to another empty non-extension menu 
    * behavior, so it can be used to refresh the extension, if any.
    */
   public static final MenuBehavior EMPTY = new MenuBehavior(Collections.<String>emptyList());

   public MenuBehavior (List<String> items) {
      this(items, false, false);
   }

   public MenuBehavior (List<String> items, boolean twoColumn) {
      this(items, twoColumn, false);
   }

   public MenuBehavior (List<String> items, boolean twoColumn, boolean extension) {
      this.items = Collections.unmodifiableList(items);
      this.twoColumn = twoColumn;
      this.extension = extension;
   }

   @Override
   public Resource getResource () {
      return extension ? AgentResources.MENU_EXTENSION : AgentResources.MENU;
   }

   @Override
   public int hashCode () {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((items == null) ? 0 : items.hashCode());
      result = prime * result + (twoColumn ? 1231 : 1237);
      result = prime * result + (extension ? 1231 : 1237);
      return result;
   }

   @Override
   public boolean equals (Object obj) {
      if ( this == obj ) return true;
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
      if ( extension != other.extension )
         return false;
      return !(!extension && (items == null || items.size() == 0));
   }

   public List<String> getItems () {
      return items;
   }

   public boolean isTwoColumn () {
      return twoColumn;
   }
   
   public boolean isExtension () {
      return extension;
   }

   @Override
   public String toString () {
      return (extension ? "MENU_EXT(" : twoColumn ? "MENU2(" : "MENU(")
            + Utils.listify(items) + ')';
   }
}
