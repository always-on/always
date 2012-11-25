package edu.wpi.always.cm.disco;

import java.util.*;

import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.Agenda.Plugin;
import edu.wpi.disco.plugin.*;

public class ProposeShouldNotPlanSpecificPlugin extends Plugin {

	private ProposeShouldNotPlugin innerPlugin;
	private final int priority2;
	private final Actor actor;

	public ProposeShouldNotPlanSpecificPlugin (Actor actor, Agenda agenda, int priority) {
		agenda.super(priority);
		this.actor = actor;
		priority2 = priority;

		tryCreateInnerPluginIfNotCreatedYet();
	}

	private void tryCreateInnerPluginIfNotCreatedYet () {
		if (innerPlugin != null)
			return;

		Agenda agendaInstanceNeverToBeUsed = DiscoUtils.createEmptyAgendaFor(actor);

		if (agendaInstanceNeverToBeUsed != null)
			innerPlugin = new ProposeShouldNotPlugin(agendaInstanceNeverToBeUsed, priority2);
	}

	@Override
	public List<Item> apply (Plan plan) {
		tryCreateInnerPluginIfNotCreatedYet();

		if (innerPlugin != null && getAgenda().getDisco().getFocus() == plan) {
			return innerPlugin.apply();
		}

		return new ArrayList<Item>();
	}

}
