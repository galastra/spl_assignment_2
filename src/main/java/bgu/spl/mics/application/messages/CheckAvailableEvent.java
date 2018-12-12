package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderResult;

public class CheckAvailableEvent implements Event<OrderResult> {
    private String booktitle;

    public CheckAvailableEvent(String _booktitle){booktitle = _booktitle;}

    public String getBooktitle(){return booktitle;}

}
