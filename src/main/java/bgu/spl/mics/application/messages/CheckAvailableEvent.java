package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

//Event<integer> because we care about the price (and -1 if doesn't exist)
public class CheckAvailableEvent implements Event<Integer> {
    private String booktitle;

    public CheckAvailableEvent(String _booktitle){booktitle = _booktitle;}

    public String getBooktitle(){return booktitle;}

}
