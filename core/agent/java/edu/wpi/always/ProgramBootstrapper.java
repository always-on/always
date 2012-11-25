package edu.wpi.always;

import java.util.*;

import org.apache.log4j.*;
import org.apache.log4j.varia.*;
import org.picocontainer.*;
import org.picocontainer.behaviors.*;

import edu.wpi.always.user.*;
import edu.wpi.always.user.owl.*;

public class ProgramBootstrapper {

	private final MutablePicoContainer pico;

	public ProgramBootstrapper(boolean logToConsole) {
		if(logToConsole)
			BasicConfigurator.configure();
		else
			BasicConfigurator.configure(new NullAppender());
		
		pico = new PicoBuilder().withBehaviors(new OptInCaching()).withConstructorInjection().build();
		pico.addComponent(pico);
	}

	private final List<PicoRegistry> picoRegistries = new ArrayList<PicoRegistry>();
	private final List<OntologyRegistry> ontologyRegistries = new ArrayList<OntologyRegistry>();

	public void addRegistry(Registry registry) {
		if (registry instanceof PicoRegistry)
			picoRegistries.add((PicoRegistry) registry);
		if (registry instanceof OntologyRegistry)
			ontologyRegistries.add((OntologyRegistry) registry);
	}

	private final List<Registry> cmRegistries = new ArrayList<Registry>();

	public void addCMRegistry(Registry registry) {
		cmRegistries.add(registry);
	}

	public void start() {
		for (PicoRegistry registry : picoRegistries)
			registry.register(pico);

		OntologyRuleHelper helper = pico.getComponent(OntologyRuleHelper.class);
		for (OntologyRegistry registry : ontologyRegistries)
			registry.register(helper);

		UserModel userModel = pico.getComponent(UserModel.class);
		if(userModel != null) {
			userModel.load();
			System.out.println("Loaded user model");
		}

		ICollaborationManager cmBootstrapper = pico.getComponent(ICollaborationManager.class);
		for (Registry registry : cmRegistries)
			cmBootstrapper.addRegistry(registry);
		
		System.out.println("Starting Collaboration Manager");
		cmBootstrapper.start();
		System.out.println("Program started");
	}

	public MutablePicoContainer getContainer() {
		return pico;
	}

}
