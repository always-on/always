package edu.wpi.always;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.schemas.ActivityStarterSchema;
import edu.wpi.always.rm.*;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.owl.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.util.DiscoDocument;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.picocontainer.*;
import org.picocontainer.behaviors.OptInCaching;
import java.util.*;

public class Bootstrapper {

   private final MutablePicoContainer container =
         new PicoBuilder().withBehaviors(new OptInCaching())
            .withConstructorInjection().build();

   public Bootstrapper (boolean logToConsole) {
      if ( logToConsole )
         BasicConfigurator.configure();
      else
         BasicConfigurator.configure(new NullAppender());
      container.addComponent(container);
      container.as(Characteristics.CACHE).addComponent(
            ICollaborationManager.class,
            edu.wpi.always.cm.CollaborationManager.class);
      container.as(Characteristics.CACHE).addComponent(
            // note using DUMMY relationship manager!
            IRelationshipManager.class, DummyRelationshipManager.class);
   }

   private final List<ComponentRegistry> componentRegistries = new ArrayList<ComponentRegistry>();
   private final List<OntologyRegistry> ontologyRegistries = new ArrayList<OntologyRegistry>();

   public void addRegistry (Registry registry) {
      if ( registry instanceof ComponentRegistry )
         componentRegistries.add((ComponentRegistry) registry);
      if ( registry instanceof OntologyRegistry )
         ontologyRegistries.add((OntologyRegistry) registry);
   }

   private final List<Registry> cmRegistries = new ArrayList<Registry>();

   public void addCMRegistry (Registry registry) {
      cmRegistries.add(registry);
   }

   public void start () {
      for (ComponentRegistry registry : componentRegistries)
         registry.register(container);
      OntologyRuleHelper helper = container.getComponent(OntologyRuleHelper.class);
      for (OntologyRegistry registry : ontologyRegistries)
         registry.register(helper);
      UserModel userModel = container.getComponent(UserModel.class);
      if ( userModel != null ) {
         userModel.load();
         System.out.println("Loaded user model");
      }
      ICollaborationManager cm = container.getComponent(ICollaborationManager.class);
      for (Registry registry : cmRegistries)
         cm.addRegistry(registry);
      System.out.println("Starting Collaboration Manager");
      cm.start();
      System.out.println("Program started");
   }

   public MutablePicoContainer getContainer () {
      return container;
   }
}
