package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
	private int serialNumber;
	private volatile AtomicBoolean available;

	public Ewok(){}

	public Ewok(int serialNumber){
	    this.serialNumber=serialNumber;
	    available = new AtomicBoolean(true);
    }
    /**
     * Acquires an Ewok
     */
    public synchronized void acquire() {
		available.compareAndSet(true, false);
    }

    /**
     * release an Ewok
     */
    public synchronized void release() {
    	available.compareAndSet(false, true);
    }

    public boolean isAvailable(){return available.get();}
}
