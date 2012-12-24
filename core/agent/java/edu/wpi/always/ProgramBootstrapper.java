package edu.wpi.always;

import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.owl.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.picocontainer.*;
import org.picocontainer.behaviors.OptInCaching;
import java.util.*;

public class ProgramBootstrapper {

   private final MutablePicoContainer pico;

   public ProgramBootstrapper (boolean logToConsole) {
      if ( logToConsole )
         BasicConfigurator.configure();
      else
         BasicConfigurator.configure(new NullAppender());
      pico = new PicoBuilder().withBehaviors(new OptInCaching())
            .withConstructorInjection().build();
      pico.addComponent(pico);
   }

   private final List<SimpleRegistry> picoRegistries = new ArrayList<SimpleRegistry>();
   private final List<OntologyRegistry> ontologyRegistries = new ArrayList<OntologyRegistry>();

   public void addRegistry (Registry registry) {
      if ( registry instanceof SimpleRegistry )
         picoRegistries.add((SimpleRegistry) registry);
      if ( registry instanceof OntologyRegistry )
         ontologyRegistries.add((OntologyRegistry) registry);
   }

   private final List<Registry> cmRegistries = new ArrayList<Registry>();

   public void addCMRegistry (Registry registry) {
      cmRegistries.add(registry);
   }

   public void start () {
      for (SimpleRegistry registry : picoRegistries)
         registry.register(pico);
      OntologyRuleHelper helper = pico.getComponent(OntologyRuleHelper.class);
      for (OntologyRegistry registry : ontologyRegistries)
         registry.register(helper);
      UserModel userModel = pico.getComponent(UserModel.class);
      if ( userModel != null ) {
         userModel.load();
         System.out.println("Loaded user model");
      }
      ICollaborationManager cmBootstrapper = pico
            .getComponent(ICollaborationManager.class);
      for (Registry registry : cmRegistries)
         cmBootstrapper.addRegistry(registry);
      System.out.println("Starting Collaboration Manager");
      cmBootstrapper.start();
      System.out.println("Program started");
   }

   public MutablePicoContainer getContainer () {
      return pico;
   }
}
