package io.github.adsa06.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Indicates that an element should be automatically injected by the dependency
 * injection system.
 *
 * <p>
 * It can be applied to constructors and fields. The annotation is available at
 * runtime to allow it to be detected through reflection.
 * </p>
 *
 * @author Aitor
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.CONSTRUCTOR,
        ElementType.FIELD
})
public @interface Inject {
}