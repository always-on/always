package edu.wpi.always.cm.disco.actions;


import edu.wpi.always.DiscoAction;
import edu.wpi.disco.*;

public class LoadModel implements DiscoAction {

	private final String modelPath;

	public LoadModel (String modelPath) {
		this.modelPath = modelPath;
	}

	@Override
	public void execute (Disco disco) {
		disco.load(modelPath);
	}

}
