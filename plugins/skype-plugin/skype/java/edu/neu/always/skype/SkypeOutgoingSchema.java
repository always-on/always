package edu.neu.always.skype;

import edu.wpi.always.Always;
import edu.wpi.always.user.*;
import edu.wpi.always.user.people.Person;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class SkypeOutgoingSchema extends SkypeSchema {

   public SkypeOutgoingSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(new SkypeOutgoing(always.getUserModel()),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always);
   }
   
   private static class SkypeOutgoing extends MultithreadAdjacencyPair<AdjacencyPair.Context> {

      private SkypeOutgoing (UserModel model) {
         super("please select the person you would like to arrange a video call with", new AdjacencyPair.Context());
         this.repeatOption = false;
         for (final Person person : model.getPeopleManager().getPeople(false)) {
            if ( person.getSkypeNumber() != null )
               choice(person.getName(), new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     return new SkypePerson(getContext(), person);
                  };
               });
         }
         choice("Never mind", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               getContext().getSchema().stop();
               return null;
            }});
      }
   }
   
   private static class SkypePerson extends MultithreadAdjacencyPair<AdjacencyPair.Context> {
      
      private final Person person;
      
      private SkypePerson (Context context, Person person) { 
         super("I have sent "+person.getName()+" an email asking for a video call if "
               +UserUtils.getPronoun(person)+" is available.  While we are waiting for a call, we can do other things.",
               context);
         this.person = person; 
         choice("Ok", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               getContext().getSchema().stop();
               return null;
            }});
      }
      
      @Override
      public void enter () {
         log(Direction.OUTGOING, person.getName());
         // TODO send email to person.getSkypeName() and wait for incoming call?
      }
   }
}
