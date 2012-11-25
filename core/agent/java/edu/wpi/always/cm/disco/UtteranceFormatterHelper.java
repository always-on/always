package edu.wpi.always.cm.disco;

import static com.google.common.collect.Maps.*;

import java.util.*;

import edu.wpi.always.DiscoSynchronizedWrapper;
import edu.wpi.always.cm.disco.actions.*;

import edu.wpi.cetask.*;
import edu.wpi.disco.Agenda.Plugin.Item;
import edu.wpi.disco.*;
import edu.wpi.disco.lang.*;

public class UtteranceFormatterHelper {
	private HashMap<Utterance, String> utteranceTranslationCache = newHashMap();
	private final DiscoSynchronizedWrapper discoWrapper;

	public UtteranceFormatterHelper(DiscoSynchronizedWrapper discoWrapper) {
		this.discoWrapper = discoWrapper;
	}

	public UtteranceFormatterHelper(Disco disco) {
		this(new DiscoSynchronizedWrapper(disco));
	}

	public List<String> format(List<Item> items) {
		ArrayList<String> menus = new ArrayList<String>();

		if (items != null) {
			for (Item i : items) {
				if (i.task instanceof Utterance) {
					String formatted = format(i);
					menus.add(formatted);
				}
			}
		}

		return menus;
	}

	public String format(Item item) {
		if(!Utterance.class.isAssignableFrom(item.task.getClass()))
			throw new IllegalArgumentException("item.task was not of type Utterance");
		
		Utterance utterance = (Utterance) item.task;

		if (!utteranceTranslationCache.containsKey(utterance)) {
			String formatted = item.formatted;
			
			if(formatted == null) {
			formatted = utterance.occurred() ? utterance.formatTask()
					: discoWrapper.execute(new TranslateUtterance(utterance));
			} else {
				formatted = discoWrapper.execute(new TranslateUtterance(utterance, formatted));
			}
			
			formatted = Utils.capitalize(formatted);
			StringBuffer buffer = new StringBuffer(formatted);
			Utils.endSentence(buffer);
			formatted = buffer.toString();

			utteranceTranslationCache.put(utterance, formatted);
		}

		return utteranceTranslationCache.get(utterance);
	}
	
	public void clearTranslationCache() {
		utteranceTranslationCache.clear();
	}

}
