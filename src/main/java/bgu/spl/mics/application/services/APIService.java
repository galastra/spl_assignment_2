package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.LastTickBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;

import java.util.*;
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
	/**I added it because the OrderId field in OrderSchedule has nothing to do with
	//the actual order id because we do not get it from the json file but it's the id
	corresponding to the order id in the list
	 **/

	public APIService(int id,List<OrderSchedule> _schedules,Customer _customer) {
		super("API Service "+id);
		this.futures=new ArrayList<>();
		this.receipts=new ArrayList<>();
		this.customer=_customer;
		orderIdIndex = new AtomicInteger(0);
		schedules = new ArrayList<>();
		synchronized (this) {
			schedules.addAll(_schedules);
			schedules.sort(new Comparator<OrderSchedule>() {
				@Override
				public int compare(OrderSchedule o1, OrderSchedule o2) {
					return Integer.compare(o1.getTick(), o2.getTick());
				}
			});
		}
	}

	public List<OrderReceipt> getReceipts() {
		return receipts;
	}

	@Override
	protected void initialize() {
		//System.out.println(getName()+" started");

		subscribeBroadcast(TickBroadcast.class,broadcast->{
			while (orderIdIndex.get() < schedules.size() && broadcast.getCurr_tick() == schedules.get(orderIdIndex.get()).getTick()) {
				Future<OrderReceipt> receiptFuture = (Future<OrderReceipt>) sendEvent(new BookOrderEvent(schedules.get(orderIdIndex.get()).getBookTitle(),
						customer, broadcast.getCurr_tick(),orderIdIndex.get()));
				futures.add(receiptFuture);
				orderIdIndex.incrementAndGet();
			}
		});

		subscribeBroadcast(LastTickBroadcast.class,lastickCallback->{
			for (Future<OrderReceipt> tempFuture: futures) {
				if (tempFuture.get() != null) { //TODO:figure out if a null receipt must be in the receipts
					receipts.add(tempFuture.get());
					customer.getCustomerReceiptList().add(tempFuture.get());
				}
			}
			//System.out.println(getName()+" terminates");
			terminate();
		});


	}

}
