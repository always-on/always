package edu.wpi.always.user.owl;

import java.util.List;
import org.joda.time.MonthDay;
import org.mindswap.pellet.*;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import com.clarkparsia.pellet.owlapiv3.*;
import com.clarkparsia.pellet.rules.builtins.BuiltInRegistry;
import com.clarkparsia.pellet.rules.builtins.GeneralFunction;
import com.clarkparsia.pellet.rules.builtins.GeneralFunctionBuiltIn;

public class OntologyImpl implements Ontology {

   private OWLOntologyManager manager;
   private OWLOntology ontology;
   private OWLDataFactory factory;
   private OWLReasoner reasoner;
   private PrefixManager pm;

   public OntologyImpl () {
      // incremental consistency options
      PelletOptions.USE_COMPLETION_QUEUE = true;
      PelletOptions.USE_INCREMENTAL_CONSISTENCY = true;
      PelletOptions.USE_SMART_RESTORE = false;
      //
      PelletOptions.USE_TRACING = true;
      BuiltInRegistry.instance.registerBuiltIn("my:gMonthDay",
            new GeneralFunctionBuiltIn(new GMonthDay()));
      reset();
   }

   @Override
   public void ensureConsistency () {
      try {
         PelletReasoner reasoner = (PelletReasoner) getReasoner();
         reasoner.refresh();
         reasoner.getKB().ensureConsistency(); 
      } catch ( org.mindswap.pellet.exceptions.InconsistentOntologyException e) {
         throw inconsistent(e);
      }
   }
   
   public static InconsistentOntologyException inconsistent (Throwable cause) {
      InconsistentOntologyException e = new InconsistentOntologyException();
      e.initCause(cause);
      return e;
   }
   
   @Override
   public void reset () {
      try {
         manager = OWLManager.createOWLOntologyManager();
         ontology = manager.createOntology(DOCUMENT_IRI);
         pm = new DefaultPrefixManager();
         factory = manager.getOWLDataFactory();
         reasoner = null;
         manager.addOntologyChangeListener(new OWLOntologyChangeListener() {

            @Override
            public void ontologiesChanged (
                  List<? extends OWLOntologyChange> changes)
                  throws OWLException {
               reasoner = null;
            }
         });
      } catch (OWLOntologyCreationException e) {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   public OWLOntologyManager getManager () {
      return manager;
   }

   @Override
   public OWLOntology getOntology () {
      return ontology;
   }

   @Override
   public OWLDataFactory getFactory () {
      return factory;
   }

   @Override
   public PrefixManager getPm () {
      return pm;
   }

   @Override
   public OWLReasoner getReasoner () {
      if ( reasoner == null ) {
         // reasoner = new StructuralReasoner(ontology, new
         // SimpleConfiguration(), BufferingMode.NON_BUFFERING);
         reasoner = PelletReasonerFactory.getInstance()
               .createReasoner(ontology);
      }
      return reasoner;
   }

   private static class GMonthDay implements GeneralFunction {

      @Override
      public boolean apply (ABox abox, Literal[] args) {
         int month = ((Number) args[1].getValue()).intValue();
         int day = ((Number) args[2].getValue()).intValue();
         args[0] = abox.addLiteral(ATermUtils.makeTypedLiteral(
               OntologyValue.XML_GMonthDay_FORMAT
                     .print(new MonthDay(month, day)),
               org.mindswap.pellet.utils.Namespaces.XSD + "gMonthDay"));
         return true;
      }

      @Override
      public boolean isApplicable (boolean[] boundPositions) {
         if ( boundPositions.length == 3 ) {
            return boundPositions[1] && boundPositions[2];// create the month
                                                          // day
         }
         return false;
      }
   }
}
