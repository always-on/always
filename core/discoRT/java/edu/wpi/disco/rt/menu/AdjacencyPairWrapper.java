package edu.wpi.disco.rt.menu;

import java.util.List;

public abstract class AdjacencyPairWrapper<C extends AdjacencyPair.Context> extends AdjacencyPairBase<C> {

   protected final AdjacencyPair inner;

   public AdjacencyPairWrapper (AdjacencyPair inner) {
      super(null, null);
      this.inner = inner;
   }

   @Override
   public void enter () {}

   @Override
   public boolean prematureEnd () { return inner.prematureEnd(); }

   @Override
   public AdjacencyPair nextState (String text) { return inner.nextState(text); }

   @Override
   public List<String> getChoices () { return inner.getChoices(); }

   @Override
   public double timeRemaining () { return inner.timeRemaining(); }

   @Override
   public boolean isTwoColumnMenu () { return inner.isTwoColumnMenu(); }
   
   @Override
   public C getContext () { return (C) inner.getContext(); }
}

