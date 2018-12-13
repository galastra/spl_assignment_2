package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * An event that is sent when the BookOrderEvent is succesfully completed and a delivery is required.
 * Processing: should try to acquire a delivery vehicle and wait until it completes.
 * Otherwise, should the method deliver of the acquired delivery vehicle and wait until it completes.
 * Otherwise, should wait the vehicle becomes available.
 * It is sent to the LogisticsService once the order is succesfully completed.
 * The sender does not need to wait on the event since it does not return a value.
 */

public class DeliveryEvent implements Event {

    private int distance;
    private String address;

    public DeliveryEvent(int _distance,String _address)
    {
        this.address=_address;
        this.distance=_distance;
    }

    public int getDistance() {
        return distance;
    }

    public String getAddress() {
        return address;
    }
}
