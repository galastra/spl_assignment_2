package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.List;
import bgu.spl.mics.application.passiveObjects.Models.JSON_Services.CreditCard;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
	private int id;
	private String name;
	private String address;
	private int distance;
	private List<OrderReceipt> receipts;
	private int credit_card;
	private int available_amount_credit_card;

	public Customer(){}

	public Customer(int _id, String _name, String _address, int _distance, List<OrderReceipt> _receipts, int _credit_card, int _available_amount_credit_card){
		id = _id;
		name = _name;
		address = _address;
		distance = _distance;
		receipts = _receipts;
		credit_card = _credit_card;
		available_amount_credit_card = _available_amount_credit_card;
	}

	public Customer(Customer other){
		this(other.id,other.name,other.address,other.distance,other.receipts,other.credit_card,other.available_amount_credit_card);
	}
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return receipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return available_amount_credit_card;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return credit_card;
	}

	/**
	 * Charges the customer with an amount of money
	 * @param amount the amount
	 */
	public void chargeCredit(int amount){
		synchronized (this) {
			available_amount_credit_card -= amount;
		}
	}

	public void setCreditCard(CreditCard card){
		this.credit_card = card.getNumber();
		this.available_amount_credit_card = card.getAmount();
	}
	
}
