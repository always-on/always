package edu.wpi.always.cm;

import javax.swing.*;

import edu.wpi.always.cm.disco.*;
import edu.wpi.always.cm.disco.actions.*;
import edu.wpi.cetask.ShellWindow;
import edu.wpi.disco.*;

public class DiscoBootstrapper {
	
	public Disco bootstrap(Actor me, boolean startConsole) {
		return bootstrap(startConsole, me, new User("user"));
	}
	
	public Disco bootstrap(boolean startConsole, Actor me, Actor userActor) {
		Interaction interaction = new Interaction(me, userActor);
		interaction.setOk(false);
		Disco disco = interaction.getDisco();

		if (startConsole) new ConsoleWindow(interaction, 600, 500, 14);

		DiscoUtils.setAgendaInteraction(interaction.getExternal().getAgenda(),
				interaction);
		DiscoUtils.setAgendaInteraction(interaction.getSystem().getAgenda(),
				interaction);

		loadMainModels(disco);

		return disco;
	}

	private void loadMainModels(Disco disco) {
		loadModel(disco, "/resources/DemoTasks.xml");
		loadModel(disco, "/resources/taskModels/Knock.xml");
	}

	private void loadModel(Disco disco, String resourcePath) {
		new LoadModelFromResource(resourcePath).execute(disco);
	}
}
