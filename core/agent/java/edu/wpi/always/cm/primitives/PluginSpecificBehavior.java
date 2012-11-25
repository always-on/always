package edu.wpi.always.cm.primitives;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.realizer.*;

public class PluginSpecificBehavior extends PrimitiveBehavior {

	private final Plugin plugin;
	private final String actionName;
	private final Resource resource;

	public PluginSpecificBehavior (Plugin plugin, String actionName, Resource resource) {
		this.plugin = plugin;
		this.actionName = actionName;
		this.resource = resource;
	}

	@Override
	public Resource getResource () {
		return resource;
	}

	@Override
	public boolean equals (Object o) {
		if (o == this)
			return true;

		if (!(o instanceof PluginSpecificBehavior))
			return false;

		PluginSpecificBehavior theOther = (PluginSpecificBehavior) o;

		if (!this.plugin.equals(theOther.plugin))
			return false;

		if (!this.resource.equals(theOther.resource))
			return false;

		if (!this.actionName.equals(theOther.actionName))
			return false;

		return true;
	}

	@Override
	public int hashCode () {
		int result;

		result = plugin.hashCode();
		result = 31 * result + resource.hashCode();
		result = 31 * result + actionName.hashCode();

		return result;
	}

	public String getActionName () {
		return actionName;
	}

	public Plugin getPlugin () {
		return plugin;
	}

	@Override
	public String toString() {
		return actionName + "@" + plugin + " on <" + resource + ">";
	}

}
