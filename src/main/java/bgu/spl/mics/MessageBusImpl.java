package bgu.spl.mics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
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
	private AtomicInteger totalAttacks;



	private static class SingleHolder{
		private static MessageBus instance = new MessageBusImpl();
	}

	// returns the instance
	public static MessageBus getInstance(){
		return MessageBusImpl.SingleHolder.instance;
	}
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (registeredByEventType){
			registeredByEventType.putIfAbsent(type, new ConcurrentLinkedQueue<MicroService>());
			registeredByEventType.get(type).add(m); // other microservices can't access the queue because the map in locked
			registeredByEventType.notifyAll();
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (registeredByBroadcastType){
			registeredByBroadcastType.putIfAbsent(type, new ConcurrentLinkedQueue<MicroService>());
			registeredByBroadcastType.get(type).add(m); // other microservices can't access the queue because the map in locked
			registeredByBroadcastType.notifyAll();
		}
	}


	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		synchronized (eventsAndFuture) {
			eventsAndFuture.get(e).resolve(result);
			eventsAndFuture.remove(e);
			eventsAndFuture.notifyAll();
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (registeredByBroadcastType.get(b.getClass()) != null) {
			synchronized (microservicesQueues) {
				for (MicroService m : registeredByBroadcastType.get(b.getClass())) {

					microservicesQueues.get(m).add(b);
				}
				microservicesQueues.notifyAll();
			}
		}
	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// the blocking and concurrent queues takes care on the multiThreading, therefore theres no need in 'synchronized'
		MicroService m;
		if (registeredByEventType.get(e.getClass()) != null && !registeredByEventType.get(e.getClass()).isEmpty()) {
			synchronized (registeredByEventType) {
				m = registeredByEventType.get(e.getClass()).poll();
				if (microservicesQueues.get(m) != null) {
					synchronized (microservicesQueues) {
						microservicesQueues.get(m).add(e);
						microservicesQueues.notifyAll();
					}
					Future<T> f = new Future<>();
					synchronized (eventsAndFuture) {
						eventsAndFuture.put(e, f);
						eventsAndFuture.notifyAll();
					}
					registeredByEventType.get(e.getClass()).add(m);
					return f;
				}
				registeredByEventType.notifyAll();
			}
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		synchronized (microservicesQueues) {
			microservicesQueues.putIfAbsent(m, new LinkedBlockingQueue<Message>());
		}
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
			if (microservicesQueues.contains(m))
				microservicesQueues.remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message message;
		synchronized (microservicesQueues.get(m)) {
			while (microservicesQueues.get(m).isEmpty())
				microservicesQueues.get(m).wait();
			message = microservicesQueues.get(m).poll();
		}
		return message;
	}

	public Object getQueue(String name) { return null; }

}
