package edu.wpi.always.user.owl;

import edu.wpi.always.Always;
import edu.wpi.always.user.owl.*;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import java.io.FileOutputStream;

/**
 * A program for generating ontologies
 * 
 * @author mwills
 */
public class OntologyFactoryMain {

   public static void main (String[] args) {
      Always always = new Always(true);
      // always.addRegistry(new OntologyUserRegistry("Test User",
      //      "ontology/OntologyTmp.owl"));
      always.start();
      OntologyRuleHelper ontology = always.getContainer().getComponent(
            OntologyRuleHelper.class);
      OntologyClass clazz = new OntologyClass(ontology.getOntologyDataObject(),
            ontology.getClass("MyClass"));
      OWLDataProperty property = clazz.declareDataProperty(
            ontology.getDataProperty("prop"), OWL2Datatype.XSD_BOOLEAN,
            OntologyCardinality.AT_MOST_ONE);
      OntologyIndividual i = ontology.getNamedIndividual("myindividual");
      i.setDataProperty("MyProp", ontology.getLiteral("abcd"));
      SWRLDataPropertyAtom atom = ontology.getSWRLDataPropertyAtom(
            property,
            ontology.getSWRLVariable("MyVar"),
            ontology.getFactory().getSWRLLiteralArgument(
                  ontology.getLiteral("MyStringValue").getOWLLiteral()));
      ontology.addAxiom(ontology.getSWRLRule(atom, atom));
      // i.setDataProperty("MyProp", ontology.getLiteral("978-266-9697"));
      ontology.getReasoner().getDataPropertyValues(i.getOWLIndividual(),
            ontology.getDataProperty("MyProp"));
      try {
         ontology.getManager().saveOntology(ontology.getOntology(),
               new OWLFunctionalSyntaxOntologyFormat(),
               new FileOutputStream("OntologyTmp.owl"));
         System.out.println("Saved file");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
