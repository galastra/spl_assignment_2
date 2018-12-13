package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link //BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //MoneyRegister}, {@link //Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private List<OrderSchedule> schedules;
	private Customer customer;
	private List<Future<OrderReceipt>> futures;
	private List<OrderReceipt> receipts;

	private AtomicInteger orderIdIndex;
	//I added it because the OrderId field in OrderSchedule has nothing to do with
	//the actual order id because we do not get it from the json file but it's the id
	//corresponding to the order id in the list

	public APIService(List<OrderSchedule> _schedules,Customer _customer) {
		super("API Service");
		this.futures=new LinkedList<>();
		this.receipts=new LinkedList<>();
		schedules.addAll(_schedules);
		orderIdIndex = new AtomicInteger(0);
		schedules.sort(new Comparator<OrderSchedule>() {
			@Override
			public int compare(OrderSchedule o1, OrderSchedule o2) {
				return Integer.compare(o1.getTick(),o2.getTick());
			}
		});
		this.customer=_customer;
	}

	public List<OrderReceipt> getReceipts() {
		return receipts;
	}

	@Override
	protected void initialize() {
		System.out.println("API Service "+getName()+" started");
		subscribeBroadcast(TickBroadcast.class,broadcast->{
			while (orderIdIndex.get() < schedules.size() && broadcast.getCurr_tick() == schedules.get(orderIdIndex.get()).getTick()) {
				Future<OrderReceipt> receiptFuture = (Future<OrderReceipt>) sendEvent(new BookOrderEvent(schedules.get(orderIdIndex.get()).getBookTitle(),
						customer, broadcast.getCurr_tick(), schedules.get(orderIdIndex.get()).getOrderId()));
				futures.add(receiptFuture);
				//schedules.remove(0);
				orderIdIndex.incrementAndGet();
			}
			for (Future<OrderReceipt> temp: futures) {
				//maybe we should do a timed get because we cant know which one will end first;
				receipts.add(temp.get());
			}
		});



	}

}
