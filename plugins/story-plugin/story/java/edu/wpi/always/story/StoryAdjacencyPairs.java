package edu.wpi.always.story;

import edu.wpi.always.client.*;
import edu.wpi.always.client.ClientPluginUtils.InstanceResuseMode;
import edu.wpi.always.cm.SchemaImplBase;
import edu.wpi.always.cm.dialog.*;
import edu.wpi.always.cm.ui.KeyboardAdjacenyPair;
import edu.wpi.always.user.people.Person;

public class StoryAdjacencyPairs {

   private static AudioRecorder recorder = new AudioRecorder();
   private static AudioPlayer player = new AudioPlayer();
   private static String storyNameToSave, storyNameToPlayOrEdit, storyMood,
         storyReplayMoodChoice, storyAudience = "";
   private static OntologyStory[] storyList;

   public static class StoryStartAdjacencyPair extends
         AdjacencyPairImpl<StoryStateContext> {

      public StoryStartAdjacencyPair (final StoryStateContext context) {
         super(
               "Do you want to listen to a story you told me before or to tell me a new one?",
               context);
         choice("Tell you a new one", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new EnterStoryNameToRecord(context);
            }
         });
         choice("See story list to listen or edit",
               new DialogStateTransition() {

                  @Override
                  public AdjacencyPair run () {
                     return new StoryReplayStoryTypeChoiceAdjacencyPair(context);
                  }
               });
      }

      @Override
      public void enter () {
         ClientPluginUtils.startPlugin(getContext().getDispatcher(), "story",
               InstanceResuseMode.Reuse, null);
      }
   }

   public static class StoryRecordingAdjacencyPair extends
         AdjacencyPairImpl<StoryStateContext> {

      public StoryRecordingAdjacencyPair (final StoryStateContext context) {
         super("Go ahead, I'm listening...", context);
         choice("Done!", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new StoryRecordDoneGetAttitudeAdjacencyPair(context);
            }
         });
      }

      @Override
      public void enter () {
         ClientPluginUtils.startPlugin(getContext().getDispatcher(), "story",
               InstanceResuseMode.Reuse, null);
         recorder.record(storyNameToSave);
         // SchemaImplBase.backchanneling = true;
      }
   }

   public static class StoryReplayAdjacencyPair extends
         AdjacencyPairImpl<StoryStateContext> {

      public StoryReplayAdjacencyPair (final StoryStateContext context) {
         super("", context);
         choice("Done!", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new StoryStartAdjacencyPair(context);
            }
         });
      }

      @Override
      public void enter () {
         player.play(storyNameToPlayOrEdit);
      }
   }

   public static class StoryRecordDoneGetAttitudeAdjacencyPair extends
         AdjacencyPairImpl<StoryStateContext> {

      public StoryRecordDoneGetAttitudeAdjacencyPair (
            final StoryStateContext context) {
         super(
               "Could you tell me if it was a happy story, neutral or sad one?",
               context);
         choice("Happy:)", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               storyMood = "happy";
               return new StoryRecordDoneGetAudienceAdjacencyPair(context);
            }
         });
         choice("So so... Neutral", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               storyMood = "neutral";
               return new StoryRecordDoneGetAudienceAdjacencyPair(context);
            }
         });
         choice("Sad:(", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               storyMood = "sad";
               return new StoryRecordDoneGetAudienceAdjacencyPair(context);
            }
         });
      }

      @Override
      public void enter () {
         SchemaImplBase.backchanneling = false;
         recorder.stopRecording();
      }
   }

   public static class StoryRecordDoneGetAudienceAdjacencyPair extends
         AdjacencyPairImpl<StoryStateContext> {

      public StoryRecordDoneGetAudienceAdjacencyPair (
            final StoryStateContext context) {
         super("Was this story meant for someone? If yes, tell me who?",
               context);
         Person[] persons = context.getPeopleManager().getPeople();
         for (final Person person : persons)
            choice(person.getName(), new DialogStateTransition() {

               @Override
               public AdjacencyPair run () {
                  storyAudience = person.getName();
                  return new StoryRecordDoneSaveOntologyAdjacencyPair(context);
               }
            });
         choice("Some friend", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               storyAudience = "friend";
               return new StoryRecordDoneSaveOntologyAdjacencyPair(context);
            }
         });
         choice("A family member", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               storyAudience = "family";
               return new StoryRecordDoneSaveOntologyAdjacencyPair(context);
            }
         });
         choice("Let me tell you who", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new EnterAudienceName(context);
            }
         });
         choice("No one particular", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               storyAudience = "noone";
               return new StoryRecordDoneSaveOntologyAdjacencyPair(context);
            }
         });
      }

      @Override
      public void enter () {
         recorder.stopRecording();
      }
   }

   public static class StoryRecordDoneSaveOntologyAdjacencyPair extends
         AdjacencyPairImpl<StoryStateContext> {

      public StoryRecordDoneSaveOntologyAdjacencyPair (
            final StoryStateContext context) {
         super("I will remember that.", context);
         choice("Go back", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new StoryStartAdjacencyPair(context);
            }
         });
      }

      @Override
      public void enter () {
         getContext().getStoryManager().addStory(storyMood, storyNameToSave,
               storyAudience);
      }
   }

   public static class EnterAudienceName extends
         KeyboardAdjacenyPair<StoryStateContext> {

      public EnterAudienceName (StoryStateContext context) {
         super("Type his or her name, I will remember.", context, context
               .getKeyboard());
      }

      @Override
      public AdjacencyPair success (String text) {
         storyAudience = text;
         return new StoryRecordDoneSaveOntologyAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel () {
         return new StoryRecordDoneGetAudienceAdjacencyPair(getContext());
      }
   }

   public static class EnterStoryNameToRecord extends
         KeyboardAdjacenyPair<StoryStateContext> {

      public EnterStoryNameToRecord (StoryStateContext context) {
         super("What do you want the name of it to be?", context, context
               .getKeyboard());
      }

      @Override
      public AdjacencyPair success (String text) {
         storyNameToSave = text;
         return new StoryRecordingAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel () {
         return new StoryStartAdjacencyPair(getContext());
      }
   }

   public static class StoryReplayStoryTypeChoiceAdjacencyPair extends
         AdjacencyPairImpl<StoryStateContext> {

      public StoryReplayStoryTypeChoiceAdjacencyPair (
            final StoryStateContext context) {
         super("What mood do you want to chose from?", context);
         choice("Happy ones", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               storyReplayMoodChoice = "happy";
               return new StoryReplayShowStoriesAdjacencyPair(context);
            }
         });
         choice("Neutral ones", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               storyReplayMoodChoice = "neutral";
               return new StoryReplayShowStoriesAdjacencyPair(context);
            }
         });
         choice("Sad ones", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               storyReplayMoodChoice = "sad";
               return new StoryReplayShowStoriesAdjacencyPair(context);
            }
         });
         choice("All", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               storyReplayMoodChoice = "all";
               return new StoryReplayShowStoriesAdjacencyPair(context);
            }
         });
         choice("Write story name directly", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new EnterStoryNameToPlay(context);
            }
         });
         choice("Go back", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new StoryStartAdjacencyPair(context);
            }
         });
      }

      @Override
      public void enter () {
         storyList = getContext().getStoryManager().getStories();
      }
   }

   public static class StoryReplayShowStoriesAdjacencyPair extends
         AdjacencyPairImpl<StoryStateContext> {

      public StoryReplayShowStoriesAdjacencyPair (
            final StoryStateContext context) {
         super("I remember these:", context);
         for (final OntologyStory storyInstance : storyList)
            if ( storyInstance.getAttitude().contains(storyReplayMoodChoice)
               || storyReplayMoodChoice.equals("all") )
               choice(storyInstance.getName(), new DialogStateTransition() {

                  @Override
                  public AdjacencyPair run () {
                     storyNameToPlayOrEdit = storyInstance.getName();
                     return new StoryReplayEditOneStory(getContext());
                  }
               });
      }

      @Override
      public void enter () {
      }
   }

   public static class StoryReplayEditOneStory extends
         AdjacencyPairImpl<StoryStateContext> {

      public StoryReplayEditOneStory (final StoryStateContext context) {
         super("Do you want to play it, edit the name or just delete it?",
               context);
         choice("Play it", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new StoryReplayAdjacencyPair(getContext());
            }
         });
         choice("Edit its name", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new StoryReplayAdjacencyPair(getContext());
            }
         });
         choice("Delete it", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new StoryReplayAdjacencyPair(getContext());
            }
         });
      }

      @Override
      public void enter () {
      }
   }

   public static class EnterStoryNameToPlay extends
         KeyboardAdjacenyPair<StoryStateContext> {

      public EnterStoryNameToPlay (StoryStateContext context) {
         super("Tell me the story name:", context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success (String text) {
         storyNameToPlayOrEdit = text;
         return new StoryReplayAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel () {
         return new StoryStartAdjacencyPair(getContext());
      }
   }
}
