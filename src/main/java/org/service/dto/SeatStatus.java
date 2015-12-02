package org.service.dto;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Set;

import org.service.enums.Status;

public class SeatStatus {
	
	private long id;
	private long seatHoldId;
	private String reservationId;
	private int numOfSeats;
	private BigDecimal price;
	private String customeEmail;
	private Set<SeatLevelStatus> set;
	private Status status;
	private Calendar seatHoldCalendar;
	private Calendar seatHoldExpiryCalendar;
	private Calendar seatReserveCalendar;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getSeatHoldId() {
		return seatHoldId;
	}
	public void setSeatHoldId(long seatHoldId) {
		this.seatHoldId = seatHoldId;
	}
	public String getReservationId() {
		return reservationId;
	}
	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}
	public int getNumOfSeats() {
		return numOfSeats;
	}
	public void setNumOfSeats(int numOfSeats) {
		this.numOfSeats = numOfSeats;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getCustomeEmail() {
		return customeEmail;
	}
	public void setCustomeEmail(String customeEmail) {
		this.customeEmail = customeEmail;
	}
	public Set<SeatLevelStatus> getSet() {
		return set;
	}
	public void setSet(Set<SeatLevelStatus> set) {
		this.set = set;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Calendar getSeatHoldCalendar() {
		return seatHoldCalendar;
	}
	public void setSeatHoldCalendar(Calendar seatHoldCalendar) {
		this.seatHoldCalendar = seatHoldCalendar;
	}
	public Calendar getSeatHoldExpiryCalendar() {
		return seatHoldExpiryCalendar;
	}
	public void setSeatHoldExpiryCalendar(Calendar seatHoldExpiryCalendar) {
		this.seatHoldExpiryCalendar = seatHoldExpiryCalendar;
	}
	public Calendar getSeatReserveCalendar() {
		return seatReserveCalendar;
	}
	public void setSeatReserveCalendar(Calendar seatReserveCalendar) {
		this.seatReserveCalendar = seatReserveCalendar;
	}
	
	
	

}
