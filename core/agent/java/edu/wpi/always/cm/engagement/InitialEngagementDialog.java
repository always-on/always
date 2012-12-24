package edu.wpi.always.cm.engagement;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.dialog.*;


public class InitialEngagementDialog  extends AdjacencyPairImpl<Object> {
	public InitialEngagementDialog(final SchemaManager schemaManager) {
		super("Hello", null);

		choice("Hi", new DialogStateTransition() {
			@SuppressWarnings("unchecked")
			@Override
			public AdjacencyPair run() {
				try {
					schemaManager.start((Class<? extends Schema>) Class.forName("edu.wpi.always.test.weather.WeatherSchema"));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
}
