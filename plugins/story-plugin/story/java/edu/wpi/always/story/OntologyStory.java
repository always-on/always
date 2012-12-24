package edu.wpi.always.story;

import edu.wpi.always.user.owl.*;

public class OntologyStory {

   public static final String STORY_CLASS = "Story";
   public static final String NAME_PROPERTY = "name";
   public static final String ATTITUDE_PROPERTY = "attitude";
   public static final String TIME_PROPERTY = "time";
   public static final String AUDIENCE_PROPERTY = "audience";
   private final OntologyIndividual owlPlace;
   private final OntologyHelper helper;

   public OntologyStory (Ontology ontology, OntologyIndividual owlPlace) {
      this.owlPlace = owlPlace;
      helper = new OntologyHelper(ontology);
   }

   public String getAttitude () {
      return owlPlace.getDataPropertyValue(ATTITUDE_PROPERTY).asString();
   }

   public void setAttitude (String attitude) {
      owlPlace.setDataProperty(ATTITUDE_PROPERTY, helper.getLiteral(attitude));
   }

   public OntologyIndividual getIndividual () {
      return owlPlace;
   }

   public String getTime () {
      return owlPlace.getDataPropertyValue(TIME_PROPERTY).asString();
   }

   public void setTime (String time) {
      owlPlace.setDataProperty(TIME_PROPERTY, helper.getLiteral(time));
   }

   public String getName () {
      return owlPlace.getDataPropertyValue(NAME_PROPERTY).asString();
   }

   public void setName (String name) {
      owlPlace.setDataProperty(NAME_PROPERTY, helper.getLiteral(name));
   }

   public String getAudience () {
      return owlPlace.getDataPropertyValue(AUDIENCE_PROPERTY).asString();
   }

   public void setAudience (String audience) {
      owlPlace.setDataProperty(AUDIENCE_PROPERTY, helper.getLiteral(audience));
   }
}
