package edu.wpi.always.cm.schemas.registries;

import org.picocontainer.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.ragclient.*;
import edu.wpi.always.*;

public class FunPackRegistry implements SchemaRegistry, PicoRegistry {

	@Override
	public void register(SchemaManager manager) {
		manager.registerSchema(RummySchema.class, false);
	}

	@Override
	public void register(MutablePicoContainer container) {
		container.addComponent(RummyPlugin.class);
	}

}
