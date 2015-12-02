package org.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.objects.SeatHold;
import org.service.TicketService;
import org.service.config.TicketsConfig;
import org.service.exception.InvalidRequestException;
import org.service.exception.SeatHoldNotFoundException;
import org.service.exception.TicketServiceException;
import org.service.exception.TicketsUnavilableException;

/**
 * Implementation class for ticketing service.
 * @author a576709
 *
 */
public class TicketServiceImpl implements TicketService{
	
	final static Logger logger = Logger.getLogger(TicketServiceImpl.class);
	
	
	/* (non-Javadoc)
	 * @see org.service.TicketService#numSeatsAvailable(java.util.Optional)
	 */
	public int numSeatsAvailable(Optional<Integer> venueLevel) throws TicketServiceException {
		logger.debug("Entered method numSeatsAvailable : venueLevel"+venueLevel);
		int numSeats = 0;
		TicketsConfig config = TicketsConfig.getInstance();
		if(venueLevel.isPresent()){
			if(!isValidVenueLevel(venueLevel.get())){
				logger.error("Inavlid Request");
				throw new InvalidRequestException("Invalid Request");
			}			
			numSeats = config.getNonReservedSeats(venueLevel.get())-config.getHeldSeats(venueLevel.get());
		} else {
			numSeats = config.getTotalNonReservedSeats()-config.getTotalHeldSeats();
		}
		logger.debug("Returning numSeats:"+numSeats);
		return numSeats;
	}

	/* (non-Javadoc)
	 * @see org.service.TicketService#findAndHoldSeats(int, java.util.Optional, java.util.Optional, java.lang.String)
	 */
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel,
			Optional<Integer> maxLevel, String customerEmail) throws TicketServiceException {
		logger.debug("Entered method findAndHoldSeats : numSeats"+numSeats+" minLevel:"+minLevel+" maxlevel:"+maxLevel+" email:"+customerEmail);
		if(!vaildateHoldRequest(numSeats, minLevel, maxLevel, customerEmail)){
			logger.error("Inavlid Request");
			throw new InvalidRequestException("Invalid Request");
		}
		TicketsConfig config = TicketsConfig.getInstance();
			
		int minLevelId = minLevel.isPresent()?minLevel.get():config.getMinLevelId();
		int maxLevelId = maxLevel.isPresent()?maxLevel.get():config.getMaxLevelId();
		
		SeatHold seatHold = new SeatHold();
		seatHold.setCustomerEmail(customerEmail);
		seatHold.setNumOfSeats(numSeats);
		BigDecimal price = BigDecimal.ZERO;
		for(int i = minLevelId; i <= maxLevelId && numSeats > 0; i++){
			int availableSeatsInLevel = numSeatsAvailable(Optional.of(i));
			seatHold.getSeatMap().put(i, availableSeatsInLevel > numSeats ? numSeats : availableSeatsInLevel);
			numSeats -= availableSeatsInLevel;
			price.add(config.getPricingMap().get(i).multiply(BigDecimal.valueOf(seatHold.getSeatMap().get(i))));
		}
		if(numSeats > 0){
			logger.error("Tickets Unavailable");
			throw new TicketsUnavilableException("Tickets Unavailable");
		}
		seatHold.setPrice(price);
		
		// call persistent storage service
		// below nano time set is temporary, normally it comes form the storage system.
		seatHold.setSeatHoldId(System.nanoTime());

		// only after data is persisted, modify config accordingly
		synchronized (config) {
			config.getCustomerSeatHoldMap().put(seatHold.getSeatHoldId(), seatHold);
			for(Integer levelId: seatHold.getSeatMap().keySet()){
				config.getSeatHoldMap().put(levelId, config.getSeatHoldMap().get(levelId) + seatHold.getSeatMap().get(levelId));
			}
		}
		// spawn a thread to reverse the above synchronized block after the given amount of time
		// config.getSeatHoldExpiryInSeconds(), and keep config.seatHoldMap up to date
		logger.debug("SeatHold:"+seatHold);
		return seatHold;
	}

	/* (non-Javadoc)
	 * @see org.service.TicketService#reserveSeats(long, java.lang.String)
	 */
	public String reserveSeats(long seatHoldId, String customerEmail) throws TicketServiceException {
		logger.debug("Entered method reserveSeats : seatHoldId"+seatHoldId+" email:"+customerEmail);
		String reservationId = null;
		TicketsConfig config = TicketsConfig.getInstance();
		SeatHold seatHold = config.getCustomerSeatHoldMap().get(seatHoldId);
		if(!validateReservationRequest(seatHoldId, customerEmail, seatHold)){
			logger.error("Invalid Seat Hold request");
			throw new SeatHoldNotFoundException("Invalid Seat Hold request");
		}
		//call persistent storage and mark the seat hold in to reservation
		// temporary setting , comes from storage system
		reservationId = "reservation"+seatHoldId;
		
		// modify config accordingly
		synchronized (config) {
			for(Integer levelId: seatHold.getSeatMap().keySet()){
				config.getNonReservedSeatMap().put(levelId, config.getNonReservedSeatMap().get(levelId) - seatHold.getSeatMap().get(levelId));
			}
			config.getCustomerSeatHoldMap().remove(seatHold.getSeatHoldId());
		}
		logger.debug("ReservationId :"+reservationId);
		return reservationId;
	}
	
	/**
	 * Method to validate venue level id.
	 * @param venueLevel 
	 * @return true or false
	 */
	private boolean isValidVenueLevel(Integer venueLevel){
		return TicketsConfig.getInstance().getLevelIdsSet().contains(venueLevel);
	}
	
	/**
	 * Method to validate incoming request to method {@link TicketService}.findAndHoldSeats
	 * @param numSeats
	 * @param minLevel
	 * @param maxLevel
	 * @param customerEmail
	 * @return true or false
	 */
	private boolean vaildateHoldRequest(int numSeats, Optional<Integer> minLevel,
			Optional<Integer> maxLevel, String customerEmail){		
		
		return !(
				(numSeats <= 0) ||
				(minLevel.isPresent() && !isValidVenueLevel(minLevel.get())) ||
				(maxLevel.isPresent() && !isValidVenueLevel(maxLevel.get())) ||
				(minLevel.isPresent() && maxLevel.isPresent() && minLevel.get() > maxLevel.get())
				// || validate email address too
				);
		
	}
	
	/**
	 * Method to validate incoming request to method {@link TicketService}.reserveSeats
	 * @param seatHoldId
	 * @param customerEmail
	 * @param seatHold
	 * @return true or false
	 */
	private boolean validateReservationRequest(long seatHoldId, String customerEmail, SeatHold seatHold){
		boolean isValidRequest = Boolean.FALSE;
		if(seatHold!= null && customerEmail !=null && customerEmail.equalsIgnoreCase(seatHold.getCustomerEmail())){
			isValidRequest = Boolean.TRUE;
		} else {
			// look up db and throw an exception saying SeatHold Request is expired. 
		}
		return isValidRequest;
	}
	
	

}
