package edu.wpi.always.calendar.schema;

import org.joda.time.*;

import edu.wpi.always.cm.dialog.*;


public class WhatDo  extends CalendarAdjacencyPairImpl {
	public WhatDo(final CalendarStateContext context) {
		super("How do you want to use the calendar?", context);

		choice("Add a new event", new DialogStateTransition() {
			@Override
			public AdjacencyPair run() {
				return new RepeatEvent(context);
			}
		});
		choice("Change an event", new DialogStateTransition() {
			@Override
			public AdjacencyPair run() {
				return new CalendarChangeState.EventThisWeek(context, new LocalDate());
			}
		});
		choice("Delete an event", new DialogStateTransition() {
			@Override
			public AdjacencyPair run() {
				return new CalendarDeleteState.EventThisWeek(context, new LocalDate());
			}
		});
		choice("Just look at the calendar", new DialogStateTransition() {
			@Override
			public AdjacencyPair run() {
				return new CalendarViewState.LookCalendarStyle(context);
			}
		});
	}
	public static class RepeatEvent extends CalendarAdjacencyPairImpl {
		public RepeatEvent(final CalendarStateContext context) {
			super("Is the event going to repeat?", context);
			choice("Yes, the event will repeat", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new CalendarRepeatAddState.EventType(context);
				}
			});
			choice("No, the event will not repeat", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new CalendarSingleAddState.EventType(context);
				}
			});
		}
	}
}
