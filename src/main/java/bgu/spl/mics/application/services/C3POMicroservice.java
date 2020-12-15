package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;
import java.util.ListIterator;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
    private Ewoks ewoks;
    private Diary diary;
	
    public C3POMicroservice() {
        super("C3PO");
        ewoks = Ewoks.getInstance();
    }

    @Override
    protected void initialize() {
        // subscribeEvent and implement the call function for AttackEvent
        subscribeEvent(AttackEvent.class, c -> {
            // acquire the Ewoks
            List<Integer> requiredEwoks = c.getSerials();
            for (ListIterator i = requiredEwoks.listIterator(); i.hasNext(); ) {
                Integer t = (Integer) i.next();
                Ewok curr = ewoks.getEwok(t.intValue());
                synchronized (curr) {
                    while (!curr.isAvailable()) {
                        curr.wait();
                    }
                    curr.acquire();
                }
            }
            Thread.sleep(c.getDuration());

            // release the ewoks
            for (ListIterator i = requiredEwoks.listIterator(); i.hasNext(); ) {
                Integer t = (Integer) i.next();
                Ewok curr = ewoks.getEwok(t.intValue());
                synchronized (curr) {
                    curr.release();
                    curr.notifyAll();
                }
            }
            complete(c, true);
            Diary.getInstance().setTotalAttacks();
        });

        // subscribe to the Broadcast that comes after c3po finish his attacks
        subscribeBroadcast(FinishBroadcast.class, c -> {
            Diary.getInstance().setC3POFinish(System.currentTimeMillis());
        });

        // subscribeBroadcast and implement the call function for terminateBroadcast
        subscribeBroadcast(TerminateBroadcast.class, c -> {
            Diary.getInstance().setC3POTerminate(System.currentTimeMillis());
            this.terminate();
        });

    }
}
