package io.github.adsa06.di;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link Injector} class.
 */
public class InjectorTest {

    /**
     * Injector used to register and retrieve dependencies during tests.
     */
    private Injector injector;

    /**
     * Creates a new injector before each test.
     */
    @BeforeEach
    void setUp() {
        injector = new Injector();
    }

    /**
     * Verifies that a registered instance can be retrieved from the injector.
     */
    @Test
    void getInstanceTest() {
        Object object = new Object();
        injector.registerInstance(Object.class, object);

        assertSame(object, injector.getInstance(Object.class));
    }
}
