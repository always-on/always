package edu.wpi.disco.rt.actions;


import edu.wpi.disco.*;
import edu.wpi.disco.rt.DiscoAction;

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
