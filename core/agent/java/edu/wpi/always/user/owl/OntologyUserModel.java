package edu.wpi.always.user.owl;

import edu.wpi.always.user.UserModel;
import org.picocontainer.annotations.Bind;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.*;
import java.io.*;
import java.lang.annotation.*;
import java.util.*;

public class OntologyUserModel implements UserModel {

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ ElementType.FIELD, ElementType.PARAMETER })
   @Bind
   public @interface UserOntologyLocation {
   }

   private final String userName;
   private final OntologyIndividual user;
   private final OntologyHelper ontology;
   private final File userDataFile;
   private final OntologyCalendar calendar;
   private final OntologyPeopleManager peopleManager;
   private final OntologyPlaceManager placeManager;

   public OntologyUserModel (@UserName
         String userName, OntologyHelper ontology, @UserOntologyLocation
         File userDataFile, OntologyCalendar calendar,
         OntologyPeopleManager peopleManager, OntologyPlaceManager placeManager) {
      this.userName = userName;
      this.user =  ontology.getNamedIndividual(userName);
      if ( !user.hasSuperclass(OntologyPerson.USER_CLASS) ) {
         user.addSuperclass(OntologyPerson.USER_CLASS);
         peopleManager.addPerson(userName, null, null);
      }
      this.ontology = ontology;
      this.userDataFile = userDataFile;
      this.calendar = calendar;
      this.peopleManager = peopleManager;
      this.placeManager = placeManager;
   }

   @Override
   public OntologyCalendar getCalendar () {
      return calendar;
   }

   @Override
   public OntologyPeopleManager getPeopleManager () {
      return peopleManager;
   }

   @Override
   public OntologyPlaceManager getPlaceManager () {
      return placeManager;
   }

   @Override
   public String getUserName () {
      return userName;
   }
   
   @Override
   public String getProperty (String property) {
      return user.getDataPropertyValue(property).asString();
   }
   
   @Override
   public void setProperty (String property, String value) {
      user.setDataProperty(property, value == null ? null : ontology.getLiteral(value));
   }
    
   public void addAxiomsFromInputStream (InputStream stream) {
      ontology.addAxiomsFromInputStream(stream);
   }
   
   private static final Set<AxiomType<?>> types = new HashSet<AxiomType<?>>();
   static {
      types.add(AxiomType.CLASS_ASSERTION);
      types.add(AxiomType.DIFFERENT_INDIVIDUALS);
      types.add(AxiomType.DATA_PROPERTY_ASSERTION);
      types.add(AxiomType.OBJECT_PROPERTY_ASSERTION);
   }

   
   @Override
   public void save () {
      try {
         OWLOntologyManager manager = ontology.getManager();
         OWLOntology userOntology = manager.createOntology(IRI.create("file:"
            + userDataFile.getPath()));
         LinkedList<AddAxiom> userAxioms = new LinkedList<AddAxiom>();
         for (OWLAxiom ax : ontology.getOntology().getAxioms()) {
            if ( types.contains(ax.getAxiomType()) )
               userAxioms.add(new AddAxiom(userOntology, ax));
         }
         manager.applyChanges(userAxioms);
         System.out.println("Saving user ontology to: "+userDataFile);
         manager.saveOntology(userOntology,
               new OWLFunctionalSyntaxOntologyFormat(), new FileOutputStream(
                     userDataFile));
         manager.removeOntology(userOntology);
      } catch (OWLOntologyStorageException e) {
         e.printStackTrace();
      } catch (OWLOntologyCreationException e) {
         e.printStackTrace();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void load () {
      if ( userDataFile != null && userDataFile.exists() ) {
         System.out.println("Loading user ontology from: "+userDataFile);
         ontology.addAxiomsFromFile(userDataFile);
      } else {
         peopleManager.getUser();// force the user's OWLIndividual to be created
      }
   }
   
   @Override
   public String toString () { return userName; }
}
