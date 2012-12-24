package edu.wpi.disco.rt;

import edu.wpi.disco.*;
import edu.wpi.disco.plugin.*;

public class DiscoUser extends Actor {

   public DiscoUser (String name) {
      super(name);
      new UtterancePlugin(agenda, 100, true); // instead of AuthorizedPlugin
      new ProposeGlobalPlugin(agenda, 95);
      new ProposeShouldSelfPlugin(agenda, 90, false);
      new AskShouldPlugin(agenda, 80);
      new ProposeShouldOtherPlugin(agenda, 70);
      new ProposeWhoPlugin(agenda, 50);
      new ProposeWhatPlugin(agenda, 50);
      new AskWhatPlugin(agenda, 50);
      new DecompositionPlugin(agenda, 25, true, true);
      new DiscoProposeShouldNotPlugin(this, agenda, 5);
      new ProposeHowPlugin(agenda, 30);
   }

   @Override
   protected boolean synchronizedRespond (Interaction interaction, boolean ok,
         boolean guess) {
      return true;
   }
}
