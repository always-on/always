package edu.wpi.always.enroll.schema;


import edu.wpi.disco.rt.menu.*;
import edu.wpi.always.user.people.Person;
import edu.wpi.always.enroll.schema.EditPersonState.*;
import edu.wpi.always.enroll.schema.EnrollAdjacencyPairs.*;

public class InitialEnroll extends EnrollAdjacencyPairImpl {

   public InitialEnroll (final EnrollStateContext context) {
      super("I'm ready for you to tell me about your family and friends", context);
      choice("Okay", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            return new ReadyForStartEvent(context);
         }
      });
      choice("No, not right now.", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            return new DialogEndEvent(context);
         }
      });
      choice("I want to edit someones's profile.", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            return new PeopleSelectEvent(context);
         }
      });
      choice("I want to edit my own profile.", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            Person person = getContext()
                  .getUserModel().getPeopleManager().getUser();
            if(person == null)
               return new NoOwnProfile(getContext());
            return new EditPersonAdjacencyPair(getContext(), person);
         }
      });
   }
   
   public static class NoOwnProfile extends EnrollAdjacencyPairImpl{

      public NoOwnProfile(final EnrollStateContext context) {
         super("Oh! I forgot to ask you to enter your profile first."
            + " After you do, you can edit it.", context);
         choice("Oh! Okay", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new InitialEnroll(context);
            }
         });
      }
   }

   public static class ReadyForStartEvent extends EnrollAdjacencyPairImpl{

      public ReadyForStartEvent(final EnrollStateContext context) {
         super("That's good. Let's start", context);
         choice("Yes, let's start.", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new PersonNameAdjacencyPair(context);
            }
         });
         choice("No, I changed my mind.", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new InitialEnroll(getContext());
            }
         });
      }
   }

   public static class DialogEndEvent extends EnrollAdjacencyPairImpl{


      public DialogEndEvent(final EnrollStateContext context) {
         super("Okay, we can do this again if you want.", context);
         choice("Sure.", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               context.getSchema().cancel();
               return null;
            }
         });
      }
   }

   public static class PeopleSelectEvent extends EnrollAdjacencyPairImpl {

      public PeopleSelectEvent (final EnrollStateContext context) {
         super("Tell me whose profile do you want to edit.", context);
         Person[] people = getContext().getPeopleManager().getPeople(true);
         for(final Person person : people){
            choice(person.getName(), new DialogStateTransition() {

               @Override
               public AdjacencyPair run() {
                  return new EditPersonAdjacencyPair(getContext(), person);
               }
            });
         }
      }
   }
}
