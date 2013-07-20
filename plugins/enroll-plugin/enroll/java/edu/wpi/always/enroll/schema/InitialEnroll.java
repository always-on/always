package edu.wpi.always.enroll.schema;


import edu.wpi.disco.rt.menu.*;
import edu.wpi.always.user.people.Person;
import edu.wpi.always.enroll.schema.EditPersonState.*;
import edu.wpi.always.enroll.schema.EnrollAdjacencyPairs.*;

public class InitialEnroll extends EnrollAdjacencyPairImpl {

   public InitialEnroll (final EnrollStateContext context) {
      super("I'm ready for you to tell me about your family and friends.", context);
      choice("Okay.", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            return new ReadyForStartEvent(context);
         }
      });
      choice("No, I'm not ready for doing that.", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            return new DialogEndEvent(context);
         }
      });
      choice("I want to edit people's profile.", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            return new PeopleSelectEvent(context);
         }
      });
      choice("I want to edit my own profile.", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            return new EditPersonAdjacencyPair(getContext(), 
                  getContext().getUserModel().getPeopleManager().getUser());
         }
      });
   }

   public static class ReadyForStartEvent extends EnrollAdjacencyPairImpl{

      public ReadyForStartEvent(final EnrollStateContext context) {
         super("That's good. Let's start right now.", context);
         choice("Yes, let's start.", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new PersonNameAdjacencyPair(context);
            }
         });
         choice("No, I changed my mind.", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new EditPersonAdjacencyPair(getContext(), 
                     getContext().getUserModel().getPeopleManager().getUser());
            }
         });
      }
   }

   public static class DialogEndEvent extends EnrollAdjacencyPairImpl{


      public DialogEndEvent(final EnrollStateContext context) {
         super("Okay, we can do this any other time if you want.", context);
         choice("Sure.", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new InitialEnroll(getContext());
            }
         });
      }
   }

   public static class PeopleSelectEvent extends EnrollAdjacencyPairImpl {

      public PeopleSelectEvent (final EnrollStateContext context) {
         super("Tell me whose profile do you want to edit.", context);
         Person[] people = getContext().getPeopleManager().getPeople();
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
