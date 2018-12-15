package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.LastTickBroadcast;
import bgu.spl.mics.application.messages.ReleaseVehicleBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link //ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //MoneyRegister}, {@link //Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private ResourcesHolder resourcesHolder;

	public ResourceService(int id) {
		super("Resource Service "+id);
		resourcesHolder=ResourcesHolder.getInstance();
	}

	@Override
	protected void initialize() {
		System.out.println(getName() +" started");

		subscribeEvent(AcquireVehicleEvent.class,ev->{
			//should return the future<Vehicle>? YES
			Future<DeliveryVehicle> temp=resourcesHolder.acquireVehicle();
			complete(ev,temp);
		});

		//should we do another event to release a the vehicle? YES

		subscribeBroadcast(ReleaseVehicleBroadcast.class,ev->{
			resourcesHolder.releaseVehicle(ev.getVehicle());
		});

		subscribeBroadcast(LastTickBroadcast.class,brod->{
			System.out.println(getName() +" terminates");
			terminate();
		});

	}

}
