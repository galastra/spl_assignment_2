package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleBroadcast implements Broadcast {
    private DeliveryVehicle vehicle;

    public ReleaseVehicleBroadcast(DeliveryVehicle _vehicle){
        vehicle=_vehicle;
    }

    public DeliveryVehicle getVehicle() {
        return vehicle;
    }
}
