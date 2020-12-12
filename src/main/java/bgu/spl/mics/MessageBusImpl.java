package bgu.spl.mics;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private volatile ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microservicesQueues= new ConcurrentHashMap();
	private volatile ConcurrentHashMap <Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> registeredByEventType= new ConcurrentHashMap();
	private volatile ConcurrentHashMap <Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> registeredByBroadcastType= new ConcurrentHashMap();
	private volatile ConcurrentHashMap <Event ,Future> eventsAndFuture = new ConcurrentHashMap();




	private static class InstanceHolder{
		private static MessageBus instance = new MessageBusImpl();
	}

	// returns the instance
	public static MessageBus getInstance(){
		return MessageBusImpl.InstanceHolder.instance;
	}
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		registeredByEventType.putIfAbsent(type, new ConcurrentLinkedQueue<MicroService>());
		registeredByEventType.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		registeredByBroadcastType.putIfAbsent(type, new ConcurrentLinkedQueue<MicroService>());
		registeredByBroadcastType.get(type).add(m);
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		eventsAndFuture.get(e).resolve(result);
		// TODO: PORQE?
		eventsAndFuture.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO ** do we need here synchronized? **
		if (registeredByBroadcastType.get(b) != null)
			for (MicroService m: registeredByBroadcastType.get(b)){
				microservicesQueues.get(m).add(b);
			}
	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// the blocking and concurrent queues takes care on the multiThreading, therefore theres no need in 'synchronized'
		MicroService m;
		if (registeredByEventType.get(e.getClass()) != null && !registeredByEventType.get(e.getClass()).isEmpty()) {
			m = registeredByEventType.get(e.getClass()).poll();
			if (microservicesQueues.get(m) != null) {
				microservicesQueues.get(m).add(e);
				notifyAll();
				Future<T> f = new Future<>();
				eventsAndFuture.put(e, f);
				registeredByEventType.get(e.getClass()).add(m);
				return f;
			}
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		microservicesQueues.putIfAbsent(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		microservicesQueues.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return microservicesQueues.get(m).take(); // 'take' function waits until theres message to pull from the queue
	}

	public Object getQueue(String name) { return null; }
}
