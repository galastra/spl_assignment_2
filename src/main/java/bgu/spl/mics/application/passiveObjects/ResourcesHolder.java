package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private static volatile ResourcesHolder instance = null;
	private static Object mutex = new Object();
	private ConcurrentLinkedQueue<DeliveryVehicle> freeVehicles=new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> futuresWithOutVehicles=new ConcurrentLinkedQueue<>();

	/**
	 * Retrieves the single instance of this class.
	 */
	private ResourcesHolder(){
	}

	public static ResourcesHolder getInstance() {
		ResourcesHolder result = instance;
		if (result == null){
			synchronized (mutex){
				result = instance;
				if (result == null){
					instance = result = new ResourcesHolder();
				}
			}
		}
		return result;
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> temp=new Future<>();
		if(freeVehicles.isEmpty())
			futuresWithOutVehicles.add(temp);
		else
			temp.resolve(freeVehicles.poll());
		return temp;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		freeVehicles.add(vehicle);
		checkIfNeededAndProvide();
	}

	private void checkIfNeededAndProvide(){
		if(!futuresWithOutVehicles.isEmpty() & !freeVehicles.isEmpty())
			futuresWithOutVehicles.poll().resolve(freeVehicles.poll());
	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		for (DeliveryVehicle temp:vehicles) {
			freeVehicles.add(temp);
		}
	}

}
