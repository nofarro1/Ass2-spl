package bgu.spl.mics;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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


	private static class SingleHolder{
		private static MessageBus instance = new MessageBusImpl();
	}

	// returns the instance
	public static MessageBus getInstance(){
		return MessageBusImpl.SingleHolder.instance;
	}
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//synchronized (registeredByEventType) {
			registeredByEventType.putIfAbsent(type, new ConcurrentLinkedQueue<>());
			registeredByEventType.get(type).add(m);
		//}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {

		//synchronized (registeredByBroadcastType) {
			registeredByBroadcastType.putIfAbsent(type, new ConcurrentLinkedQueue<>());
			registeredByBroadcastType.get(type).add(m);
		//}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		eventsAndFuture.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (registeredByBroadcastType.get(b.getClass()) != null) {
			synchronized (microservicesQueues) {
				for (MicroService m : registeredByBroadcastType.get(b.getClass())) {
					synchronized (microservicesQueues.get(m)) {
						microservicesQueues.get(m).add(b);
						microservicesQueues.get(m).notifyAll();
					}
				}
			}
		}
	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

		MicroService m;
		Future<T> f = new Future<>();

		if (registeredByEventType.get(e.getClass()) != null && !registeredByEventType.get(e.getClass()).isEmpty()) {
			eventsAndFuture.putIfAbsent(e,f);
			synchronized (registeredByEventType.get(e.getClass())) {
				m = registeredByEventType.get(e.getClass()).poll(); // pull m from queue so the next one will receive the next event
				registeredByEventType.get(e.getClass()).add(m); // return m to the queue

				if (microservicesQueues.get(m) != null) {
					synchronized (microservicesQueues.get(m)) {
						microservicesQueues.get(m).add(e);
						microservicesQueues.get(m).notifyAll();
					}
					return f;
				}
			}

		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		microservicesQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {

		//remove m from the events queue
		synchronized (registeredByEventType) {
			for (Map.Entry<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> entry : registeredByEventType.entrySet()) {
				if (entry.getValue().contains(m))
					entry.getValue().remove(m);
			}
		}

		//remove m from the broadcast queue
		synchronized (registeredByBroadcastType) {
			for (Map.Entry<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> entry : registeredByBroadcastType.entrySet()) {
				if (entry.getValue().contains(m))
					entry.getValue().remove(m);
			}
		}

		//delete m's queue
		synchronized (microservicesQueues) {
			microservicesQueues.remove(m);
		}

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return microservicesQueues.get(m).take();
	}

	public Object getQueue(String name) { return null; }

}
