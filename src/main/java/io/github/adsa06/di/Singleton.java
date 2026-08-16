package io.github.adsa06.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Indicates that a class should be managed as a singleton, ensuring that there
 * is only one instance of that class during the lifetime of the dependency
 * injection container.
 *
 * <p>
 * This annotation is available at runtime so that the container can detect it through reflection.
 * </p>
 *
 * @author Aitor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Singleton {
}
