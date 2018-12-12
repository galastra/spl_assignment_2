package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/**
 * An Event that is sent when a client of the store wishes to buy a book. Its expected response type is an OrderReceipt.
 * In the case that the order was not completed successfully, null should be returned as the event result.
 * Processing: if the book is available in the inventory ten the book should be taken, and the credit card of the customer should be charged.
 * If there is not enough money in the credit card, the order should be discarded,
 * and the book should not be taken from the inventory.
 * Sent by the WebAPI sevice, the WebAPI waits for this event to be complete to get the result.
 * The event is sent to a SellingService to handle it.
 */

public class BookOrderEvent implements Event<String>{
    private String bookTitle;
    private Customer customer;

    public BookOrderEvent(String _bookTitle,Customer _customer){
        bookTitle = _bookTitle;
        customer = _customer;
    }

    public String getBookTitle(){return bookTitle;}
    public Customer getCustomer(){return customer;}

}
