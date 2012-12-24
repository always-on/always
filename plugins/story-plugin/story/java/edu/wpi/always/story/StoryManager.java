package edu.wpi.always.story;

import edu.wpi.always.user.owl.*;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import java.util.*;

public class StoryManager {

   private final OntologyHelper helper;
   private final Ontology ontology;

   public StoryManager (Ontology ontology) {
      this.ontology = ontology;
      helper = new OntologyHelper(ontology);
   }

   public OntologyStory addStory (String attitude, String name, String audience) {
      OntologyIndividual owlPerson = helper.getNamedIndividual(UUID
            .randomUUID().toString());
      owlPerson.addSuperclass(OntologyStory.STORY_CLASS);
      OntologyStory story = new OntologyStory(ontology, owlPerson);
      story.setAttitude(attitude);
      // The time is saved, not from Adj pair, added here.
      story.setTime(new Date().toString());
      story.setName(name);
      return story;
   }

   // rename: get all, find it, change
   public OntologyStory[] getStories () {
      Set<OWLNamedIndividual> owlPeople = helper
            .getAllOfClass(OntologyStory.STORY_CLASS);
      OntologyStory[] people = new OntologyStory[owlPeople.size()];
      int i = 0;
      for (OWLNamedIndividual owlPerson : owlPeople) {
         people[i++] = new OntologyStory(ontology, new OntologyIndividual(
               ontology, owlPerson));
      }
      return people;
   }
}
