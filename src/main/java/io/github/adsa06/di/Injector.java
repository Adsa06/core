package io.github.adsa06.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Lightweight dependency injector based on reflection.
 * <p>
 * It manages the creation of singleton instances,
 * dependency resolution through constructors, and field injection.
 * </p>
 * 
 * @author Aitor
 */
public class Injector {

    /**
     * Default constructor
     */
    public Injector() {
    }

    /**
     * All registered instances
     */
    private final Map<Class<?>, Object> instances = new HashMap<>();

    /**
     * Instances that are currently being created
     */
    private final Set<Class<?>> onCreation = new HashSet<>();

    /**
     * Manually register an existing instance associated with a specific class type.
     * <p>
     * Verify that the provided instance actually matches the class type specified
     * before storing it.
     * </p>
     * 
     * @param <T>      The instance type.
     * @param type     The class that represents the component's type.
     * @param instance The instance that will be associated with the specified type.
     * @return the same instance that was registered
     * @throws IllegalArgumentException if {@code instance} is not an instance of
     *                                  {@code type}
     */
    public <T> T registerInstance(Class<T> type, T instance) throws IllegalArgumentException {
        if (!type.isInstance(instance)) {
            throw new IllegalArgumentException(
                    "Instance is not of type " + type.getName());
        }

        instances.put(type, instance);
        return instance;
    }

    /**
     * Gets an instance of the specified class.
     * <p>
     * If the instance already exists in the internal map, it returns it directly.
     * If it does not exist, it checks that the class is annotated with
     * {@code @Singleton}, recursively resolves its dependencies, constructs the
     * instance, injects its fields, and stores it for future use.
     * </p>
     *
     * @param <T>  The instance type.
     * @param type The class for which the instance is required.
     * @return The single instance corresponding to the requested type.
     * @throws IllegalStateException If the class is not annotated with
     *                               {@code @Singleton} or if a circular dependency
     *                               is detected during instantiation.
     * @throws RuntimeException      If a reflection error occurs while attempting
     *                               to create the instance or inject its
     *                               dependencies.
     */
    public <T> T getInstance(Class<T> type) throws IllegalStateException, RuntimeException {
        if (instances.containsKey(type))
            return type.cast(instances.get(type));

        if (!type.isAnnotationPresent(Singleton.class))
            throw new IllegalStateException("Class " + type.getName() + " is not annotated with @Singleton");

        if (!onCreation.add(type))
            throw new IllegalStateException(
                    "A circular dependency has been detected while creating " + type.getName()
                            + ". This injector does not resolve it automatically: break the "
                            + "cycle by using field injection on one of the two sides, "
                            + "or redesign the code to eliminate the circular dependency.");

        try {
            T instance = type.cast(build(type));

            instances.put(type, instance);
            injectFields(instance);
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("An instance of " + type.getName() + " could not be created", e);
        } finally {
            onCreation.remove(type);
        }
    }

    /**
     * Creates a new instance of the specified type using reflection, resolving and
     * injecting the necessary dependencies into its constructor parameters.
     *
     * @param type The class for which the instance is required.
     * @return A new object instantiated using the selected constructor.
     * @throws ReflectiveOperationException If the constructor cannot be accessed,
     *                                      invoked, or fails during instantiation
     *                                      via reflection.
     */
    private Object build(Class<?> type) throws ReflectiveOperationException {
        Constructor<?> constructor = selectConstructor(type);
        constructor.setAccessible(true);

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            arguments[i] = getInstance(parameterTypes[i]);
        }

        return constructor.newInstance(arguments);
    }

    /**
     * Select the appropriate constructor that the injector should use to create the
     * instance.
     * <p>
     * If the class has a single constructor, it is selected automatically. If it
     * has multiple constructors, the one annotated with {@code @Inject} will be
     * explicitly selected.
     * </p>
     *
     * @param type The class to be inspected.
     * @return The {@link Constructor} selected to instantiate the class.
     * @throws IllegalStateException If there are multiple constructors and none of
     *                               them is annotated with {@code @Inject}.
     */
    private Constructor<?> selectConstructor(Class<?> type) throws IllegalStateException {
        Constructor<?>[] constructors = type.getDeclaredConstructors();

        if (constructors.length == 1)
            return constructors[0];

        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class))
                return constructor;
        }

        throw new IllegalStateException(
                "Class " + type.getName() + " has multiple constructors; "
                        + " annotate the one that the Injector should use with @Inject.");
    }

    /**
     * Injects dependencies into the declared fields of the given instance that are
     * annotated with {@code @Inject}.
     *
     * @param instance The newly created object into which the field dependencies
     *                 will be injected.
     * @throws IllegalAccessException If you try to inject a field that has the
     *                                {@code final} modifier.
     */
    private void injectFields(Object instance) throws IllegalAccessException {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class))
                continue;

            if (Modifier.isFinal(field.getModifiers()))
                throw new IllegalAccessException(
                        "The field '" + field.getName() + "' in " + instance.getClass().getSimpleName()
                                + "is final: Field injection occurs after the object is created, so it cannot be final. "
                                + "Use constructor injection for that field.");

            field.setAccessible(true);
            field.set(instance, getInstance(field.getType()));
        }
    }
}