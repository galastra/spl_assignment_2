package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * A broadcast message that is sent at every passed clock tick.
 * This message must contain the current tick(int).
 */
public class TickBroadcast implements Broadcast{
    private int curr_tick;
    private int duration; //milliseconds

    public TickBroadcast(int _duration,int _curr_tick){
        curr_tick = _curr_tick;
        duration = _duration;
    }

    public int getCurr_tick(){
        return curr_tick;
    }
     public int getDuration(){return duration;}
}
