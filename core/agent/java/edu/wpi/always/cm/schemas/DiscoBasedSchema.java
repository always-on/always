package edu.wpi.always.cm.schemas;

import java.io.*;
import java.util.*;

import com.google.common.collect.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.dialog.*;
import edu.wpi.always.cm.disco.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.disco.Agenda.Plugin.Item;

public class DiscoBasedSchema extends SchemaImplBase implements AdjacencyPair {

	DiscoDialogHelper discoHelper = new DiscoDialogHelper(true);
	private final UtteranceFormatterHelper formatter;
	private MenuTurnStateMachine stateMachine;
	private APCache currentAP;

	public DiscoBasedSchema(BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor, MenuPerceptor menuPerceptor) {
		super(behaviorReceiver, behaviorHistory);

		formatter = new UtteranceFormatterHelper(discoHelper.getDisco());

		stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor, menuPerceptor, new RepeatMenuTimeoutHandler());
		stateMachine.setSpecificityMetadata(0.9);

		setNeedsFocusResouce();
	}

	public void setTaskId(String taskId) {
		discoHelper.setTaskId(taskId);
		stateMachine.setAdjacencyPair(this);
	}
	
	public Object eval(String script, String where) {
		Object result = discoHelper.getDisco().eval(script, where);
		return result;
	}

	@Override
	public void run() {
		if (discoHelper.getPlan() != null) {
			propose(stateMachine);
		} else {
			proposeNothing();
		}
	}

	private List<Item> generateUserTasks() {
		return discoHelper.generateUserTasks();
	}

	@Override
	public double timeRemaining() {
		return 5;
	}

	@Override
	public void enter() {
		
	}

	@Override
	public AdjacencyPair nextState(String text) {
		List<Item> items = generateUserTasks();
		List<String> choices = formatter.format(items);
		if (choices.contains(text)) {
			int idx = choices.indexOf(text);
			discoHelper.userItemDone(idx, text);
		}
		
		updateCurrentAP();
		
		return this;
	}

	private void updateCurrentAP() {
		discoHelper.getDisco().getInteraction().getSystem().respond(discoHelper.getDisco().getInteraction(), false, true);
		
		currentAP = new APCache(discoHelper.getlastUtterance(), getChoicesFromDisco());
	}
	
	private List<String> getChoicesFromDisco() {
		List<Item> items = generateUserTasks();
		return formatter.format(items);
	}

	@Override
	public String getMessage() {
		if(currentAP == null)
			updateCurrentAP();
		
		return currentAP.getMessage();
	}

	@Override
	public List<String> getChoices() {
		if(currentAP == null)
			updateCurrentAP();
		
		return currentAP.getMenus();
	}

	@Override
	public boolean isTwoColumnMenu() {
		return false;
	}

	@Override
	public boolean prematureEnd() {
		return false;
	}

	protected void loadModel(String resourcePath) throws IOException {
		discoHelper.getDisco().load(resourcePath);
	}

	private static class APCache {
		private final String message;
		private final List<String> menus;

		public APCache(String message, List<String> menus) {
			this.message = message;
			this.menus = ImmutableList.copyOf(menus);
		}

		public String getMessage() {
			return message;
		}

		public List<String> getMenus() {
			return menus;
		}
	}
	
}
