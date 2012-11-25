package edu.wpi.always.user.calendar;

import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePeriod;


public interface RepeatingCalendarEntry extends CalendarEntry {

	/**
	 * @author mwills
	 * 
	 * Represents the frequency at which an event repeats
	 *
	 */
	public enum Frequency{
		/**
		 * Repeat every day
		 */
		DAILY("Daily") {
			@Override
			public LocalDate next(LocalDate date) {
				return date.plusDays(1);
			}
		},
		/**
		 * Repeat every week
		 */
		WEEKLY("Weekly") {
			@Override
			public LocalDate next(LocalDate date) {
				return date.plusWeeks(1);
			}
		},
		/**
		 * Repeat every month
		 */
		MONTHLY("Monthly") {
			@Override
			public LocalDate next(LocalDate date) {
				return date.plusMonths(1);
			}
		},
		YEARLY("Yearly") {
			@Override
			public LocalDate next(LocalDate date) {
				return date.plusYears(1);
			}
		};
		
		public abstract LocalDate next(LocalDate date);

		private final String name;
		private Frequency(String name){
			this.name = name;
		}
		public String getDisplayName() {
			return name;
		}
	}
	
	public RepeatingCalendarEntry clone();


	
	public UUID getRepeatId();
	public void setRepeatId(UUID repeatingId);
	
	public LocalDate getRepeatStartDate();
	public void setRepeatStartDate(LocalDate date);

	public LocalDate getRepeatEndDate();
	public void setRepeatEndDate(LocalDate date);

	public LocalTime getRepeatStartTime();
	public void setRepeatStartTime(LocalTime time);

	public ReadablePeriod getRepeatDuration();
	public void setRepeatDuration(ReadablePeriod duration);

	public Frequency getRepeat();
	public void setRepeat(Frequency frequency);



	public Iterable<LocalDate> getRepeatDateIterator();

}
