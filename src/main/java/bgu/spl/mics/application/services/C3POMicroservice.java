package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
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
    private MessageBus messageBus;
    private Ewoks ewoks;
	
    public C3POMicroservice() {
        super("C3PO");
        messageBus = MessageBusImpl.getInstance();
        ewoks = Ewoks.getInstance();
    }

    @Override
    protected void initialize() {
        messageBus.register(this);

        // subscribeEvent and implement the call function for AttackEvent
        subscribeEvent(AttackEvent.class, c -> {
                    List<Integer> requiredEwoks = c.getSerials();
                    for (ListIterator i = requiredEwoks.listIterator(); i.hasNext(); ) {
                        Integer t = (Integer) i.next();
                        Ewok curr = ewoks.getEwok(t.intValue());
                        while (!curr.isAvailable())
                            wait();
                        curr.acquire();
                    }
                    try {
                        Thread.sleep(c.getDuration());
                    } catch (InterruptedException e) { }
                    for (ListIterator i = requiredEwoks.listIterator(); i.hasNext(); ) {
                        Integer t = (Integer) i.next();
                        Ewok curr = ewoks.getEwok(t.intValue());
                        curr.release();
                        notifyAll();
                    }
                    complete(c, true);
                });

        // subscribeBroadcast and implement the call function for terminateBroadcast
        TerminateBroadcast terminateBroadcast = new TerminateBroadcast();
        subscribeBroadcast(terminateBroadcast.getClass(), c -> {
            // TODO: put all details in diary
            this.terminate();
        });

    }
}
