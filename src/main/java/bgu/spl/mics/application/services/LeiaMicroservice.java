package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private MessageBusImpl messageBus;
    private Attack[] attacks;
    private AttackEvent [] attackEvents;
    private int count;
    private Future deactivateFuture;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        count=attacks.length;
        attackEvents = new AttackEvent[attacks.length];
        messageBus = (MessageBusImpl) MessageBusImpl.getInstance(); // TODO**********
    }

    @Override
    protected void initialize() throws InterruptedException {
        messageBus.register(this);

        // subscribe to terminate broadcast
        TerminateBroadcast terminateBroadcast = new TerminateBroadcast();
        subscribeBroadcast(terminateBroadcast.getClass(), c -> {
            Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
            this.terminate();

        });

        // send the attack events
        for(int i=0;i<attackEvents.length;i++) {
            attackEvents[i] = new AttackEvent(attacks[i]);
            sendEvent(attackEvents[i]);
        }

        // broadcast to know when hanSolo and c3po finish there attacks
        FinishBroadcast finish = new FinishBroadcast();
        sendBroadcast(finish);

        // when all the attacks complete, send deactivationEvent
        while (Diary.getInstance().getTotalAttacks().intValue() != attacks.length)
            wait();
        DeactivationEvent deactivationEvent = new DeactivationEvent();
        deactivateFuture = sendEvent(deactivationEvent);

        deactivateFuture.get(); // <- wait function
        BombDestroyerEvent bombDestroyerEvent = new BombDestroyerEvent();
        messageBus.sendEvent(bombDestroyerEvent);

    }
}
