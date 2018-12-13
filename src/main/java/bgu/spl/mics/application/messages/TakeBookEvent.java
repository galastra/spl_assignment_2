package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderResult;

public class TakeBookEvent implements Event<OrderResult> {
    private String bookTitle;

    public TakeBookEvent(String _booktitle){
        bookTitle = _booktitle;
    }

    public String getBookTitle() {
        return bookTitle;
    }
}
