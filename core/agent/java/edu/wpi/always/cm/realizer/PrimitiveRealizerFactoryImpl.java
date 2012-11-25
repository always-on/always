package edu.wpi.always.cm.realizer;

import java.lang.reflect.*;
import java.util.*;

import org.picocontainer.*;

public class PrimitiveRealizerFactoryImpl implements PrimitiveRealizerFactory {

	private final PicoContainer container;
	private final HashMap<Class<?>, Constructor<?>> constructors = new HashMap<Class<?>, Constructor<?>>();

	public PrimitiveRealizerFactoryImpl(PicoContainer container) {
		this.container = container;
	}

	@Override
	public PrimitiveRealizer<?> create(PrimitiveBehavior primitiveBehavior) {
		if (!constructors.containsKey(primitiveBehavior.getClass()))
			return null;

		Constructor<?> ctor = constructors.get(primitiveBehavior.getClass());

		return instantiate(ctor, primitiveBehavior);
	}

	private PrimitiveRealizer<?> instantiate(Constructor<?> ctor,
			PrimitiveBehavior primitiveBehavior) {
		Class<?>[] types = ctor.getParameterTypes();

		Object[] params = new Object[types.length];
		for (int i = 0; i < params.length; i++) {

			if (types[i].equals(primitiveBehavior.getClass()))
				params[i] = primitiveBehavior;
			else
				params[i] = container.getComponent(types[i]);

		}

		return invokeConstructor(ctor, params);
	}

	public void register(
			Class<? extends PrimitiveRealizer<? extends PrimitiveBehavior>> type) {
		Constructor<?> ctor = findLargestConstructorThatAcceptsAPrimitiveBehavior(type);

		if (ctor == null)
			throw new RuntimeException("No suitable constructor for realizer <"
					+ type.getCanonicalName() + ">");

		Class<?> pbType = findPrimitiveBehavior(ctor.getParameterTypes());

		constructors.put(pbType, ctor);
	}

	private Constructor<?> findLargestConstructorThatAcceptsAPrimitiveBehavior(
			Class<? extends PrimitiveRealizer<? extends PrimitiveBehavior>> type) {
		Constructor<?> ctor = null;
		int maxParamNum = -1;

		for (Constructor<?> c : type.getConstructors()) {
			Class<?>[] parameterTypes = c.getParameterTypes();

			if (aPrimitiveRealizerIsAmongThem(parameterTypes)) {
				if (parameterTypes.length > maxParamNum) {
					maxParamNum = c.getParameterTypes().length;
					ctor = c;
				}
			}
		}

		return ctor;
	}

	private boolean aPrimitiveRealizerIsAmongThem(Class<?>[] types) {
		return findPrimitiveBehavior(types) != null;
	}

	private Class<?> findPrimitiveBehavior(Class<?>[] types) {
		for (Class<?> t : types) {
			if (PrimitiveBehavior.class.isAssignableFrom(t)) {
				return t;
			}
		}

		return null;
	}

	private static PrimitiveRealizer<?> invokeConstructor(Constructor<?> ctor,
			Object[] params) {
		PrimitiveRealizer<?> instance = null;

		try {
			instance = (PrimitiveRealizer<?>) ctor.newInstance(params);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return instance;
	}

	public void registerAllRealizerInContainer() {
		for(Class<? extends PrimitiveRealizer<? extends PrimitiveBehavior>> t : findAllRealizers()) {
			register(t);
		}
	}

	@SuppressWarnings("rawtypes")
	private List<Class<? extends PrimitiveRealizer<? extends PrimitiveBehavior>>> findAllRealizers() {
		List<ComponentAdapter<PrimitiveRealizer>> registered = container
				.getComponentAdapters(PrimitiveRealizer.class);
		List<Class<? extends PrimitiveRealizer<? extends PrimitiveBehavior>>> realizerTypes = new ArrayList<Class<? extends PrimitiveRealizer<? extends PrimitiveBehavior>>>();

		for (ComponentAdapter<PrimitiveRealizer> r : registered) {
			@SuppressWarnings("unchecked")
			Class<? extends PrimitiveRealizer<? extends PrimitiveBehavior>> t =
				(Class<? extends PrimitiveRealizer<? extends PrimitiveBehavior>>)r.getComponentImplementation();
			
			realizerTypes.add(t);
		}
		return realizerTypes;
	}
}
