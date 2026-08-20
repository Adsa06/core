package io.github.adsa06.cbm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomBundleManagerTest {
    
    private CustomBundleManager cbm;
 
    @BeforeEach
    void setUp() {
        cbm = new CustomBundleManager("sentences.sentences", "A");
    }

    @Test
    void testA() {
        assertEquals(cbm.getString("sentence1"),"A");
        assertEquals(cbm.getString("sentence2"),"AA");
        assertThrowsExactly(NullPointerException.class, () -> cbm.getString("sentence3"));
    }

    @Test
    void testChangeAndB() {
        cbm.loadBundle("B");
        assertEquals(cbm.getString("sentence1"),"B");
        assertEquals(cbm.getString("sentence2"),"BB");
        assertEquals(cbm.getString("sentence3", "b"),"BB b BB");
    }
}