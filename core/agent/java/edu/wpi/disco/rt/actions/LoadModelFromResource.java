package edu.wpi.disco.rt.actions;

import edu.wpi.disco.Disco;
import edu.wpi.disco.rt.*;

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
