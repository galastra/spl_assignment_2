package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.TimeUnit;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link //DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //MoneyRegister}, {@link //Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private int durationInMillis;

	public LogisticsService(int id) {
		super("Logistics Service " + id);
		durationInMillis = 0;

	}

	@Override
	protected void initialize() {
		System.out.println(getName()+" started");

		sendBroadcast(new ImHereBroadcast());

		subscribeEvent(DeliveryEvent.class,ev->{
			Future<Future<DeliveryVehicle>> futureDeliveryVehicle=sendEvent(new AcquireVehicleEvent());
			DeliveryVehicle deliveryVehicle = futureDeliveryVehicle.get().get(durationInMillis, TimeUnit.MILLISECONDS);
			if (deliveryVehicle != null) {
				deliveryVehicle.deliver(ev.getAddress(), ev.getDistance());
				sendBroadcast(new ReleaseVehicleBroadcast(deliveryVehicle));
			}
		});

		subscribeBroadcast(TickBroadcast.class,brod->{
			durationInMillis = brod.getDuration();
		});

		subscribeBroadcast(LastTickBroadcast.class,brod->{
			System.out.println(getName()+" terminates");
			terminate();
		});
	}

}
