package org.service.exception;

public class SeatHoldNotFoundException extends TicketServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1847937319024411086L;
	
	public SeatHoldNotFoundException(){
		super();
	}
	
	public SeatHoldNotFoundException(String message){
		super(message);
	}

}
