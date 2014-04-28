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

   public StoryOntology addStory (String attitude, String name, String audience) {
      OntologyIndividual owlPerson = helper.getNamedIndividual(UUID
            .randomUUID().toString());
      owlPerson.addSuperclass(StoryOntology.STORY_CLASS);
      StoryOntology story = new StoryOntology(ontology, owlPerson);
      story.setAttitude(attitude);
      // The time is saved, not from Adj pair, added here.
      story.setTime(new Date().toString());
      story.setName(name);
      return story;
   }

   // rename: get all, find it, change
   public StoryOntology[] getStories () {
      Set<OWLNamedIndividual> owlPeople = helper
            .getAllOfClass(StoryOntology.STORY_CLASS);
      StoryOntology[] people = new StoryOntology[owlPeople.size()];
      int i = 0;
      for (OWLNamedIndividual owlPerson : owlPeople) {
         people[i++] = new StoryOntology(ontology, new OntologyIndividual(
               ontology, owlPerson));
      }
      return people;
   }
}
