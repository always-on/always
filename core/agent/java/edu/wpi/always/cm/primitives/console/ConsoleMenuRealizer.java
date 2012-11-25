package edu.wpi.always.cm.primitives.console;

import com.google.gson.*;

import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;

public class ConsoleMenuRealizer extends
		SingleRunPrimitiveRealizer<MenuBehavior> {

	public ConsoleMenuRealizer(MenuBehavior params) {
		super(params);
	}

	@Override
	protected void singleRun() {
		Gson gson = new Gson();
		String json = gson.toJson(getParams().getItems());
		System.out.println("Menus for user: " + json);
	}

}
