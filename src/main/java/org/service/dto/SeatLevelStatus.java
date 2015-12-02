package org.service.dto;

public class SeatLevelStatus {
	
	private long id;
	private long seatHoldId;
	private int levelId;
	private int numOfSeats;
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
	public int getLevelId() {
		return levelId;
	}
	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}
	public int getNumOfSeats() {
		return numOfSeats;
	}
	public void setNumOfSeats(int numOfSeats) {
		this.numOfSeats = numOfSeats;
	}
	
	

}
