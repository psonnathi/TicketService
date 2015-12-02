package ticketservice;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.objects.SeatHold;
import org.service.TicketService;
import org.service.config.TicketsConfig;
import org.service.exception.TicketServiceException;
import org.service.impl.TicketServiceImpl;

/**
 * Test class for {@link TicketService} implementation.
 * @author a576709
 *
 */
public class TicketServiceImplTest {
	
	private final String CUSTOMER_EMAIL = "xyz@xyz.com";
	
	TicketsConfig config;
	TicketService ticketservice;
    
    @Before
    public void initialize() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	ticketservice = new TicketServiceImpl();
    	// Use reflection to re create config singleton instance.
        Field instance = TicketsConfig.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        config = TicketsConfig.getInstance();
    }
    
    @After
    public void tearDown() {
        config = null;
        ticketservice = null;
    }
    
    @Rule
    public ExpectedException thrown= ExpectedException.none();
	
    /**
     * Test an invalid request to method {@link TicketService}.numSeatsAvailable
     * @throws TicketServiceException
     */
	@Test
	public void testNumSeatsAvailable_InvalidRequest1() throws TicketServiceException{
		int levelId = config.getMinLevelId();
		thrown.expect(TicketServiceException.class);
	    thrown.expectMessage("Invalid Request");
		ticketservice.numSeatsAvailable(Optional.of(levelId-1));
	}
	
	/**
     * Test an invalid request to method {@link TicketService}.numSeatsAvailable
     * @throws TicketServiceException
     */
	@Test
	public void testNumSeatsAvailable_InvalidRequest2() throws TicketServiceException{
		thrown.expect(TicketServiceException.class);
	    thrown.expectMessage("Invalid Request");
		ticketservice.numSeatsAvailable(Optional.of(config.getMaxLevelId()+1));
	}
	
	/**
     * Test a valid request to method {@link TicketService}.numSeatsAvailable
     * @throws TicketServiceException
     */
	@Test
	public void testNumSeatsAvailable_ValidRequest() throws TicketServiceException{		
		int levelId = config.getMinLevelId();
		int numSeats = ticketservice.numSeatsAvailable(Optional.empty());
		Assert.assertEquals(config.getMaxSeats(), numSeats);
		
		numSeats = ticketservice.numSeatsAvailable(Optional.of(levelId));
		Assert.assertEquals(config.getMaxNumSeats(levelId), numSeats);
		
		numSeats = ticketservice.numSeatsAvailable(Optional.of(config.getMaxLevelId()-1));
		Assert.assertEquals(config.getMaxNumSeats(config.getMaxLevelId()-1), numSeats);
	}
	
	/**
     * Test an invalid request to method {@link TicketService}.findAndHoldSeats
     * @throws TicketServiceException
     */
	@Test
	public void testFindAndHoldSeats_InvalidRequest1() throws TicketServiceException{
		int levelId = config.getMinLevelId();
		thrown.expect(TicketServiceException.class);
	    thrown.expectMessage("Invalid Request");
		ticketservice.findAndHoldSeats(1, Optional.of(levelId-1), Optional.empty(), CUSTOMER_EMAIL);
	}
	
	/**
     * Test an invalid request to method {@link TicketService}.findAndHoldSeats
     * @throws TicketServiceException
     */
	@Test
	public void testFindAndHoldSeats_InvalidRequest2() throws TicketServiceException{		
		thrown.expect(TicketServiceException.class);
	    thrown.expectMessage("Invalid Request");
		ticketservice.findAndHoldSeats(1, Optional.empty(), Optional.of(config.getMaxLevelId()+1) , CUSTOMER_EMAIL);
	}
	
	/**
     * Test an invalid request to method {@link TicketService}.findAndHoldSeats
     * @throws TicketServiceException
     */
	@Test
	public void testFindAndHoldSeats_InvalidRequest3() throws TicketServiceException{		
		thrown.expect(TicketServiceException.class);
	    thrown.expectMessage("Invalid Request");
		ticketservice.findAndHoldSeats(0, Optional.empty(), Optional.empty() , CUSTOMER_EMAIL);
	}
	
	/**
     * Test an invalid request to method {@link TicketService}.findAndHoldSeats
     * @throws TicketServiceException
     */
	@Test
	public void testFindAndHoldSeats_InvalidRequest4() throws TicketServiceException{		
		thrown.expect(TicketServiceException.class);
	    thrown.expectMessage("Tickets Unavailable");
		ticketservice.findAndHoldSeats(config.getMaxSeats()+1, Optional.empty(), Optional.empty() , CUSTOMER_EMAIL);
	}
	
	/**
     * Test an invalid request to method {@link TicketService}.findAndHoldSeats
     * @throws TicketServiceException
     */
	@Test
	public void testFindAndHoldSeats_InvalidRequest5() throws TicketServiceException{
		int levelId = config.getMinLevelId();
		thrown.expect(TicketServiceException.class);
	    thrown.expectMessage("Tickets Unavailable");
		ticketservice.findAndHoldSeats(config.getMaxNumSeats(levelId)+1, 
				Optional.of(levelId), Optional.of(levelId) , CUSTOMER_EMAIL);
	}
	
	/**
     * Test a valid request to method {@link TicketService}.findAndHoldSeats
     * @throws TicketServiceException
     */
	@Test
	public void testFindAndHoldSeats_ValidRequest1() throws TicketServiceException{		
		int reqTickets = 5;
		int levelId = config.getMinLevelId();
		SeatHold seatHold = ticketservice.findAndHoldSeats(reqTickets, 
				Optional.of(levelId), Optional.of(levelId) , CUSTOMER_EMAIL);
		Assert.assertEquals(reqTickets, seatHold.getNumOfSeats());
		Assert.assertEquals(reqTickets, seatHold.getSeatMap().get(levelId).intValue());
		Assert.assertEquals(1, seatHold.getSeatMap().size());
		Assert.assertEquals(reqTickets, seatHold.getSeatMap().get(levelId).intValue());
			
		// validate total number of seats should be reduced to by reqTickets and similarly in the requested level
		Assert.assertEquals(config.getMaxSeats()-reqTickets, ticketservice.numSeatsAvailable(Optional.empty()));
		Assert.assertEquals(config.getMaxNumSeats(levelId)-reqTickets, ticketservice.numSeatsAvailable(Optional.of(levelId)));
		
	}
	
	/**
     * Test a valid request to method {@link TicketService}.findAndHoldSeats
     * @throws TicketServiceException
     */
	@Test
	public void testFindAndHoldSeats_ValidRequest2() throws TicketServiceException{		
		// Hold 5 seats in a level
		
		int reqTickets = 5;
		int levelId = config.getMinLevelId();
		SeatHold seatHold = ticketservice.findAndHoldSeats(reqTickets, 
				Optional.of(levelId), Optional.of(levelId) , CUSTOMER_EMAIL);
		Assert.assertEquals(reqTickets, seatHold.getNumOfSeats());
		Assert.assertEquals(reqTickets, seatHold.getSeatMap().get(levelId).intValue());
		Assert.assertEquals(1, seatHold.getSeatMap().size());
		Assert.assertEquals(reqTickets, seatHold.getSeatMap().get(levelId).intValue());
			
		// validate total number of seats should be reduced to by reqTickets and similarly in the requested level
		Assert.assertEquals(config.getMaxSeats()-reqTickets, ticketservice.numSeatsAvailable(Optional.empty()));
		Assert.assertEquals(config.getMaxNumSeats(levelId)-reqTickets, ticketservice.numSeatsAvailable(Optional.of(levelId)));
		
		// Hold max seats in the same level, so current level seats should all be held and 5 from next level
		
		int maxSeatsInLevelId = config.getMaxNumSeats(levelId);
		SeatHold seatHold2 = ticketservice.findAndHoldSeats(maxSeatsInLevelId, 
				Optional.of(levelId), Optional.empty() , CUSTOMER_EMAIL);
		
		Assert.assertEquals(maxSeatsInLevelId, seatHold2.getNumOfSeats());
		Assert.assertEquals(2, seatHold2.getSeatMap().size());
		
		Assert.assertEquals(maxSeatsInLevelId-reqTickets, seatHold2.getSeatMap().get(levelId).intValue());
		Assert.assertEquals(reqTickets, seatHold2.getSeatMap().get(levelId+1).intValue());
		
		// try to hold a seat in the level which is full, should throw tickets unavailable
		thrown.expect(TicketServiceException.class);
	    thrown.expectMessage("Tickets Unavailable");
		ticketservice.findAndHoldSeats(1, 
				Optional.of(levelId), Optional.of(levelId) , CUSTOMER_EMAIL);
	}
	
	/**
     * Test an invalid request to method {@link TicketService}.reserveSeats
     * @throws TicketServiceException
     */
	@Test
	public void testReserveSeats_InvalidRequest1() throws TicketServiceException{
		thrown.expect(TicketServiceException.class);
	    thrown.expectMessage("Invalid Seat Hold request");
		ticketservice.reserveSeats(123, CUSTOMER_EMAIL);
	}

	/**
     * Test an invalid request to method {@link TicketService}.reserveSeats
     * @throws TicketServiceException
     */
	@Test
	public void testReserveSeats_InvalidRequest2() throws TicketServiceException{
		// Hold 5 seats in a level
		int reqTickets = 5;
		int levelId = config.getMinLevelId();
		SeatHold seatHold = ticketservice.findAndHoldSeats(reqTickets, 
				Optional.of(levelId), Optional.of(levelId) , CUSTOMER_EMAIL);
		thrown.expect(TicketServiceException.class);
	    thrown.expectMessage("Invalid Seat Hold request");
		ticketservice.reserveSeats(seatHold.getSeatHoldId(), "abc@abc.com");
	}
	
	/**
     * Test a valid request to method {@link TicketService}.reserveSeats
     * @throws TicketServiceException
     */
	@Test
	public void testReserveSeats_ValidRequest() throws TicketServiceException{
		// Hold 5 seats in a level
		int reqTickets = 5;
		int levelId = config.getMinLevelId();
		SeatHold seatHold = ticketservice.findAndHoldSeats(reqTickets, 
				Optional.of(levelId), Optional.of(levelId) , CUSTOMER_EMAIL);
		
		ticketservice.reserveSeats(seatHold.getSeatHoldId(), CUSTOMER_EMAIL);
		
		// validate non reserved seats and map
		Assert.assertEquals(reqTickets, config.getMaxNumSeats(levelId) - config.getNonReservedSeatMap().get(levelId).intValue());
	}

}
