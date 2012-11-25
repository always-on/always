package edu.wpi.always.user.owl;

import java.util.*;

import org.joda.time.*;
import org.mindswap.pellet.*;
import org.mindswap.pellet.utils.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.*;

import com.clarkparsia.pellet.owlapiv3.*;
import com.clarkparsia.pellet.rules.builtins.*;

public class OntologyImpl implements Ontology{

	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private OWLDataFactory factory;
	private OWLReasoner reasoner;
	private PrefixManager pm;


	public OntologyImpl(){
		try{
			BuiltInRegistry.instance.registerBuiltIn("my:gMonthDay", new GeneralFunctionBuiltIn(new GMonthDay()));
			manager = OWLManager.createOWLOntologyManager();
			ontology = manager.createOntology(DOCUMENT_IRI);
			pm = new DefaultPrefixManager();
			factory = manager.getOWLDataFactory();
			manager.addOntologyChangeListener( new OWLOntologyChangeListener() {
				@Override
				public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
					reasoner = null;
				}
			} );

		} catch(OWLOntologyCreationException e){
			e.printStackTrace();
		}

	}

	@Override
	public OWLOntologyManager getManager() {
		return manager;
	}


	@Override
	public OWLOntology getOntology() {
		return ontology;
	}


	@Override
	public OWLDataFactory getFactory() {
		return factory;
	}


	@Override
	public PrefixManager getPm() {
		return pm;
	}


	@Override
	public OWLReasoner getReasoner() {
		if(reasoner==null){
			//reasoner = new StructuralReasoner(ontology, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
			reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		}
		return reasoner;
	}



	private static class GMonthDay implements GeneralFunction {

		public boolean apply(ABox abox, Literal[] args) {
			int month = ((Number)args[1].getValue()).intValue();
			int day = ((Number)args[2].getValue()).intValue();
			args[0] = abox.addLiteral(ATermUtils.makeTypedLiteral(OntologyValue.XML_GMonthDay_FORMAT.print(new MonthDay(month, day)),
					org.mindswap.pellet.utils.Namespaces.XSD+"gMonthDay"));
			return true;
		}

		public boolean isApplicable(boolean[] boundPositions) {
			if(boundPositions.length == 3){
				return boundPositions[1] && boundPositions[2];//create the month day
			}
			return false;
		}
	}



}
