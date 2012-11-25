package edu.wpi.always.cm.disco.actions;


import edu.wpi.always.DiscoAction;
import edu.wpi.disco.Agenda.Plugin.Item;
import edu.wpi.disco.*;

public class ItemDone implements DiscoAction {

	private final Item item;

	public ItemDone (Item item) {
		this.item = item;
	}

	@Override
	public void execute (Disco disco) {
		disco.getInteraction().done(item.task.isUser(), item.task, item.contributes);
		disco.decomposeAll();
	}

}
