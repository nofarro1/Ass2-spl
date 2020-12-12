package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class DeactivationEvent implements Event<Boolean> {
	Future<String> future;

	public DeactivationEvent(){ future= new Future<>("");}

	public Future<String> getFuture(){
	    return future;
    }
}
