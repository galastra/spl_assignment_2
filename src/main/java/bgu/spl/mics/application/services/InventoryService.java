package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailableEvent;
import bgu.spl.mics.application.messages.ImHereBroadcast;
import bgu.spl.mics.application.messages.LastTickBroadcast;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link //Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //MoneyRegister}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory;

	public InventoryService(int id) {
		super("InventoryService "+id);
		inventory = Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		System.out.println(getName()+" started");

		sendBroadcast(new ImHereBroadcast());

		subscribeEvent(CheckAvailableEvent.class,checkCallBack->{
			complete(checkCallBack,inventory.checkAvailabiltyAndGetPrice(checkCallBack.getBooktitle()));
		});

		subscribeEvent(TakeBookEvent.class,takeCallback->{
			complete(takeCallback,inventory.take(takeCallback.getBookTitle()));
		});

		subscribeBroadcast(LastTickBroadcast.class,lastTickCallback->{
			System.out.println(getName()+" terminates");
			terminate();
		});
		
	}

}
