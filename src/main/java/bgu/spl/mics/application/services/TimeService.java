package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link //Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //MoneyRegister}, {@link //Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	//private static volatile TimeService instance = null;
	//private static Object mutex = new Object();

	////"There is only one instance of this micro-service" ?= Singleton
	private static AtomicInteger tick;
	private int speed;
	private int duration;
	private int ticks2go;

	public TimeService(int _speed,int _duration) {
		super("Time Service");
        speed = _speed;
        duration = _duration;
        ticks2go = 0;
	}

	@Override
	protected void initialize() {
		tick = new AtomicInteger(0);
		try {
			TimeUnit.MILLISECONDS.sleep(1);
			tick.incrementAndGet();
		}catch (Exception e){}
		
	}

}
