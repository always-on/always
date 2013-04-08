package edu.wpi.always.user.owl;

import edu.wpi.always.user.*;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.*;
import java.io.File;

public class OntologyUserRegistry implements ComponentRegistry, OntologyRegistry {

   private final String username;
   private final File userDataLocation;

   public OntologyUserRegistry (String username, String userOntologyPath) {
      this.username = username;
      if ( userOntologyPath == null )
         throw new IllegalArgumentException("Ontology path cannot be null");
      userDataLocation = UserUtils.getUserFile(userOntologyPath);
   }

   public OntologyUserRegistry (String username) {
      this(username, "User.owl");
   }

   @Override
   public void register (MutablePicoContainer container) {
      container.as(Characteristics.CACHE).addComponent(UserModel.class,
            OntologyUserModel.class);
      container.as(Characteristics.CACHE).addComponent(Ontology.class,
            OntologyImpl.class);
      container.as(Characteristics.CACHE).addComponent(OntologyHelper.class);
      container.as(Characteristics.CACHE)
            .addComponent(OntologyRuleHelper.class);
      container.as(Characteristics.CACHE).addComponent(
            BindKey.bindKey(File.class,
                  OntologyUserModel.UserOntologyLocation.class),
            userDataLocation);
      container.as(Characteristics.CACHE).addComponent(
            BindKey.bindKey(String.class, UserModel.UserName.class), username);
      container.as(Characteristics.CACHE).addComponent(ZipCodes.class);
      container.getComponent(ZipCodes.class);// force zip codes to be loaded on
                                             // program start
      container.as(Characteristics.CACHE).addComponent(PeopleManager.class,
            OntologyPeopleManager.class);
      container.as(Characteristics.CACHE).addComponent(Calendar.class,
            OntologyCalendar.class);
      container.as(Characteristics.CACHE).addComponent(PlaceManager.class,
            OntologyPlaceManager.class);
      container.getComponent(OntologyPeopleManager.class).setUserModel(
            container.getComponent(OntologyUserModel.class));
   }

   @Override
   public void register (OntologyRuleHelper ontology) {
      ontology.addAxiomsFromInputStream(getClass().getResourceAsStream(
            "/edu/wpi/always/user/owl/People.owl"));
      ontology.addAxiomsFromInputStream(getClass().getResourceAsStream(
            "/edu/wpi/always/user/owl/Calendar.owl"));
      ontology.addAxiomsFromInputStream(getClass().getResourceAsStream(
            "/edu/wpi/always/user/owl/Place.owl"));
   }
}
