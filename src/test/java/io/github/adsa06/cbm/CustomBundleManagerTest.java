package io.github.adsa06.cbm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

@DisplayName("CustomBundleManager")
class CustomBundleManagerTest {
 
    private CustomBundleManager cbm;
 
    @BeforeEach
    void setUp() {
        cbm = new CustomBundleManager("sentences.sentences", "A");
    }
 
    @Nested
    @DisplayName("Con el bundle 'A' recién cargado")
    class ConBundleA {
 
        @Test
        @DisplayName("devuelve los valores simples tal cual")
        void devuelveValoresSimples() {
            assertEquals("A", cbm.getString("sentence1"));
            assertEquals("AA", cbm.getString("sentence2"));
        }
 
        @Test
        @DisplayName("lanza NullPointerException si la clave no existe en A")
        void claveInexistenteLanzaNPE() {
            assertThrowsExactly(NullPointerException.class, () -> cbm.getString("sentence3"));
        }
 
        @Test
        @DisplayName("una clave con valor vacío devuelve cadena vacía, no null")
        void valorVacioDevuelveCadenaVacia() {
            assertEquals("", cbm.getString("sentence5"));
        }
 
        @Test
        @DisplayName("MessageFormat: las comillas simples dobles ('') se interpretan como una comilla literal")
        void comillasDoblesSeInterpretanComoLiteral() {
            // Gotcha clásico de MessageFormat: una comilla simple sola inicia una
            // sección literal; para escribir una comilla literal hay que duplicarla.
            // "special=It''s {0}" -> "It's <arg>"
            assertEquals("It's ok", cbm.getString("special", "ok"));
        }
    }
 
    @Nested
    @DisplayName("getString con argumentos")
    class GetStringConArgumentos {
 
        @BeforeEach
        void cargarB() {
            cbm.loadBundle("B");
        }
 
        @Test
        @DisplayName("sustituye un único argumento en el patrón")
        void unArgumento() {
            assertEquals("BB b BB", cbm.getString("sentence3", "b"));
        }
 
        @Test
        @DisplayName("sustituye múltiples argumentos respetando el orden de los índices")
        void multiplesArgumentosEnOrden() {
            assertEquals("Suma de 2 y 3", cbm.getString("sentence4", 2, 3));
        }
 
        @Test
        @DisplayName("ignora argumentos sobrantes si el patrón no los referencia")
        void argumentosDeMasSeIgnoran() {
            assertEquals("BB", cbm.getString("sentence2", "sobra1", "sobra2"));
        }
 
        @Test
        @DisplayName("no pasar argumentos cuando el patrón los necesita deja el placeholder tal cual")
        void sinArgumentosDejaPlaceholder() {
            // MessageFormat no lanza excepción si faltan argumentos: simplemente
            // no sustituye ese índice.
            assertEquals("BB {0} BB", cbm.getString("sentence3"));
        }
    }
 
    @Nested
    @DisplayName("loadBundle - recarga del bundle")
    class RecargaDeBundle {
 
        @Test
        @DisplayName("cambia todas las claves compartidas al pasar de A a B")
        void cambiaClavesCompartidas() {
            cbm.loadBundle("B");
            assertEquals("B", cbm.getString("sentence1"));
            assertEquals("BB", cbm.getString("sentence2"));
        }
 
        @Test
        @DisplayName("las claves exclusivas de A desaparecen tras cargar B")
        void clavesExclusivasDeADesaparecen() {
            assertEquals("Exclusivo de A", cbm.getString("onlyInA"));
            cbm.loadBundle("B");
            assertThrowsExactly(NullPointerException.class, () -> cbm.getString("onlyInA"));
        }
 
        @Test
        @DisplayName("las claves exclusivas de B aparecen tras cargar B")
        void clavesExclusivasDeBAparecen() {
            cbm.loadBundle("B");
            assertEquals("Exclusivo de B", cbm.getString("onlyInB"));
        }
 
        @Test
        @DisplayName("recargar el mismo target dos veces no cambia el resultado")
        void recargarMismoTargetEsIdempotente() {
            String antes = cbm.getString("sentence1");
            cbm.loadBundle("A");
            assertEquals(antes, cbm.getString("sentence1"));
        }
 
        @Test
        @DisplayName("se puede alternar entre A y B varias veces")
        void idaYVueltaEntreBundles() {
            cbm.loadBundle("B");
            assertEquals("B", cbm.getString("sentence1"));
            cbm.loadBundle("A");
            assertEquals("A", cbm.getString("sentence1"));
            cbm.loadBundle("B");
            assertEquals("B", cbm.getString("sentence1"));
        }
    }
 
    @Nested
    @DisplayName("Manejo de errores")
    class ManejoDeErrores {
 
        @Test
        @DisplayName("target inexistente lanza RuntimeException con IllegalArgumentException como causa")
        void targetInexistenteLanzaRuntimeException() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> new CustomBundleManager("sentences.sentences", "Z"));
 
            assertInstanceOf(IllegalArgumentException.class, ex.getCause());
            assertTrue(ex.getCause().getMessage().contains("sentences_Z.properties"));
        }
 
        @Test
        @DisplayName("baseName cuyo paquete no existe también lanza RuntimeException")
        void baseNameInexistenteLanzaRuntimeException() {
            assertThrows(RuntimeException.class,
                    () -> new CustomBundleManager("paquete.que.no.existe", "A"));
        }
 
        @Test
        @DisplayName("loadBundle con recurso inexistente deja el bundle vacío (clear antes del fallo)")
        void loadBundleFallidoDejaBundleVacio() {
            // bundle.clear() se ejecuta ANTES de intentar leer el recurso,
            // así que si loadBundle falla, las claves previas también se pierden.
            assertEquals("A", cbm.getString("sentence1"));
 
            assertThrows(RuntimeException.class, () -> cbm.loadBundle("Z"));
 
            assertThrowsExactly(NullPointerException.class, () -> cbm.getString("sentence1"));
        }
    }
 
    @Nested
    @DisplayName("Resolución de rutas de recursos (getResourcePath)")
    class ResolucionDeRutas {
 
        private String invocarGetResourcePath(String baseName, String target) throws Exception {
            Method m = CustomBundleManager.class
                    .getDeclaredMethod("getResourcePath", String.class, String.class);
            m.setAccessible(true);
            return (String) m.invoke(cbm, baseName, target);
        }
 
        @Test
        @DisplayName("convierte cada punto del baseName en una barra '/'")
        void convierteTodosLosPuntos() throws Exception {
            assertEquals("sentences/sentences_A.properties",
                    invocarGetResourcePath("sentences.sentences", "A"));
        }
 
        @Test
        @DisplayName("un baseName vacío no añade ninguna carpeta")
        void baseNameVacioNoAnadeCarpeta() throws Exception {
            assertEquals("_A.properties", invocarGetResourcePath("", "A"));
        }
 
        @Test
        @DisplayName("un baseName sin puntos se usa tal cual como carpeta")
        void baseNameSinPuntos() throws Exception {
            assertEquals("sentences_A.properties", invocarGetResourcePath("sentences", "A"));
        }
    }
}