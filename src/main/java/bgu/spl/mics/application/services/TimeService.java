package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
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
	private static volatile TimeService instance = null;
	private static Object mutex = new Object();

	private static AtomicInteger tick;
	private int speed;
	private int duration;

	public static TimeService getInstance(int _speed,int _duration){
		TimeService result = instance;
		if (result == null){
			synchronized (mutex){
				result = instance;
				if (result == null){
					instance = result = new TimeService(_speed,_duration);
				}
			}
		}
		return result;
	}

	private TimeService(int _speed,int _duration) {
		super("Time Service");
		speed = _speed;
		duration = _duration;
	}

	@Override
	protected void initialize() {
		tick = new AtomicInteger(1);
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				System.out.println("sending tick "+tick.intValue());
				sendBroadcast(new TickBroadcast(tick.getAndIncrement()));
				if (tick.compareAndSet(duration,tick.intValue())){
					timer.cancel();
					terminate();
				}

			}
		};
		timer.scheduleAtFixedRate(timerTask,0,speed);



	}

}
