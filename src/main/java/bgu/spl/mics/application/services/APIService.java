package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

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
	private List<OrderSchedule> Schedule;
	private Customer customer;
	private List<Future<OrderReceipt>> futures;
	private List<OrderReceipt> receipts;

	public APIService(List<OrderSchedule> _Schedule,Customer _customer) {
		super("API Service");
		this.futures=new LinkedList<>();
		this.receipts=new LinkedList<>();
		this.Schedule=_Schedule;
		Schedule.sort(new Comparator<OrderSchedule>() {
			@Override
			public int compare(OrderSchedule o1, OrderSchedule o2) {
				return o1.getTick()-o2.getTick();
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
		subscribeBroadcast(TickBroadcast.class,ev->{
			while (!Schedule.isEmpty() && ev.getCurr_tick() == Schedule.get(0).getTick()) {
				Future<OrderReceipt> temp= (Future<OrderReceipt>) sendEvent(new BookOrderEvent(Schedule.get(0).getBookTitle(), customer, ev.getCurr_tick(), Schedule.get(0).getOrderId()));
				futures.add(temp);
				Schedule.remove(0);
			}
			for (Future<OrderReceipt> temp: futures) {
				//maybe we should do a timed get because we cant know which one will end first;
				receipts.add(temp.get());
			}
		});



	}

}
