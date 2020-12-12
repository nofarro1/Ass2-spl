package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.example.DeactivationEventBroadcast;
import bgu.spl.mics.example.EventAttackBroadcast;

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
        messageBus.subscribeBroadcast(EventAttackBroadcast.class,this); // TODO*******
        for(int i=0;i<attackEvents.length;i++) {
            //attacks[i].sorted(); // TODO**********
            attackEvents[i] = new AttackEvent(attacks[i]);
            messageBus.sendEvent(attackEvents[i]);
        }
        while (!messageBus.IsFinishAllAttacks())
            wait();
        DeactivationEvent deactivationEvent = new DeactivationEvent();
        messageBus.sendEvent(deactivationEvent);
        while (!messageBus.IsFinishDeactivation())  //for deactivationEvent
            wait();
        BombDestroyerEvent bombDestroyerEvent = new BombDestroyerEvent();
        messageBus.sendEvent(bombDestroyerEvent);
    }
}
