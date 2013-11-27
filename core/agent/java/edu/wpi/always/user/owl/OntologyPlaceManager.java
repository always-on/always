package edu.wpi.always.user.owl;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import edu.wpi.always.user.UserModelBase;
import edu.wpi.always.user.places.Place;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.always.user.places.ZipCodes;
import edu.wpi.always.user.places.ZipCodes.ZipCodeEntry;

public class OntologyPlaceManager implements PlaceManager {

   private final ZipCodes zipCodes;
   private final OntologyHelper helper;
   private final Ontology ontology;

   public OntologyPlaceManager (Ontology ontology, ZipCodes zipCodes) {
      this.ontology = ontology;
      helper = new OntologyHelper(ontology);
      this.zipCodes = zipCodes;
   }

   @Override
   public ZipCodes getZipCodes () {
      return zipCodes;
   }

   public OntologyPlace addPlace (String zip) {
      OntologyIndividual owlPlace = helper.getNamedIndividual(zip);
      owlPlace.addSuperclass(OntologyPlace.PLACE_CLASS);
      OntologyPlace place = new OntologyPlace(helper.getOntologyDataObject(),
            owlPlace);
      ZipCodeEntry placeData = zipCodes.getPlaceData(zip);
      place.setZip(zip);
      if ( placeData != null ) {
         place.setCityName(placeData.getCity() + ", " + placeData.getState());
         place.setTimeZone(placeData.getTimezone());
      }
      UserModelBase.saveIf();
      return place;
   }

   public OntologyPlace getPlace (OntologyIndividual owlPlace) {
      if ( owlPlace == null )
         return null;
      if ( !owlPlace.hasSuperclass(OntologyPlace.PLACE_CLASS) )
         return null;
      return new OntologyPlace(helper.getOntologyDataObject(), owlPlace);
   }

   @Override
   public OntologyPlace getPlace (String zip) {
      if ( zip == null )
         return null;
      // TODO get individual by zip property
      OntologyPlace place = getPlace(helper.getNamedIndividual(zip));
      if ( place == null )
         return addPlace(zip);
      return place;
   }

   @Override
   public Place[] getPlaces () {
      Set<OWLNamedIndividual> owlPlaces = helper
            .getAllOfClass(OntologyPlace.PLACE_CLASS);
      Place[] places = new Place[owlPlaces.size()];
      int i = 0;
      for (OWLNamedIndividual owlPlace : owlPlaces) {
         places[i++] = getPlace(new OntologyIndividual(ontology, owlPlace));
      }
      return places;
   }
}
