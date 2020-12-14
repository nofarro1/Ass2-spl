package bgu.spl.mics.application.services;


import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;

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
    private Ewoks ewoks;

    public HanSoloMicroservice()
    {
        super("Han");
        ewoks= Ewoks.getInstance();
    }


    @Override
    protected void initialize() {
        this.subscribeEvent(AttackEvent.class, c -> {
            System.out.println("HanSolo start AttackEvent");
            List<Integer> requiredEwoks = c.getSerials();
            //synchronized (ewoks) {
                for (ListIterator i = requiredEwoks.listIterator(); i.hasNext(); ) {
                    Integer t = (Integer) i.next();
                    Ewok curr = ewoks.getEwok(t.intValue());
                    synchronized (curr) {
                        while (!curr.isAvailable())
                            curr.wait();
                        curr.acquire();
                    }
                }
                try {
                    Thread.sleep(c.getDuration());
                } catch (InterruptedException e) {}
                for (ListIterator i = requiredEwoks.listIterator(); i.hasNext(); ) {
                    Integer t = (Integer) i.next();
                    Ewok curr = ewoks.getEwok(t.intValue());
                    synchronized (curr) {
                        curr.release();
                        curr.notifyAll();
                    }
                }
            //}
            Diary.getInstance().setTotalAttacks();
            complete(c, true);
            System.out.println("HanSolo complete AttackEvent");
            });
        // subscribe to the Broadcast that comes after han finish his attacks
        subscribeBroadcast(FinishBroadcast.class, c -> {
            Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
            System.out.println("HanSolo finish all his AttackEvent");
        });

        // subscribeBroadcast and implement the call function for terminateBroadcast
            subscribeBroadcast(TerminateBroadcast.class, c -> {
                Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
                System.out.println("HanSolo terminated");
            this.terminate();
        });

    }
}
