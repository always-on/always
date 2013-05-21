package edu.wpi.disco.rt.menu;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.util.NullArgumentException;
import java.util.*;

public abstract class AdjacencyPairBase<C> implements AdjacencyPair {

   private final String message;
   private final Map<String, DialogStateTransition> choices = new LinkedHashMap<String, DialogStateTransition>();
   private final C context;
   private final boolean twoColumn;

   public AdjacencyPairBase (String message, C context) {
      this(message, context, false);
   }

   public AdjacencyPairBase (String message, C context, boolean twoColumn) {
      this.message = message;
      this.context = context;
      this.twoColumn = twoColumn;
   }

   public C getContext () {
      return context;
   }

   protected void choice (String choice, DialogStateTransition transition) {
      if ( choice == null )
         throw new NullArgumentException("choice");
      choices.put(choice, transition);
   }

   @Override
   public String getMessage () {
      return message;
   }

   @Override
   public List<String> getChoices () {
      return Lists.newArrayList(choices.keySet());
   }

   @Override
   public AdjacencyPair nextState (String text) {
      if ( choices.containsKey(text) )
         return choices.get(text).run();
      return null;
   }

   @Override
   public double timeRemaining () { return 0; }

   @Override
   public void enter () {}

   @Override
   public boolean isTwoColumnMenu () { return twoColumn; }

   @Override
   public boolean prematureEnd () { return false; }
}
