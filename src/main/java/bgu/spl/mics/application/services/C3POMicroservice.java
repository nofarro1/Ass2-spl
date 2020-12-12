package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.Callback;

import java.sql.SQLOutput;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


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
        subscribeEvent(AttackEvent.class, c -> {
            List<Integer> requiredEwoks = c.getSerials();
        });
        messageBus.subscribeBroadcast();


    }
}
