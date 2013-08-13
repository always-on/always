package edu.wpi.always.user.owl;

import org.picocontainer.Characteristics;
import org.picocontainer.MutablePicoContainer;

import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.always.user.places.ZipCodes;
import edu.wpi.disco.rt.util.ComponentRegistry;

public class OntologyUserRegistry implements ComponentRegistry, OntologyRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.as(Characteristics.CACHE).addComponent(UserModel.class,
            OntologyUserModel.class);
      container.as(Characteristics.CACHE).addComponent(Ontology.class,
            OntologyImpl.class);
      container.as(Characteristics.CACHE).addComponent(OntologyHelper.class);
      container.as(Characteristics.CACHE)
            .addComponent(OntologyRuleHelper.class);
      container.as(Characteristics.CACHE).addComponent(ZipCodes.class);
      container.getComponent(ZipCodes.class);// force zip codes to be loaded now
      container.as(Characteristics.CACHE).addComponent(PeopleManager.class,
            OntologyPeopleManager.class);
      container.as(Characteristics.CACHE).addComponent(Calendar.class,
            OntologyCalendar.class);
      container.as(Characteristics.CACHE).addComponent(PlaceManager.class,
            OntologyPlaceManager.class);      
   }

   @Override
   public void register (OntologyRuleHelper ontology) {
      ontology.addAxioms(getClass().getResourceAsStream("/edu/wpi/always/user/owl/People.owl"));
      ontology.addAxioms(getClass().getResourceAsStream("/edu/wpi/always/user/owl/Calendar.owl"));
      ontology.addAxioms(getClass().getResourceAsStream("/edu/wpi/always/user/owl/Place.owl"));
   }
}
