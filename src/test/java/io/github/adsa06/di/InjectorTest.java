package io.github.adsa06.di;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Injector}.
 */
class InjectorTest {
 
    private Injector injector;
 
    @BeforeEach
    void setUp() {
        injector = new Injector();
    }
 
    // ------------------------------------------------------------------
    // Test fixtures
    // ------------------------------------------------------------------
 
    @Singleton
    static class SimpleService {
    }
 
    // No @Singleton on purpose
    static class NotAnnotatedService {
    }
 
    @Singleton
    static class SingleConstructorDependency {
        final SimpleService simpleService;
 
        SingleConstructorDependency(SimpleService simpleService) {
            this.simpleService = simpleService;
        }
    }
 
    @Singleton
    static class MultipleConstructorsWithInject {
        final SimpleService simpleService;
        final String source;
 
        public MultipleConstructorsWithInject() {
            this.simpleService = null;
            this.source = "no-arg";
        }
 
        @Inject
        public MultipleConstructorsWithInject(SimpleService simpleService) {
            this.simpleService = simpleService;
            this.source = "injected";
        }
    }
 
    @Singleton
    static class MultipleConstructorsNoInject {
        public MultipleConstructorsNoInject() {
        }
 
        public MultipleConstructorsNoInject(SimpleService simpleService) {
        }
    }
 
    @Singleton
    static class FieldInjectionService {
        @Inject
        SimpleService simpleService;
    }
 
    @Singleton
    static class FinalFieldInjectionService {
        @Inject
        final SimpleService simpleService = null;
    }
 
    @Singleton
    static class CircularConstructorA {
        CircularConstructorA(CircularConstructorB b) {
        }
    }
 
    @Singleton
    static class CircularConstructorB {
        CircularConstructorB(CircularConstructorA a) {
        }
    }
 
    @Singleton
    static class CircularFieldA {
        @Inject
        CircularFieldB b;
    }
 
    @Singleton
    static class CircularFieldB {
        @Inject
        CircularFieldA a;
    }
 
    @Singleton
    static class FailingConstructorService {
        public FailingConstructorService() {
            throw new RuntimeException("boom");
        }
    }
 
    // ------------------------------------------------------------------
    // registerInstance
    // ------------------------------------------------------------------
 
    @Nested
    @DisplayName("registerInstance")
    class RegisterInstanceTests {
 
        @Test
        @DisplayName("stores and returns the given instance")
        void registersAndReturnsInstance() {
            SimpleService service = new SimpleService();
 
            SimpleService returned = injector.registerInstance(SimpleService.class, service);
 
            assertSame(service, returned);
            assertSame(service, injector.getInstance(SimpleService.class));
        }
 
        @Test
        @DisplayName("allows retrieving a registered instance even without @Singleton")
        void allowsNonSingletonClassesWhenManuallyRegistered() {
            NotAnnotatedService instance = new NotAnnotatedService();
 
            injector.registerInstance(NotAnnotatedService.class, instance);
 
            assertSame(instance, injector.getInstance(NotAnnotatedService.class));
        }
 
        @Test
        @DisplayName("throws IllegalArgumentException when the instance does not match the type")
        @SuppressWarnings({ "unchecked", "rawtypes" })
        void throwsWhenInstanceTypeMismatches() {
            Class rawType = SimpleService.class;
 
            assertThrows(IllegalArgumentException.class,
                    () -> injector.registerInstance(rawType, "not a SimpleService"));
        }
    }
 
    // ------------------------------------------------------------------
    // getInstance - basic behavior
    // ------------------------------------------------------------------
 
    @Nested
    @DisplayName("getInstance basics")
    class GetInstanceBasicTests {
 
        @Test
        @DisplayName("returns the same instance on repeated calls (singleton behavior)")
        void returnsSameInstanceOnRepeatedCalls() {
            SimpleService first = injector.getInstance(SimpleService.class);
            SimpleService second = injector.getInstance(SimpleService.class);
 
            assertNotNull(first);
            assertSame(first, second);
        }
 
        @Test
        @DisplayName("throws IllegalStateException when the class is not annotated with @Singleton")
        void throwsWhenClassIsNotSingleton() {
            assertThrows(IllegalStateException.class,
                    () -> injector.getInstance(NotAnnotatedService.class));
        }
    }
 
    // ------------------------------------------------------------------
    // Constructor injection
    // ------------------------------------------------------------------
 
    @Nested
    @DisplayName("Constructor injection")
    class ConstructorInjectionTests {
 
        @Test
        @DisplayName("resolves the single constructor automatically")
        void resolvesSingleConstructorAutomatically() {
            SingleConstructorDependency instance = injector.getInstance(SingleConstructorDependency.class);
 
            assertNotNull(instance);
            assertNotNull(instance.simpleService);
        }
 
        @Test
        @DisplayName("selects the @Inject-annotated constructor among multiple")
        void selectsAnnotatedConstructorAmongMultiple() {
            MultipleConstructorsWithInject instance = injector.getInstance(MultipleConstructorsWithInject.class);
 
            assertEquals("injected", instance.source);
            assertNotNull(instance.simpleService);
        }
 
        @Test
        @DisplayName("throws IllegalStateException when multiple constructors exist and none is annotated")
        void throwsWhenNoConstructorIsAnnotated() {
            assertThrows(IllegalStateException.class,
                    () -> injector.getInstance(MultipleConstructorsNoInject.class));
        }
    }
 
    // ------------------------------------------------------------------
    // Field injection
    // ------------------------------------------------------------------
 
    @Nested
    @DisplayName("Field injection")
    class FieldInjectionTests {
 
        @Test
        @DisplayName("injects fields annotated with @Inject after construction")
        void injectsAnnotatedFields() {
            FieldInjectionService instance = injector.getInstance(FieldInjectionService.class);
 
            assertNotNull(instance.simpleService);
        }
 
        @Test
        @DisplayName("wraps IllegalAccessException in RuntimeException when field is final")
        void throwsWhenFieldIsFinal() {
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> injector.getInstance(FinalFieldInjectionService.class));
 
            assertInstanceOf(IllegalAccessException.class, exception.getCause());
        }
    }
 
    // ------------------------------------------------------------------
    // Circular dependencies
    // ------------------------------------------------------------------
 
    @Nested
    @DisplayName("Circular dependencies")
    class CircularDependencyTests {
 
        @Test
        @DisplayName("throws IllegalStateException on circular dependency through constructors")
        void detectsCircularDependencyThroughConstructors() {
            assertThrows(IllegalStateException.class,
                    () -> injector.getInstance(CircularConstructorA.class));
        }
 
        @Test
        @DisplayName("resolves circular dependency through field injection")
        void resolvesCircularDependencyThroughFields() {
            CircularFieldA a = injector.getInstance(CircularFieldA.class);
 
            assertNotNull(a.b);
            assertNotNull(a.b.a);
            assertSame(a, a.b.a);
        }
    }
 
    // ------------------------------------------------------------------
    // Reflection error handling
    // ------------------------------------------------------------------
 
    @Nested
    @DisplayName("Reflection error handling")
    class ErrorHandlingTests {
 
        @Test
        @DisplayName("wraps constructor failures in RuntimeException with the original cause preserved")
        void wrapsConstructorFailureInRuntimeException() {
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> injector.getInstance(FailingConstructorService.class));
 
            assertInstanceOf(InvocationTargetException.class, exception.getCause());
            assertEquals("boom", exception.getCause().getCause().getMessage());
        }
 
        @Test
        @DisplayName("removes the failed instance so a retry is possible")
        void removesFailedInstanceAfterError() {
            assertThrows(RuntimeException.class,
                    () -> injector.getInstance(FailingConstructorService.class));
 
            // If it were left registered as broken, this would return silently
            // instead of attempting construction (and failing) again.
            assertThrows(RuntimeException.class,
                    () -> injector.getInstance(FailingConstructorService.class));
        }
    }
}