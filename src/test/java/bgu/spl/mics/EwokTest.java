import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.Assertions;
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
        ewok.release();
        ewok.acquire();
        Assertions.assertTrue(ewok.isAvailable());
    }

    @Test
    public void testAcquireTrue(){
        ewok.acquire();
        Assertions.assertTrue(ewok.isAvailable());
    }

    @Test
    public void testReleaseTrue(){
        ewok.acquire();
        ewok.release();
        Assertions.assertFalse(ewok.isAvailable());
    }

    @Test
    public void testReleaseFalse(){
        ewok.release();
        Assertions.assertFalse(ewok.isAvailable());
    }
}