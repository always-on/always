package edu.wpi.always.cm;

import java.util.*;

import com.google.common.collect.*;

import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;

public class ProposalBuilder implements BehaviorBuilder {

	private final ArrayList<PrimitiveBehavior> primitives = Lists.newArrayList();
	private final Plugin plugin;
	private BehaviorMetadataBuilder metadataBuilder = new BehaviorMetadataBuilder();

	public ProposalBuilder (Plugin plugin, FocusRequirement focus) {
		this.plugin = plugin;
		
		if(focus == FocusRequirement.Required)
			primitives.add(new FocusRequestBehavior());
	}

	public ProposalBuilder (FocusRequirement focus) {
		this(null, focus);
	}

	@Override
	public Behavior build () {
		return Behavior.newInstance(primitives);
	}

	private void intenalAdd (PrimitiveBehavior pb) {
		Resource r = pb.getResource();

		for (PrimitiveBehavior b : primitives) {
			if (b.getResource().equals(r)) {
				primitives.remove(b);
				break;
			}
		}

		primitives.add(pb);
	}

	public ProposalBuilder say (String text) {
		intenalAdd(new SpeechBehavior(text));
		return this;
	}

	public ProposalBuilder pluginAction (String actionName, Resource resource) {
		intenalAdd(new PluginSpecificBehavior(plugin, actionName, resource));
		return this;
	}

	public ProposalBuilder gazeAtUser () {
		intenalAdd(new FaceTrackBehavior());
		return this;
	}

	@Override
	public BehaviorMetadata getMetadata() {
		return metadataBuilder.build();
	}

	public void setMetadata(BehaviorMetadataBuilder metadata) {
		metadataBuilder = metadata;
	}
	
	public BehaviorMetadataBuilder metadataBuilder() {
		return metadataBuilder;
	}
}
