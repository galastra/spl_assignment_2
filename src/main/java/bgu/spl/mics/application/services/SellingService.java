package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailableEvent;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link //MoneyRegister} singleton of the store.
 * Handles {@link //BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private MoneyRegister moneyRegister;

	public SellingService() {
		super("Selling Service");
		moneyRegister = MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {
		System.out.println("Selling Service "+getName()+" started");
		subscribeEvent(BookOrderEvent.class, ev-> {
			Future<OrderResult> futureObject = (Future<OrderResult>) sendEvent(
					new CheckAvailableEvent(ev.getBookTitle()));
			if (futureObject == null)
				System.out.println("No Micro-Service has registered to handle CheckAvailableEvent events! The event cannot be processed");
			else {
				OrderResult result = futureObject.get();

				switch (result) {
					case SUCCESSFULLY_TAKEN:
						complete(ev, OrderResult.SUCCESSFULLY_TAKEN);
						//TODO: figure out whether the SellingService should sendEvent to ResourceService
						// or it's InventoryService who should do it.
						break;

					case NOT_IN_STOCK:
						complete(ev, OrderResult.NOT_IN_STOCK);//perhaps BookOrderEvent shouldn't be Event<OrderResult>
						break;
				}
			}

		});

		
	}

}

