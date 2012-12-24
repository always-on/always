package edu.wpi.always.test.cm;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.google.common.collect.*;

import edu.wpi.always.client.ClientPlugin;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;

public class ProposalBuilderFixture {

	@Test
	public void testRequired () {
		DummyPlugin plugin = new DummyPlugin();
		Behavior b = new ProposalBuilder(plugin, FocusRequirement.NotRequired)
						.say("Hello")
						.pluginAction("Meld", Resource.Hand)
						.gazeAtUser()
						.build();

		ArrayList<PrimitiveBehavior> l = Lists.newArrayList();
		l.add(new SpeechBehavior("Hello"));
		l.add(new PluginSpecificBehavior(plugin, "Meld", Resource.Hand));
		l.add(new FaceTrackBehavior());

		CompoundBehavior expected = new SimpleCompoundBehavior(l);
		
		assertEquals(expected, b.getInner());
	}

	@Test
	public void whenAddinRequireds_IfABehaviorForTheResourceAlreadyExists_JustOverrideThatWithTheNewOne () {
		Behavior b = new ProposalBuilder(new DummyPlugin(), FocusRequirement.NotRequired)
							.say("Hello")
							.gazeAtUser()
							.gazeAtUser()
							.say("Good morning")
							.build();

		ArrayList<PrimitiveBehavior> l = Lists.newArrayList();
		l.add(new SpeechBehavior("Good morning"));
		l.add(new FaceTrackBehavior());

		CompoundBehavior expected = new SimpleCompoundBehavior(l);
		
		assertEquals(expected, b.getInner());
	}

	public static class DummyPlugin implements ClientPlugin {

		@Override
		public void doAction (String actionName) {
		}

		@Override
		public void initInteraction () {
		}

		@Override
		public BehaviorBuilder updateInteraction (boolean lastProposalIsDone) {
			return null;
		}

		@Override
		public void endInteraction () {
		}

	}
}
