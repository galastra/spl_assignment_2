package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailableEvent;
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
		subscribeEvent(CheckAvailableEvent.class,checkCallBack->{
			complete(checkCallBack,inventory.checkAvailabiltyAndGetPrice(checkCallBack.getBooktitle()));
		});

		subscribeEvent(TakeBookEvent.class,takeCallBack->{
			complete(takeCallBack,inventory.take(takeCallBack.getBookTitle()));
		});
		
	}

}
