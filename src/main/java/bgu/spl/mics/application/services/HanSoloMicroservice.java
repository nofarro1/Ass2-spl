package bgu.spl.mics.application.services;


import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.example.DeactivationEventBroadcast;
import bgu.spl.mics.example.EventAttackBroadcast;

import javax.swing.text.html.HTMLDocument;
import java.util.List;
import java.util.ListIterator;

import static java.lang.Thread.sleep;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private MessageBus messageBus;
    private Ewoks ewoks;

    public HanSoloMicroservice()
    {
        super("Han");
        messageBus = MessageBusImpl.getInstance(); // TODO**********
        ewoks= Ewoks.getInstance();
    }


    @Override
    protected void initialize() {
        messageBus.register(this);
        this.subscribeEvent(AttackEvent.class, c -> {
            List<Integer> requiredEwoks = c.getSerials();
            for (ListIterator i = requiredEwoks.listIterator(); i.hasNext();)
            {
                Integer t = (Integer) i.next();
                Ewok curr = ewoks.getEwok(t.intValue());
                while(!curr.isAvailable())
                    wait();
                curr.acquire();
            }
            try {
                Thread.sleep(c.getDuration()); // TODO**********
            }
            catch (InterruptedException e) {}
            for (ListIterator i = requiredEwoks.listIterator(); i.hasNext();)
            {
                Integer t = (Integer) i.next();
                Ewok curr = ewoks.getEwok(t.intValue());
                curr.release();
                notifyAll();
            }
            complete(c,true);
        });
    }
}
