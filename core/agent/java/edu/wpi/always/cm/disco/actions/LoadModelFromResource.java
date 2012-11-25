package edu.wpi.always.cm.disco.actions;

import edu.wpi.always.*;
import edu.wpi.always.cm.disco.*;

import edu.wpi.disco.*;

public class LoadModelFromResource implements DiscoAction {

	private final String resourcePath;

	public LoadModelFromResource (String resourcePath) {
		this.resourcePath = resourcePath;

	}

	@Override
	public void execute (Disco disco) {
		DiscoDocumentSet set = DiscoUtils.loadDocumentFromResource(resourcePath);
		
		disco.load(null, set.getMainDocument(), set.getProperties(), set.getTranslateProperties());
	}


}
