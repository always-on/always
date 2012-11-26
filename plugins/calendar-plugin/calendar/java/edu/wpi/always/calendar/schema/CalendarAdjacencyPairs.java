package edu.wpi.always.calendar.schema;

import java.util.*;

import org.joda.time.*;
import org.joda.time.format.*;

import edu.wpi.always.cm.dialog.*;
import edu.wpi.always.cm.ui.*;
import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.people.*;

abstract class CalendarAdjacencyPairs {

	static abstract class EventTypeAdjacencyPair extends CalendarAdjacencyPairImpl {
		public EventTypeAdjacencyPair(final CalendarStateContext context) {
			super("What type of event is it?", context, true);
			
			for(final CalendarEntryType type:CalendarEntryTypeManager.Types.values()){
				choice(type.getDisplayName(), new DialogStateTransition() {
					@Override
					public AdjacencyPair run() {
						return nextState(type);
					}
				});
			}
			choice("Something else", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EventCustomTypeAdjacencyPair(context, EventTypeAdjacencyPair.this);
				}
			});
		}
		public abstract AdjacencyPair nextState(CalendarEntryType type);
	}
	static abstract class EventPersonAdjacencyPair extends CalendarAdjacencyPairImpl {
		public EventPersonAdjacencyPair(final String question, final CalendarStateContext context) {
			super(question, context, true);
			
			for(final Person person:context.getPeopleManager().getPeople()){
				choice(person.getName(), new DialogStateTransition() {
					@Override
					public AdjacencyPair run() {
						return nextState(person);
					}
				});
			}
		}
		public abstract AdjacencyPair nextState(Person person);
	}
	public static class EventCustomTypeAdjacencyPair extends KeyboardAdjacenyPair<CalendarStateContext> {

		private final EventTypeAdjacencyPair pair;

		public EventCustomTypeAdjacencyPair(CalendarStateContext context, EventTypeAdjacencyPair pair) {
			super("Event Name:", context, context.getKeyboard());
			this.pair = pair;
		}

		@Override
		public AdjacencyPair success(String text) {
			return pair.nextState(CalendarEntryTypeManager.forName(text));
		}

		@Override
		public AdjacencyPair cancel() {
			return new WhatDo(getContext());
		}
	}
	

	public abstract static class EventDayAdjacencyPair extends CalendarAdjacencyPairImpl {

		private final LocalDate start;

		public EventDayAdjacencyPair(String weekQuestion, final CalendarStateContext context, final LocalDate start) {
			super(weekQuestion, context);
			this.start = start;

			choice("yes, it's this week", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EventWeekDayAdjacencyPair(EventDayAdjacencyPair.this, context, start);
				}
			});
			choice("no, it's next week", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EventWeekDayAdjacencyPair(EventDayAdjacencyPair.this, context, start.plusWeeks(1));
				}
			});
			choice("no, it's in the future", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EventMonthDayAdjacencyPair(EventDayAdjacencyPair.this, context, start.plusWeeks(2));
				}
			});
		}

		@Override
		public void enter(){
			getContext().getCalendarUI().showWeek(start, null, false);
		}
		
		public abstract AdjacencyPair nextState(LocalDate date);

	}
	public static class EventWeekDayAdjacencyPair extends CalendarAdjacencyPairImpl {

		private final LocalDate week;

		public EventWeekDayAdjacencyPair(EventDayAdjacencyPair pair, final CalendarStateContext context, final LocalDate week) {
			super("on which day does it take place", context);
			this.week = week;

			for (int i = 1; i <= 7; i++) {
				LocalDate d = CalendarUtil.withDayOfWeek(week, i);
				choice(dayOfWeek(d), transitionTo(pair, d));
			}
		}
		@Override
		public void enter(){
			getContext().getCalendarUI().showWeek(week, null, false);
		}

		private String dayOfWeek(final LocalDate d) {
			DateTimeFormatter f = DateTimeFormat.forPattern("EEEE");
			String dayOfWeek = f.print(d);
			return dayOfWeek;
		}

		private DialogStateTransition transitionTo(final EventDayAdjacencyPair pair, final LocalDate d) {
			return new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return pair.nextState(d);
				}
			};
		}
	}
	public static class EventMonthDayAdjacencyPair extends CalendarAdjacencyPairImpl {

		private final EventDayAdjacencyPair pair;
		private final LocalDate month;

		public EventMonthDayAdjacencyPair(final EventDayAdjacencyPair pair, final CalendarStateContext context, final LocalDate month) {
			super("touch the date on the calendar or next month", context);
			this.pair = pair;
			this.month = month;

			choice("next month", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EventMonthDayAdjacencyPair(pair, context, month.plusMonths(1));
				}
			});
		}
		
		@Override
		public void enter(){
			getContext().getCalendarUI().showMonth(month, this);
		}

		public AdjacencyPair selected(LocalDate date) {
			return pair.nextState(date);
		}
	}
	
	
	
	
	
	

	public abstract static class TimeAdjacencyPair extends CalendarAdjacencyPairImpl {
		private static final int TIME_INCREMENT = 15;//minutes
		private static final int NUM_TIMES = 8;
		public TimeAdjacencyPair(final String question, final LocalTime startTime, final CalendarStateContext context) {
			super(question, context, true);

			choice("Earlier", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return changeStartTime(startTime.minusMinutes(NUM_TIMES*TIME_INCREMENT));
				}
			});
			
			LocalTime time = startTime;
			for (int i = 0; i < NUM_TIMES; i++) {
				choice(asString(time), transitionTo(time));
				time = time.plusMinutes(TIME_INCREMENT);
			}

			choice("Later in the day", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return changeStartTime(startTime.plusMinutes(NUM_TIMES*TIME_INCREMENT));
				}
			});
		}
		private String asString(final LocalTime d) {
			DateTimeFormatter f = DateTimeFormat.forPattern("h:mm a");
			String time = f.print(d);
			return time;
		}
		private DialogStateTransition transitionTo(final LocalTime time) {
			return new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return nextState(time);
				}
			};
		}
		
		public abstract TimeAdjacencyPair changeStartTime(LocalTime time);
		public abstract AdjacencyPair nextState(LocalTime time);
	}
	
	
	
	
	
	

	abstract static class HowLongAdjacencyPair extends CalendarAdjacencyPairImpl {
		public HowLongAdjacencyPair(final CalendarStateContext context) {
			super("How long is the event", context);

			choice("1/2 hour", transitionTo( Minutes.minutes(30)));
			choice("1 hour", transitionTo(Hours.ONE));
			choice("2 hour", transitionTo(Hours.TWO));
			choice("3 hour", transitionTo(Hours.THREE));
			choice("most of the day", transitionTo(Hours.SIX));
		}

		private DialogStateTransition transitionTo(final ReadablePeriod d) {
			return new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return nextState(d);
				}
			};
		}
		public abstract AdjacencyPair nextState(final ReadablePeriod d);
	}

	abstract static class WhereAdjacencyPair extends CalendarAdjacencyPairImpl {
		public WhereAdjacencyPair(CalendarStateContext context) {
			super("Where is the event taking place", context);

			addChoice("Home");
			addChoice("Work");
			addChoice("Somewhere else");
		}

		private void addChoice(final String text) {
			choice(text, new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return nextState(text);
				}
			});
		}
		
		public abstract AdjacencyPair nextState(String place);
	};

	static class Cancel extends CalendarAdjacencyPairImpl {
		public Cancel(CalendarStateContext context) {
			super("Ok", context);
		}
		public List<String> getChoices() {
			return null;
		}
		@Override
		public AdjacencyPair nextState(String text) {
			return new WhatDo(getContext());
		}
	};
}
