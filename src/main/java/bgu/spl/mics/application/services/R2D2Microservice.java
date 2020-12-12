package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import static java.lang.Thread.sleep;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private MessageBusImpl messageBus;
    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration=duration;
        messageBus = (MessageBusImpl) MessageBusImpl.getInstance(); // TODO**********
    }

    @Override
    protected void initialize() {
        messageBus.register(this);
        subscribeEvent(DeactivationEvent.class, c -> {
            try{
                Thread.sleep(duration);
            }
            catch (InterruptedException e) {}
            complete(c,true);
            Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());
        });

        // subscribeBroadcast and implement the call function for terminateBroadcast
        TerminateBroadcast terminateBroadcast = new TerminateBroadcast();
        subscribeBroadcast(terminateBroadcast.getClass(), c -> {
            Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
            this.terminate();
        });
    }
}
