package org.service.exception;

public class TicketServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6789277987229553476L;
	
	public TicketServiceException(){
		super();
	}
	
	public TicketServiceException(String message){
		super(message);
	}

}
