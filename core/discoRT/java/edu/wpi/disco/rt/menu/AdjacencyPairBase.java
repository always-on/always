package edu.wpi.disco.rt.menu;

import com.google.common.collect.Lists;
import edu.wpi.cetask.Utils;
import edu.wpi.disco.rt.util.NullArgumentException;
import java.util.*;

public abstract class AdjacencyPairBase<C extends AdjacencyPair.Context> implements AdjacencyPair {

   private final String message;
   private final Map<String, DialogStateTransition> choices = new LinkedHashMap<String, DialogStateTransition>();
   private final C context;
   private final boolean twoColumn;
   protected boolean repeatOption = true;

   protected AdjacencyPairBase (String message, C context) {
      this(message, context, false);
   }

   protected AdjacencyPairBase (String message, C context, boolean twoColumn) {
      this.message = message;
      this.context = context;
      this.twoColumn = twoColumn;
   }

   @Override
   public C getContext () {
      return context;
   }

   protected void choice (String choice, DialogStateTransition transition) {
      if ( choice == null ) throw new NullArgumentException("choice");
      choices.put(Utils.capitalize(choice.trim()), transition);
   }
   
   @Override
   public String getMessage () {
      return message;
   }

   @Override
   public List<String> getChoices () {
      List<String> choices = Lists.newArrayList(this.choices.keySet());
      if( repeatOption && message != null ) choices.add(REPEAT);  
      return choices;
   }

   @Override
   public AdjacencyPair nextState (String text) {
      return REPEAT.equals(text) ? this :
         choices.containsKey(text) ? choices.get(text).run() :
            null;
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
