package io.github.adsa06.di;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class Injector {

    /**
     * Default constructor
     */
    public Injector() {
    }

    /**
     * 
     */
    private final Map<Class<?>, Object> instances = new HashMap<>();

    /**
     * 
     */
    private final Set<Class<?>> onCreation = new HashSet<>();
    
    public <T> void registerInstance(Class<T> type, T instance) {
        instances.put(type, instance);
    }

    public <T> T getInstance(Class<T> type) {
        return type.cast(instances.get(type));
    }

    private void injectFields(Object instancie) throws IllegalAccessException {
        
    }

    private Constructor<?> selectConstructor(Class<?> type) {
        return null;
    }

    private Object build(Class<?> type) throws ReflectiveOperationException {
        return null;
    }
}
