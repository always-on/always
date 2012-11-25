package edu.wpi.always.story;

import edu.wpi.always.cm.ragclient.*;
import edu.wpi.always.cm.ui.*;
import edu.wpi.always.user.people.*;

public class StoryStateContext {
	
	private final Keyboard keyboard;
	private final UIMessageDispatcher dispatcher;
	private final StoryManager storyManager;
	private final PeopleManager peopleManager;

	public StoryStateContext(UIMessageDispatcher dispatcher, StoryManager storyManager, Keyboard keyboard, PeopleManager peopleManager) {
		this.dispatcher = dispatcher;
		this.storyManager = storyManager;
		this.keyboard = keyboard;
		this.peopleManager = peopleManager;
	}
	public Keyboard getKeyboard() {
		return keyboard;
	}
	public UIMessageDispatcher getDispatcher() {
		return dispatcher;
	}
	public StoryManager getStoryManager(){
		return storyManager;
	}
	public PeopleManager getPeopleManager(){
		return peopleManager;
	}

}
