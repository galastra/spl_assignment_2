package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailableEvent implements Event<String> {
    private String booktitle;

    public CheckAvailableEvent(String _booktitle){booktitle = _booktitle;}

    public String getBooktitle(){return booktitle;}

}
