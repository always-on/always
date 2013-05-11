package edu.wpi.always.user.owl;

import edu.wpi.always.user.places.Place;
import org.joda.time.DateTimeZone;

public class OntologyPlace implements Place {

   public static final String PLACE_CLASS = "Place";
   public static final String ZIP_PROPERTY = "zip";
   public static final String TIMEZONE_PROPERTY = "timeZone";
   public static final String CITY_NAME_PROPERTY = "cityName";
   public static final String OTHER_NAME_PROPERTY = "otherName";
   private final OntologyIndividual owlPlace;
   private final OntologyHelper helper;

   public OntologyPlace (Ontology ontology, OntologyIndividual owlPlace) {
      this.owlPlace = owlPlace;
      helper = new OntologyHelper(ontology);
   }

   @Override
   public String getZip () {
      return owlPlace.getDataPropertyValue(ZIP_PROPERTY).asString();
   }

   @Override
   public String getCityName () {
      String name = owlPlace.getDataPropertyValue(CITY_NAME_PROPERTY)
            .asString();
      if ( name != null )
         return name;
      return getZip();
   }

   @Override
   public DateTimeZone getTimeZone () {
      return DateTimeZone.forID(owlPlace
            .getDataPropertyValue(TIMEZONE_PROPERTY).asString());
   }

   public OntologyIndividual getIndividual () {
      return owlPlace;
   }

   public void setZip (String zip) {
      owlPlace.setDataProperty(ZIP_PROPERTY, helper.getLiteral(zip));
   }

   public void setCityName (String name) {
      owlPlace.setDataProperty(CITY_NAME_PROPERTY, helper.getLiteral(name));
   }

   public void setTimeZone (DateTimeZone timezone) {
      owlPlace.setDataProperty(TIMEZONE_PROPERTY,
            helper.getLiteral(timezone.getID()));
   }
   
   @Override
   public String toString () { return getCityName(); }
   
}
