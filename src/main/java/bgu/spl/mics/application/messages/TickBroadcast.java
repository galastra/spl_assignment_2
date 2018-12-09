package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * A broadcast message that is sent at every passed clock tick.
 * This message must contain the current tick(int).
 */
public class TickBroadcast implements Broadcast{
    private static int curr_tick;
}
