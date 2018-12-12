package bgu.spl.mics;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServices = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> handleMessages = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Event,Future> futures = new ConcurrentHashMap<>();

	private static volatile MessageBusImpl instance = null; //galastra: volatile=נדיף
	private static Object mutex = new Object();

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBusImpl getInstance() {
		MessageBusImpl result = instance;
		if (result == null){
			synchronized (mutex){
				result  = instance;
				if (result == null){
					instance = result = new MessageBusImpl();
				}
			}
		}
		return result;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (handleMessages.containsKey(type)){
			synchronized (handleMessages) {
				handleMessages.get(type).add(m);
			}
		}
		else{
			synchronized (handleMessages) {
				ConcurrentLinkedQueue<MicroService> tmpServices = new ConcurrentLinkedQueue<>();
				tmpServices.add(m);
				handleMessages.put(type, tmpServices);
			}
		}

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (handleMessages.containsKey(type)){
			synchronized (handleMessages) {
				handleMessages.get(type).add(m);
			}
		}
		else{
			synchronized (handleMessages) {
				ConcurrentLinkedQueue<MicroService> tmpServices = new ConcurrentLinkedQueue<>();
				tmpServices.add(m);
				handleMessages.put(type, tmpServices);
			}
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		futures.get(e).resolve(result);

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for (MicroService service : handleMessages.get(b.getClass())){
			microServices.get(service).add(b);
		}


	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (!handleMessages.containsKey(e.getClass()))
			return null;
		synchronized (this) {
			ConcurrentLinkedQueue<MicroService> roundRobin = handleMessages.get(e.getClass());
			MicroService tmpMS = roundRobin.poll();
			roundRobin.add(tmpMS);
			microServices.get(tmpMS).add(e);
		}
		Future<T> tmpFuture = new Future<>();
		futures.put(e,tmpFuture);

		return tmpFuture;
	}

	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> tmp = new LinkedBlockingQueue<Message>();
		microServices.put(m,tmp);

	}

	@Override
	public void unregister(MicroService m) {
		if (microServices.contains(m)) {
			microServices.remove(m);
			for (ConcurrentHashMap.Entry<Class<? extends Message>,ConcurrentLinkedQueue<MicroService>> entry : handleMessages.entrySet()){ //removes m from the handleMessages
				ConcurrentLinkedQueue<MicroService> tmpQueue = entry.getValue();
				tmpQueue.remove(m);
				entry.setValue(tmpQueue);
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		//BlockingQueue<Message> temp=microServices.get(m);

		/*
		synchronized (this) {
			while (temp.isEmpty()) {
				this.wait();
			}
			//this.notifyAll();
		}
		*/
		Message msg = microServices.get(m).take();
		return msg;

	}

	

}
