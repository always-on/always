package edu.wpi.always.user.owl;

import java.io.*;
import java.util.*;
import org.mindswap.pellet.KnowledgeBase;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.*;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import edu.wpi.always.user.UserModelBase;
import edu.wpi.disco.rt.util.Utils;

public class OntologyUserModel extends UserModelBase {

   private OntologyIndividual user;

   private File userDataFile;

   private final OntologyHelper ontology;

   private final OntologyCalendar calendar;

   private final OntologyPeopleManager peopleManager;

   private final OntologyPlaceManager placeManager;

   public File getUserDataFile () {
      return userDataFile;
   }

   public void setUserDataFile (File userDataFile) {
      this.userDataFile = userDataFile;
   };

   public OntologyUserModel (OntologyHelper ontology,
         OntologyCalendar calendar, OntologyPeopleManager peopleManager,
         OntologyPlaceManager placeManager) {
      this.ontology = ontology;
      this.calendar = calendar;
      this.peopleManager = peopleManager;
      this.placeManager = placeManager;
      peopleManager.setUserModel(this);
      // partially set for testing and starting with empty user model
      user = ontology.getNamedIndividual("User");
   }

   @Override
   public synchronized void reset () {
      Utils.lnprint(System.out, "Resetting user model!");
      super.reset();
      ontology.reset();
      user = ontology.getNamedIndividual("User"); 
   }

   @Override
   public void setUserName (String userName) {
      if ( this.userName.isEmpty() ) {
         this.userName = userName;
         int space = userName.indexOf(' ');
         userFirstName = space < 0 ? userName : userName.substring(0, space);
         user = ontology.getNamedIndividual(userName);
         user.setDataProperty(OntologyPerson.NAME_PROPERTY,
               ontology.getLiteral(userName));
         if ( !user.hasSuperclass(OntologyPerson.USER_CLASS) ) {
            user.addSuperclass(OntologyPerson.USER_CLASS);
            peopleManager.addPerson(userName);
         }
         saveIf();
      } else
         throw new UnsupportedOperationException(
               "User model already has name: " + this.userName);
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
   public String getProperty (String property) {
      OntologyValue value = user.getDataPropertyValue(property);
      return value == null ? null : value.asString();
   }

   @Override
   public void setProperty (String property, String value) {
      user.setDataProperty(property,
            value == null ? null : ontology.getLiteral(value));
      saveIf();
   }

   @Override
   public int getIntProperty (String property) {
      return user.getDataPropertyValue(property).asInteger();
   }

   @Override
   public void setProperty (String property, int value) {
      user.setDataProperty(property, ontology.getLiteral(value));
      saveIf();
   }

   @Override
   public long getLongProperty (String property) {
      return user.getDataPropertyValue(property).asLong();
   }

   @Override
   public void setProperty (String property, long value) {
      user.setDataProperty(property, ontology.getLiteral(value));
      saveIf();
   }

   @Override
   public double getDoubleProperty (String property) {
      return user.getDataPropertyValue(property).asDouble();
   }

   @Override
   public void setProperty (String property, double value) {
      user.setDataProperty(property, ontology.getLiteral(value));
      saveIf();
   }

   @Override
   public void setProperty (String property, boolean value) {
      user.setDataProperty(property, ontology.getLiteral(value));
      saveIf();
   }

   @Override
   public boolean isProperty (String property) {
      return user.getDataPropertyValue(property).asBoolean();
   }

   public void addAxioms (InputStream stream) {
      addAxioms(stream, false);
   }

   public void addAxioms (InputStream stream, boolean inhibitSave) {
      ontology.addAxioms(stream);
      if ( !inhibitSave )
         saveIf();
   }

   public void addAxioms (File file) {
      ontology.addAxioms(file);
      saveIf();
   }

   private static final Set<AxiomType<?>> types = new HashSet<AxiomType<?>>();
   static {
      types.add(AxiomType.CLASS_ASSERTION);
      types.add(AxiomType.DIFFERENT_INDIVIDUALS);
      types.add(AxiomType.DATA_PROPERTY_ASSERTION);
      types.add(AxiomType.OBJECT_PROPERTY_ASSERTION);
   }

   @Override
   public synchronized void save () {
      if ( userName.isEmpty() ) return; // don't write out bad file
      try (FileOutputStream output = new FileOutputStream(userDataFile)) {
         OWLOntologyManager manager = ontology.getManager();
         OWLOntology userOntology = manager.createOntology(IRI.create("UserModel"));
         LinkedList<AddAxiom> userAxioms = new LinkedList<AddAxiom>();
         for (OWLAxiom ax : ontology.getOntology().getAxioms()) {
            if ( types.contains(ax.getAxiomType()) )
               userAxioms.add(new AddAxiom(userOntology, ax));
         }
         manager.applyChanges(userAxioms);
         manager.saveOntology(userOntology, new OWLFunctionalSyntaxOntologyFormat(), output);
         manager.removeOntology(userOntology);
      } catch (OWLOntologyStorageException | OWLOntologyCreationException | IOException e) {
         edu.wpi.cetask.Utils.rethrow(e); 
      }
   }

   @Override
   public synchronized void load () {
      if ( userDataFile != null && userDataFile.canRead() ) {
         Utils.lnprint(System.out, "Starting user model: " + userDataFile);
         ontology.addAxioms(userDataFile);
         ensureConsistency();
         Set<OWLNamedIndividual> userClass = ontology
               .getAllOfClass(OntologyPerson.USER_CLASS);
         if ( !userClass.isEmpty() ) {
            setUserName(new OntologyIndividual(
                  ontology.getOntologyDataObject(), userClass.iterator().next())
                  .getDataPropertyValue(OntologyPerson.NAME_PROPERTY)
                  .asString());
            Utils.lnprint(System.out, "User name: " + getUserName());
            System.out.println();  // for always-disco
         } else
            Utils.lnprint(System.out, "Loaded user model is empty!");
      } else Utils.lnprint(System.out, "Starting with no user model!");
      super.load(); // initialize start time and increment session count
   }

   @Override
   public synchronized void ensureConsistency () {
      ontology.getOntologyDataObject().ensureConsistency();
   }
   
   @Override
   public String toString () {
      return "[UserModel:" + userName + "]";
   }

}
