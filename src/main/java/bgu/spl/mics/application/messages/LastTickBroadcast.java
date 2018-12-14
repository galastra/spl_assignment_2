package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * Send a broadcast the tic=duration in TimeService and
 * thus all services should be terminated
 */

public class LastTickBroadcast implements Broadcast {

    public LastTickBroadcast(){}

}
