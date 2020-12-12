package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    public LandoMicroservice(long duration) {
        super("Lando");
    }

    @Override
    protected void initialize() {
        Callback callBack = new Callback() {
            @Override
            public void call(Object c) {
                // someEvent call
            }
        };
    }
}
