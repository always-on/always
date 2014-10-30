package edu.neu.always.skype;

import edu.wpi.always.Always;
import edu.wpi.always.client.SkypeUserHandler;
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
         final String fName = model.getUserFirstName();
         for (final Person person : model.getPeopleManager().getPeople(false)) {
            if ( person.getSkypeNumber() != null )
               choice(person.getName(), new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     return new SkypePerson(getContext(), person, fName);
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
      private final String fName;
      private String id; //TODO: Get this from the usermodel
      
      private SkypePerson (Context context, Person person, String fName) { 
         super("I have sent "+person.getName()+" an email asking for a video call if "
               +UserUtils.getPronoun(person)+" is available.  While we are waiting for a call, we can do other things.",
               context);
         this.person = person;
         this.fName = fName;
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
         id = SkypeUserHandler.USER_ID;
         String address = person.getSkypeNumber();
         // TODO send email to person.getSkypeName() and wait for incoming call?
         String body = person.getName() +",\n\n" +
        		 fName + " would like to have a video call with you using the AlwaysOn system.\n" +
        		 "You can call them by going to the following website:\n\n" +
        		 "https://ragserver.ccs.neu.edu/hangoutTest/login.html?id=" + id + "\n\n" +
        		 "*This is an automatically generated email, if you have any questions about the AlwaysOn System please contact the study team at lring@ccs.neu.edu";
         String subject = "AlwaysOn: Video Call Request from " + fName;
         MailSender.sendEmail(body, subject, address);
      }
   }
}
