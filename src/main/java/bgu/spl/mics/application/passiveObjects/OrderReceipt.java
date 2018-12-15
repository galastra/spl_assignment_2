package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {

	private int _orderid; //the id of the order
	private String _seller; //the name of the service which handled the order
	private int _customer; //the id of the customer the receipt is issued to
	private String _book_title; //title of the book bought
	private int _price; //the price the customer paid for the book
	private int _issued_tick; //tick in which this receipt was issued (upon completing the corresponding event)
	private int _order_tick; //tick in which the customer ordered the book
	private int _process_tick; //tick in which the selling service started processing the order

	public OrderReceipt(int _orderid,String _seller,int _customer,String _book_title, int _price,int _issued_tick,int _order_tick,int _process_tick){
		this._orderid = _orderid;
		this._seller = _seller;
		this._customer = _customer;
		this._book_title = _book_title;
		this._price = _price;
		this._issued_tick = _issued_tick;
		this._order_tick = _order_tick;
		this._process_tick = _process_tick;
	}
	
	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {
		return _orderid;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return _seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return _customer;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return _book_title;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return _price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {
		return _issued_tick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase request.
     */
	public int getOrderTick() {
		return _order_tick;
	}
	
	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {
		return _process_tick;
	}


}
