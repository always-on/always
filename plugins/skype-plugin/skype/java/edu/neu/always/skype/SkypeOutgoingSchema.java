package edu.neu.always.skype;

import java.util.*;
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
         super(getChoices(model).isEmpty() ? 
            "in order to arrange a video call, you need to include an email address when you introduce me to someone you know" :
            "please select the person you would like to arrange a video call with", 
            new AdjacencyPair.Context());
         this.repeatOption = false;
         final String fName = model.getUserFirstName();
         List<Person> choices = getChoices(model);
         for (final Person person : choices)
            choice(person.getName(), new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     return new SkypePerson(getContext(), person, fName);
                  };
               });
         choice(choices.isEmpty() ? "Ok" : "Never mind", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               getContext().getSchema().stop();
               return null;
            }});
      }
     
      private static List<Person> getChoices (UserModel model) {
         List<Person> choices = new ArrayList<Person>();
         for (Person person : model.getPeopleManager().getPeople(false))
            if ( person.getSkypeNumber() != null ) choices.add(person);
         return choices;
      }
   }
   
   private static class SkypePerson extends MultithreadAdjacencyPair<AdjacencyPair.Context> {
      
      private final Person person;
      
      private SkypePerson (Context context, Person person, String fName) { 
         super(sendMail(person, fName),context);
         this.person = person;
         choice("Ok", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               getContext().getSchema().stop();
               return null;
            }});
      }
      
      private static String sendMail (Person person, String fName) {
          String address = person.getSkypeNumber();
          String body = "Dear " + person.getName() +",\n\n" +
         		 fName + " has asked the AlwaysOn system to send you this email so that you can make video calls using Google Hangout. You can start a video call with " + fName + " now or at any later time by visiting the following website:\n\n" +
         		 "https://ragserver.ccs.neu.edu/hangoutTest/login.html?id=" +
         		 Always.THIS.getUserModel().getPeopleManager().getUser().getSkypeNumber() + "\n\n" +
         		 "If " + fName + " is not available, the system will let you know.\n\n" +
         		 "*This is an automatically generated email, if you have any questions about the AlwaysOn System please contact the study team at lring@ccs.neu.edu";
          String subject = "AlwaysOn: Video Call Request from " + fName;
          return MailSender.sendEmail(body, subject, address) ?
    	      ("I have sent "+person.getName()+" an email asking for a video call if "+UserUtils.getPronoun(person)+
    	       " is available.  While we are waiting for a call, we can do other things.")
    	      : ("Sadly I was not able to send an email to " + person.getName() + ". Please try again later.");
      }
      
      @Override
      public void enter () {
         log(Direction.OUTGOING, person.getName());         
      }
   }
}
