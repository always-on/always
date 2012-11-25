package edu.wpi.always.cm;

import javax.swing.*;

import edu.wpi.always.cm.disco.*;
import edu.wpi.always.cm.disco.actions.*;
import edu.wpi.disco.*;

public class DiscoBootstrapper {
	private DiscoConsolePanel buildPanel(){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		DiscoConsolePanel panel = new DiscoConsolePanel();
		frame.add(panel);
		frame.setSize(600, 500);
		frame.setVisible(true);
		return panel;
	}
	
	public Disco bootstrap(Actor me, boolean startConsole) {
		return bootstrap(startConsole, me, new User("user"));
	}
	
	public Disco bootstrap(boolean startConsole, Actor me, Actor userActor) {
		Interaction interaction = new Interaction(me, userActor);
		interaction.setOk(false);
		Disco disco = interaction.getDisco();

		if (startConsole){
			final DiscoConsolePanel panel = buildPanel();
			Console console = new Console(null, interaction);
			console.setReader(panel.getInput());
			interaction.setConsole(console);
			console.setOut(panel.getOutput());
			console.init(interaction.getDisco());

			interaction.start(true);
		}

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
