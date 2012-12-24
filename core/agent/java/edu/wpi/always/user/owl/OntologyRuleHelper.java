package edu.wpi.always.user.owl;

import org.semanticweb.owlapi.model.*;
import java.util.*;

public class OntologyRuleHelper extends OntologyHelper {

   public OntologyRuleHelper (Ontology ont) {
      super(ont);
   }

   public SWRLVariable getSWRLVariable (String name) {
      return getFactory().getSWRLVariable(getPm().getIRI(toIRIName(name)));
   }

   public SWRLObjectPropertyAtom getSWRLObjectPropertyAtom (
         OWLObjectPropertyExpression property, SWRLIArgument individual,
         SWRLIArgument value) {
      return getFactory()
            .getSWRLObjectPropertyAtom(property, individual, value);
   }

   public SWRLDataPropertyAtom getSWRLDataPropertyAtom (
         OWLDataPropertyExpression property, SWRLIArgument individual,
         SWRLDArgument value) {
      return getFactory().getSWRLDataPropertyAtom(property, individual, value);
   }

   public SWRLClassAtom getSWRLClassAtom (OWLClassExpression predicate,
         SWRLIArgument arg) {
      return getFactory().getSWRLClassAtom(predicate, arg);
   }

   public SWRLSameIndividualAtom getSWRLSameIndividualAtom (SWRLIArgument arg0,
         SWRLIArgument arg1) {
      return getFactory().getSWRLSameIndividualAtom(arg0, arg1);
   }

   public SWRLDifferentIndividualsAtom getSWRLDifferentIndividualsAtom (
         SWRLIArgument arg0, SWRLIArgument arg1) {
      return getFactory().getSWRLDifferentIndividualsAtom(arg0, arg1);
   }

   public SWRLRule getSWRLRule (Set<? extends SWRLAtom> antecedent,
         Set<? extends SWRLAtom> consequent) {
      return getFactory().getSWRLRule(antecedent, consequent);
   }

   public SWRLRule getSWRLRule (SWRLAtom antecedent,
         Set<? extends SWRLAtom> consequent) {
      return getSWRLRule(Collections.singleton(antecedent), consequent);
   }

   public SWRLRule getSWRLRule (Set<? extends SWRLAtom> antecedent,
         SWRLAtom consequent) {
      return getSWRLRule(antecedent, Collections.singleton(consequent));
   }

   public SWRLRule getSWRLRule (SWRLAtom antecedent, SWRLAtom consequent) {
      return getSWRLRule(antecedent, Collections.singleton(consequent));
   }

   public SWRLRule isARule (OWLObjectProperty antecedentProperty,
         OWLObjectProperty consequentProperty) {
      SWRLVariable varA = getSWRLVariable("varA");
      SWRLVariable varB = getSWRLVariable("varB");
      return getSWRLRule(
            getSWRLObjectPropertyAtom(antecedentProperty, varA, varB),
            getSWRLObjectPropertyAtom(consequentProperty, varA, varB));
   }

   public SWRLRule isARule (OWLObjectProperty antecedentProperty,
         OWLClassExpression antecedentClass,
         OWLObjectProperty consequentProperty) {
      SWRLVariable varA = getSWRLVariable("varA");
      SWRLVariable varB = getSWRLVariable("varB");
      Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
      antecedent.add(getSWRLObjectPropertyAtom(antecedentProperty, varA, varB));
      antecedent.add(getSWRLClassAtom(antecedentClass, varB));
      return getSWRLRule(antecedent,
            getSWRLObjectPropertyAtom(consequentProperty, varA, varB));
   }

   public SWRLRule isARule (OWLObjectProperty antecedentProperty,
         OWLClassExpression consequentClass) {
      SWRLVariable varA = getSWRLVariable("varA");
      SWRLVariable varB = getSWRLVariable("varB");
      return getSWRLRule(
            getSWRLObjectPropertyAtom(antecedentProperty, varA, varB),
            getSWRLClassAtom(consequentClass, varB));
   }

   public SWRLRule hasARule (OWLObjectProperty antecedentProperty,
         OWLObjectProperty consequentProperty) {
      SWRLVariable varA = getSWRLVariable("varA");
      SWRLVariable varB = getSWRLVariable("varB");
      return getSWRLRule(
            getSWRLObjectPropertyAtom(antecedentProperty, varA, varB),
            getSWRLObjectPropertyAtom(consequentProperty, varB, varA));
   }

   /**
    * If B is property of A and C is property of B and A is not C then C is
    * property of A
    * 
    * @param property
    * @return
    */
   public SWRLRule isTransitiveExclusiveRule (OWLObjectProperty property) {
      SWRLVariable varA = getSWRLVariable("varA");
      SWRLVariable varB = getSWRLVariable("varB");
      SWRLVariable varC = getSWRLVariable("varC");
      Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
      antecedent.add(getSWRLObjectPropertyAtom(property, varA, varB));
      antecedent.add(getSWRLObjectPropertyAtom(property, varB, varC));
      antecedent.add(getSWRLDifferentIndividualsAtom(varA, varC));
      return getSWRLRule(antecedent,
            getSWRLObjectPropertyAtom(property, varA, varC));
   }

   public SWRLRule isBidirectionalRule (OWLObjectProperty property) {
      SWRLVariable varA = getSWRLVariable("varA");
      SWRLVariable varB = getSWRLVariable("varB");
      return getSWRLRule(getSWRLObjectPropertyAtom(property, varA, varB),
            getSWRLObjectPropertyAtom(property, varB, varA));
   }
}
