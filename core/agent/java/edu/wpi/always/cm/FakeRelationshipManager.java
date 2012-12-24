package edu.wpi.always.cm;

import org.w3c.dom.*;

import edu.wpi.always.*;
import edu.wpi.always.test.*;
import edu.wpi.disco.rt.*;


public class FakeRelationshipManager implements IRelationshipManager {

	@Override
	public DiscoDocumentSet getLatestPlan() {
		return DiscoUtils.loadDocumentFromResource("/resources/today.xml");
	}

	@Override
	public void afterInteraction(DiscoSynchronizedWrapper disco,
			float closeness, int time) {
	}

	@Override
	public Document getLatestPlanInDoc() {
		// TODO Auto-generated method stub
		return null;
	}

}
