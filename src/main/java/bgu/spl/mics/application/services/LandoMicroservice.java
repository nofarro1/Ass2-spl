package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;

    }

    @Override
    protected void initialize() {
        MessageBus messageBus = MessageBusImpl.getInstance();
        messageBus.register(this);

        // subscribeBroadcast and implement the call function for terminateBroadcast
        TerminateBroadcast terminateBroadcast = new TerminateBroadcast();
        subscribeBroadcast(terminateBroadcast.getClass(), c -> {
            // TODO: put all the details in the diary
            this.terminate();
        });

        // subscribeBroadcast and implement the call function for terminateBroadcast
        subscribeEvent(BombDestroyerEvent.class, c -> {
            try{
                Thread.sleep(duration);
            }
            catch (InterruptedException e) {}
            sendBroadcast(terminateBroadcast);
            complete(c, true);
        });
    }
}
