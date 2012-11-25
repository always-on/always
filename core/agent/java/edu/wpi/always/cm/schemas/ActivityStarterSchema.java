package edu.wpi.always.cm.schemas;

import java.util.*;

import org.joda.time.*;

import com.google.common.collect.*;

import edu.wpi.always.*;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.disco.*;
import edu.wpi.always.cm.disco.actions.*;
import edu.wpi.always.cm.engagement.*;
import edu.wpi.always.cm.perceptors.*;

import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.Agenda.Plugin.Item;
import edu.wpi.disco.lang.*;
import edu.wpi.disco.plugin.*;

public class ActivityStarterSchema extends SchemaImplBase implements
		DialogContentProvider {

	private final Perceptor<GeneralEngagementPerception> engagementPerceptor;
	private boolean alreadyEngaged;
	private TopsPlugin topsPlugin;
	private final UtteranceFormatterHelper formatter;
	private OldDialogStateMachine stateMachine;
	private Plan awaitUserAcceptance;
	private RespondPlugin.Accept acceptPlugin;
	private RespondPlugin.Reject rejectPlugin;
	private DateTime lastProposal = DateTime.now();
	private Agenda agenda;
	private final DiscoBasedActivityManager topicManager;
	private final List<String> containerTaskIds = new ArrayList<String>();

	public ActivityStarterSchema(BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory resourceMonitor,
			DiscoSynchronizedWrapper disco,
			IRelationshipManager relationshipManager,
			GeneralEngagementPerceptor engagementPerceptor,
			MenuPerceptor menuPerceptor, SchemaManager schemaManager) {
		super(behaviorReceiver, resourceMonitor);
		this.topicManager = new DiscoBasedActivityManager(relationshipManager, schemaManager);
		this.engagementPerceptor = engagementPerceptor;

		setNeedsFocusResouce();
		
		formatter = new UtteranceFormatterHelper(disco);

		stateMachine = new OldDialogStateMachine(behaviorHistoryWithAutomaticInclusionOfFocus(), this,
				menuPerceptor);
		stateMachine.setSpecificityMetadata(0.6);
		stateMachine.setNewActivity(true);

		containerTaskIds.add("Rummy");
	}

	public boolean CheckEngagementStatus() {
		GeneralEngagementPerception engPerc = engagementPerceptor.getLatest();

		if (engPerc != null && engPerc.engaged()) {
			if (!alreadyEngaged) {
				formatter.clearTranslationCache();

				topicManager.initFromRelationshipManager();

				topsPlugin = createTopsPlugin();
				acceptPlugin = new RespondPlugin.Accept(topsPlugin.getAgenda(), 1);
				rejectPlugin = new RespondPlugin.Reject(topsPlugin.getAgenda(), 1);

				agenda = createAgenda();
				new DecompositionPlugin(agenda, 1, true, false);
				new ProposeShouldTopPlugin(agenda, 1);

				alreadyEngaged = true;
			}

			return true;
		}

		return false;
	}

	@Override
	public void run() {
		if (CheckEngagementStatus())
			propose(stateMachine);
		else
			proposeNothing();
	}

	private List<Item> getProposeItems(final boolean human) {
//		if(true)
//			throw new RuntimeException("New Activity Schema needs to be fixed because of disco constructor visibility change");
		if (CheckEngagementStatus()) {

			return getDisco().execute(new DiscoFunc<List<Item>>() {

				@Override
				public List<Item> execute(Disco disco) {
					HashMap<Task, Item> map = new HashMap<Task, Item>();
					agenda.visit(getTodaysPlan(), map, null);

					List<Item> items = Lists.newArrayList(map.values());

					if (items.size() < 4) {
						for (String tid : containerTaskIds) {
							TaskClass taskClass = disco.getTaskClass(tid);

							if (!topicManager.isRunning(taskClass)) {
								items.add(topsPlugin.new Item(Propose.Should.newInstance(disco, human, taskClass.newInstance()), null));
							}

							if (items.size() >= 4)
								break;
						}
					}
					return items;
				}

			});
		}

		return null;
	}

	private TopsPlugin createTopsPlugin() {
		Agenda agenda = createAgenda();
		return new TopsPlugin(agenda, 1, true);
	}

	private Agenda createAgenda() {
		Actor actor = getInteraction().getExternal();

		return DiscoUtils.createEmptyAgendaFor(actor);
	}

	private Interaction getInteraction() {
		return getDisco().execute(new DiscoFunc<Interaction>() {
			public Interaction execute(Disco disco) {
				return disco.getInteraction();
			}
		});
	}

	@Override
	public String whatToSay() {
		if (awaitUserAcceptance != null
				|| lastProposal.isAfter(DateTime.now().minusSeconds(5)))
			return null;

		List<Item> items = getProposeItems(false);

		if (items != null && items.size() == 1)
			return formatter.format(items.get(0));

		return "what do you think we should do?";
	}

	@Override
	public void doneSaying(final String text) {

		getDisco().execute(new DiscoAction() {

			@Override
			public void execute(Disco disco) {
				handleSelectedProposal(text, false);

				awaitUserAcceptance = disco.getFocus();
			}
		});

	}

	private void handleSelectedProposal(String text, boolean human) {
		List<Item> items = currentOptionsOfTheUser(human);

		if (items != null) {
			List<String> texts = formatter.format(items);
			int i = texts.indexOf(text);
			if (i >= 0) {
				final Item selected = items.get(i);
				getDisco().execute(new DiscoAction() {

					public void execute(Disco disco) {
						done(selected);
						topicManager.runSchemaBasedOnTaskOnTopOfStack();
					}

				});

				lastProposal = DateTime.now();
			}
		}
	}

	protected boolean shouldRunDiscoBasedSchemaFor(Plan plan) {
		return !plan.getDecompositions().isEmpty();
	}

	private void done(Item item) {
		getDisco().execute(new ItemDone(item));
	}

	@Override
	public List<String> userChoices() {
		if (lastProposal.isAfter(DateTime.now().minusSeconds(5)))
			return null;

		List<Item> items = currentOptionsOfTheUser(true);

		if (items != null && items.size() > 1)
			return formatter.format(items);

		return null;
	}

	private List<Item> currentOptionsOfTheUser(boolean human) {
		List<Item> items;
		if (awaitUserAcceptance == null) {
			items = getProposeItems(human);
		} else {
			items = new ArrayList<Item>();

			items.addAll(acceptPlugin.apply(awaitUserAcceptance));
			items.addAll(rejectPlugin.apply(awaitUserAcceptance));
		}
		return items;
	}

	@Override
	public void userSaid(String text) {
		handleSelectedProposal(text, true);
		awaitUserAcceptance = null;
	}

	@Override
	public double timeRemaining() {
		return 1;
	}

	public DiscoSynchronizedWrapper getDisco() {
		return topicManager.getDisco();
	}

	public Plan getTodaysPlan() {
		return topicManager.getPlan();
	}
}
