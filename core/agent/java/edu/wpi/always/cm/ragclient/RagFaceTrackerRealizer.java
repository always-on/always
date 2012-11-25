package edu.wpi.always.cm.ragclient;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;

public class RagFaceTrackerRealizer extends
		PrimitiveRealizerImplBase<FaceTrackBehavior> {
	public static final long FACE_TRACK_TIME_DAMPENING = 1000;

	private final RagClientProxy proxy;
	private final EmotiveFacePerceptor perceptor;
	
	private AgentTurn lastDir;
	
	private AgentTurn nextDir;
	private long lastNewNext = 0;

	public RagFaceTrackerRealizer(FaceTrackBehavior params,
			EmotiveFacePerceptor perceptor, RagClientProxy proxy) {
		super(params);
		this.proxy = proxy;
		this.perceptor = perceptor;
	}

	@Override
	public void run() {
		EmotiveFacePerception perception = perceptor.getLatest();
		if (perception != null)
		{
			AgentTurn dir = RagGazeRealizer.translateToAgentTurn(perception.getLocation());
			if(dir != lastDir) {
				if(dir!=nextDir){
					lastNewNext = System.currentTimeMillis();
					nextDir = dir;
				}
				if(System.currentTimeMillis()-lastNewNext>FACE_TRACK_TIME_DAMPENING){
					System.out.println(dir+" - "+perception.getLocation().getX());
					proxy.gaze(dir);
					lastDir = dir;
				}
			}
			fireDoneMessage();
		}
	}

}
