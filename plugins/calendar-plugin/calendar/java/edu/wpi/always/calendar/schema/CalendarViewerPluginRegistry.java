package edu.wpi.always.calendar.schema;

import edu.wpi.always.cm.*;

public class CalendarViewerPluginRegistry implements SchemaRegistry{
	@Override
	public void register(SchemaManager manager) {
		manager.registerSchema(CalendarSchema.class, true);
	}

}
