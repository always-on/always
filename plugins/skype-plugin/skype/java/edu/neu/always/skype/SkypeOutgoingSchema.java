package edu.neu.always.skype;

import edu.wpi.always.Always;
import edu.wpi.always.client.UIMessageDispatcher;
import edu.wpi.always.cm.perceptors.FacePerceptor;
import edu.wpi.always.user.*;
import edu.wpi.always.user.people.Person;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class SkypeOutgoingSchema extends SkypeSchema {

   /* TODO for logging:
    * 
    * Note: If you are satisfied with the log messages that are already
    * automatically generated for start/end of activity and for all
    * user model updates, then you can delete the log method below
    * (and already defined enums above, if any) and go directly to (4) below.
    *
    * (1) Add arguments to log method below as needed (use enums instead of
    *     string constants to avoid typos and ordering errors!)
    *     
    * (2) Update always/docs/log-format.txt with any new logging fields
    * 
    * (3) Call log method at appropriate places in code
    * 
    * (4) Remove this comment!
    *
    */
   public static void log (int duration) { log(Direction.OUTGOING, duration); }
   
   public SkypeOutgoingSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, FacePerceptor shore, UIMessageDispatcher dispatcher, 
         SkypeClient client, Always always) {
      super(new SkypeOutgoing(always.getUserModel()),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor,
            shore, dispatcher, client, always);
   }
   
   private static class SkypeOutgoing extends MultithreadAdjacencyPair<AdjacencyPair.Context> {

      private SkypeOutgoing (UserModel model) {
         super("please select the person you would like to arrange a video call with", new AdjacencyPair.Context());
         this.repeatOption = false;
         for (final Person person : model.getPeopleManager().getPeople(false)) {
            if (  person.getSkypeNumber() != null )
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
               return new SkypeStop(getContext());
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
               return new SkypeStop(getContext());
            }});
      }
      
      @Override
      public void enter () {
         // TODO send email to person.getSkypeName() and wait for incoming call?
      }
   }
}
