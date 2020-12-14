package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;

import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static Ewoks instance;
    private Ewok [] ewoks;

    private static class SingleHolder{
        private static Ewoks instance = new Ewoks();
    }

    public static Ewoks getInstance(){
        return Ewoks.SingleHolder.instance;
    }

    public void makeEwoks (int size)
    {
        ewoks = new Ewok[size+1];
        for(int i=1 ; i<ewoks.length ; i++)
            ewoks[i] = new Ewok(i);
    }

    public Ewok getEwok(int i){return ewoks[i];}
}
