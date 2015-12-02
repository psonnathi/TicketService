package org.objects;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SeatHold {
	
	private int numOfSeats;
	/**
	 * Holds levelId and number of seats assigned in that level.
	 */
	private Map<Integer, Integer> seatMap = new HashMap<Integer, Integer>();
	/**
	 * Id for the object
	 */
	private long seatHoldId;
	/**
	 * Total price for the reservation
	 */
	private BigDecimal price;
	/**
	 * Customer email
	 */
	private String customerEmail;

	public int getNumOfSeats() {
		return numOfSeats;
	}
	public void setNumOfSeats(int numOfSeats) {
		this.numOfSeats = numOfSeats;
	}
	public Map<Integer, Integer> getSeatMap() {
		return seatMap;
	}
	public void setSeatMap(Map<Integer, Integer> seatMap) {
		this.seatMap = seatMap;
	}
	public long getSeatHoldId() {
		return seatHoldId;
	}
	public void setSeatHoldId(long seatHoldId) {
		this.seatHoldId = seatHoldId;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	@Override
	public String toString() {
		return "SeatHold [numOfSeats=" + numOfSeats + ", seatMap=" + seatMap
				+ ", seatHoldId=" + seatHoldId + ", price=" + price
				+ ", customerEmail=" + customerEmail + "]";
	}

}
