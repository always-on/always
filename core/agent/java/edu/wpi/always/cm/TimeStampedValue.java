package edu.wpi.always.cm;

import org.joda.time.*;

public class TimeStampedValue<T> {
	private final DateTime timeStamp;
	private final T val;
	
	public TimeStampedValue(T value, DateTime timeStamp) {
		this.timeStamp = timeStamp;
		this.val = value;
	}
	
	public TimeStampedValue(T value) {
		this(value, DateTime.now());
	}

	public T getValue() {
		return val;
	}
	
	public DateTime getTimeStamp() {
		return timeStamp;
	}
}
