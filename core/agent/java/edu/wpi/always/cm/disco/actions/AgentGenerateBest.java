package edu.wpi.always.cm.disco.actions;

import java.util.*;


import edu.wpi.always.DiscoFunc;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.Agenda.Plugin.Item;

public class AgentGenerateBest implements DiscoFunc<Item> {

	private final Plan plan;

	public AgentGenerateBest (Plan plan) {
		this.plan = plan;
	}

	@Override
	public Item execute (Disco disco) {
		HashMap<Task, Item> items = new HashMap<Task, Item>();
		
		plan.decomposeAll();
		Interaction interaction = disco.getInteraction();
		((Agenda)interaction.getSystem().getAgenda()).visit(plan, items, null);
		
		int maxPriority = Integer.MIN_VALUE;
		Item best = null;
		
		for(Item it : items.values()) {
			if(it.getPriority() > maxPriority) {
				maxPriority = it.getPriority();
				best = it;
			}
		}
		
		return best;
	}

}
