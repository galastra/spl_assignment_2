package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * A broadcast message that is sent at every passed clock tick.
 * This message must contain the current tick(int).
 */
public class TickBroadcast implements Broadcast{
    private int curr_tick;

    public TickBroadcast(int _curr_tick){curr_tick = _curr_tick;}

    public int getCurr_tick(){
        return curr_tick;
    }
}
