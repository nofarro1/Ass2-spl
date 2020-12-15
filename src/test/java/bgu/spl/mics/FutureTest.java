package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future future;

    @BeforeEach
    public void setUp(){
        future = new Future();
    }

    @Test
    public void c(){
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        assertTrue(str.equals(future.get()));
    }

    @Test
    public void getNoParamTest() {
        assertFalse(future.isDone());
        future.resolve("completed");
        assertTrue(future.get()=="completed");
        assertEquals ("completed",future.get());
    }

    @Test
    public void getWithParamTest() {
        future.resolve("completed");
        try {
            assertTrue(future.get(10, TimeUnit.NANOSECONDS)=="completed");
        } catch (InterruptedException e) {
            System.out.println("getWithParamTest Failed in the get function");
            e.printStackTrace();
        }
    }


}
