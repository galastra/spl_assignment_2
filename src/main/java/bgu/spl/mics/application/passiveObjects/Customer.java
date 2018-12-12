package bgu.spl.mics.application.passiveObjects;

import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer {
	private int _id;
	private String _name;
	private String _address;
	private int _distance;
	private List<OrderReceipt> _receipts;
	private int _credit_card;
	private int _available_amount_credit_card;


	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return _name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return _id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return _address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return _distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return _receipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return _available_amount_credit_card;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return _credit_card;
	}

	/**
	 * Charges the customer with an amount of money
	 * @param amount the amount
	 */
	public void chargeCredit(int amount){
		synchronized (this) {
			_available_amount_credit_card -= amount;
		}
	}
	
}
