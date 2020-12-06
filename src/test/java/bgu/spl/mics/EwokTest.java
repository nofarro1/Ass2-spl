package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    private Ewok ewok;

    @BeforeEach
    public void setUp(){
        ewok = new Ewok();
    }

    @Test
    public void testAcquireFalse(){
        ewok.available=false;
        ewok.acquire();
        assertTrue(ewok.available);
    }

    @Test
    public void testAcquireTrue(){
        ewok.available=true;
        ewok.acquire();
        assertTrue(ewok.available);
    }

    @Test
    public void testReleaseTrue(){
        ewok.available=true;
        ewok.release();
        assertFalse(ewok.available);
    }

    @Test
    public void testReleaseFalse(){
        ewok.available=false;
        ewok.release();
        assertFalse(ewok.available);
    }
}