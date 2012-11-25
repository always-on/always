package edu.wpi.always.cm.perceptors;

import org.joda.time.*;

public class MenuPerceptionImpl implements MenuPerception {

	private final DateTime t;
	private final String menu;
	
	
	public MenuPerceptionImpl(String selectedMenu, DateTime ts) {
		this.t = ts;
		this.menu = selectedMenu;
	}

	public MenuPerceptionImpl(String selectedMenu) {
		this(selectedMenu, DateTime.now());
	}

	
	@Override
	public DateTime getTimeStamp() {
		return t;
	}

	@Override
	public String selectedMenu() {
		return menu;
	}

}
