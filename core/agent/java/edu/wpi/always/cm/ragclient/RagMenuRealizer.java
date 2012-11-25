package edu.wpi.always.cm.ragclient;

import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;

public class RagMenuRealizer extends SingleRunPrimitiveRealizer<MenuBehavior>
		implements RagClientProxyObserver {

	private final RagClientProxy proxy;

	public RagMenuRealizer(MenuBehavior params, RagClientProxy proxy) {
		super(params);
		this.proxy = proxy;
	}

	@Override
	protected void singleRun() {
		proxy.addObserver(this);
		proxy.showMenu(getParams().getItems(), getParams().isTwoColumn());
	}

	@Override
	public void notifyDone(RagClientProxy sender, String action, String data) {
		if (action.equals("show_menu")) {
			fireDoneMessage();
			proxy.removeObserver(this);
		}
	}

	@Override
	public void notifyMenuSelected(RagClientProxy ragClientProxy, String text) {
	}

}
