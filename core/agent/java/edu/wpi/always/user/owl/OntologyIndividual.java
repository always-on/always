package edu.wpi.always.user.owl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import edu.wpi.always.*;

public class OntologyIndividual {
   
   // synchronization added to allow multiple thread access

   private final OWLNamedIndividual individual;
   private final OntologyHelper helper;

   // for logging
   
   private enum LogObject { USER, EVENT }
   private enum LogMode { SET, ADD, DELETE }
   
   private String getLogObject () {
      return ( equals(((OntologyUserModel) Always.THIS.getUserModel()).getUser()) ? LogObject.USER : 
         hasSuperclass(OntologyCalendar.EVENT_CLASS) ? LogObject.EVENT : this )
         .toString();
   }
   
   public OntologyIndividual (Ontology ontology, OWLNamedIndividual individual) {
      helper = new OntologyHelper(ontology);
      this.individual = individual;
   }

   public OWLNamedIndividual getOWLIndividual () {
      return individual;
   }

   public void addSuperclass (String className) {
      synchronized (OntologyUserModel.LOCK) {
         helper.addAxiom(helper.getFactory().getOWLClassAssertionAxiom(
               helper.getClass(className), individual));
      }
   }

   public void removeSuperclass (String className) {
      synchronized (OntologyUserModel.LOCK) {
         helper.removeAxiom(helper.getFactory().getOWLClassAssertionAxiom(
               helper.getClass(className), individual));
      }
   }

   public void setDataProperty (String property, OntologyValue value) {
      synchronized (OntologyUserModel.LOCK) {
         setDataProperty(helper.getDataProperty(property), value);
      }
   }

   public void setDataProperty (OWLDataProperty property, OntologyValue value) {
      synchronized (OntologyUserModel.LOCK) {
         List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
         Set<OWLLiteral> oldValues = getDataPropertyValues(property);
         for (OWLLiteral oldValue : oldValues) {
            helper.removeAxiom(
                  changes,
                  helper.getFactory().getOWLDataPropertyAssertionAxiom(property,
                        individual, oldValue));
         }
         helper.applyChanges(changes);
         if ( value != null )
            addDataProperty(property, value, LogMode.SET);
      }
   }

   public void addDataProperty (OWLDataProperty property, OntologyValue value) {
      addDataProperty(property, value, LogMode.ADD);
   }

   private void addDataProperty (OWLDataProperty property, OntologyValue value, LogMode mode) {
      synchronized (OntologyUserModel.LOCK) {
         if ( value != null ) {
            Logger.logEvent(Logger.Event.MODEL, mode, getLogObject(), property, value);
            helper.addAxiom(helper.getFactory().getOWLDataPropertyAssertionAxiom(
                  property, individual, value.getOWLLiteral()));
         }
      }
   }
   
   public Set<OWLLiteral> getDataPropertyValues (OWLDataProperty property) {
      synchronized (OntologyUserModel.LOCK) {
         return helper.getReasoner().getDataPropertyValues(individual, property);
      }
   }

   public OntologyValue getDataPropertyValue (String property) {
      synchronized (OntologyUserModel.LOCK) {
         return getDataPropertyValue(helper.getDataProperty(property));
      }
   }

   public OntologyValue getDataPropertyValue (OWLDataProperty property) {
      synchronized (OntologyUserModel.LOCK) {
         Set<OWLLiteral> values = getDataPropertyValues(property);
         if ( values.size() == 0 )
            return new OntologyValue(null);
         if ( values.size() != 1 )
            throw OntologyImpl.inconsistent(new RuntimeException("The OWLIndividual " + individual
                  + " has more than one value for the property " + property));
         return new OntologyValue(values.iterator().next());
      }
   }

   
   public void setObjectProperty (String property, OntologyIndividual value) {
      synchronized (OntologyUserModel.LOCK) {
         setObjectProperty(helper.getObjectProperty(property), value);
      }
   }

   public void setObjectProperty (OWLObjectProperty property, OntologyIndividual value) {
      synchronized (OntologyUserModel.LOCK) {
         List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
         Set<OWLNamedIndividual> oldValues = getObjectPropertyValues(property);
         for (OWLNamedIndividual oldValue : oldValues) {
            helper.removeAxiom(
                  changes,
                  helper.getFactory().getOWLObjectPropertyAssertionAxiom(property,
                        individual, oldValue));
         }
         if ( value != null )
            addObjectProperty(property, value, LogMode.SET);
         helper.applyChanges(changes);
      }
   }

   public void addObjectProperty (String property, OntologyIndividual value) {
      synchronized (OntologyUserModel.LOCK) {
         addObjectProperty(helper.getObjectProperty(property), value);
      }
   }

   public void addObjectProperty (OWLObjectProperty property, OntologyIndividual value) {
      addObjectProperty(property, value, LogMode.ADD);
   }
   
   private void addObjectProperty (OWLObjectProperty property, OntologyIndividual value, LogMode mode) {
      synchronized (OntologyUserModel.LOCK) {
         if ( value != null ) {
            Logger.logEvent(Logger.Event.MODEL, mode, getLogObject(), property, value);
            helper.addAxiom(helper.getFactory()
                  .getOWLObjectPropertyAssertionAxiom(property, individual,
                        value.getOWLIndividual()));
         }
      }
   }
   
   public Set<OWLNamedIndividual> getObjectPropertyValues (String property) {
      synchronized (OntologyUserModel.LOCK) {
         return getObjectPropertyValues(helper.getObjectProperty(property));
      }
   }

   public Set<OWLNamedIndividual> getObjectPropertyValues (OWLObjectProperty property) {
      synchronized (OntologyUserModel.LOCK) {
         return helper.getReasoner().getObjectPropertyValues(individual, property)
               .getFlattened();
      }
   }

   public OntologyIndividual getObjectPropertyValue (String property) {
      synchronized (OntologyUserModel.LOCK) {
         return getObjectPropertyValue(helper.getObjectProperty(property));
      }
   }

   public OntologyIndividual getObjectPropertyValue (OWLObjectProperty property) {
      synchronized (OntologyUserModel.LOCK) {
         Set<OWLNamedIndividual> values = getObjectPropertyValues(property);
         if ( values.size() == 0 )
            return null;
         if ( values.size() != 1 )
            throw OntologyImpl.inconsistent(new RuntimeException("The OWLIndividual " + individual
                  + " has more than one value for the property " + property));
         return new OntologyIndividual(helper.getOntologyDataObject(), values
               .iterator().next());
      }
   }

   public boolean hasSuperclass (String className) {
      synchronized (OntologyUserModel.LOCK) {
         return helper.getReasoner().getTypes(individual, false).getFlattened()
               .contains(helper.getClass(className));
      }
   }

   public void delete () {
      synchronized (OntologyUserModel.LOCK) {
         Logger.logEvent(Logger.Event.MODEL, LogMode.DELETE, getLogObject(), 
               getDataPropertyValue(OntologyCalendar.UUID_PROPERTY));
         OWLEntityRemover remover = new OWLEntityRemover(helper.getManager(),
               Collections.singleton(helper.getOntology()));
         individual.accept(remover);
         helper.applyChanges(remover.getChanges());
      }
   }
   
   @Override
   public int hashCode () {
      final int prime = 31;
      int result = 1;
      result = prime * result
         + ((individual == null) ? 0 : individual.hashCode());
      return result;
   }

   @Override
   public boolean equals (Object obj) {
      if ( this == obj )
         return true;
      if ( obj == null )
         return false;
      if ( getClass() != obj.getClass() )
         return false;
      OntologyIndividual other = (OntologyIndividual) obj;
      if ( individual == null ) {
         if ( other.individual != null )
            return false;
      } else if ( !individual.equals(other.individual) )
         return false;
      return true;
   }

   @Override
   public String toString () { return individual.toString(); }
}
