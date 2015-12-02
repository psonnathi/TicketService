package org.service.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objects.SeatHold;

/**
 * Singleton class to load the number of seats per level and the timeout for a
 * reservation. 
 * This is a core class containing a snapshot of the whole system in the persistent storage system.
 * This class is always maintained in memory, and only saves and updates or done to storage system 
 * and avoids all read requests so that application is very fast.
 *  
 * @author praveen
 *
 */
public class TicketsConfig {

	/**
	 * Start Venue Configuration
	 */
	/*
	 * Map containing venue levels and the number of seats in them. 
	 */
	private Map<Integer, Integer> maxSeatMap;
	/*
	 * Pricing per seat for a given venue level
	 */
	private Map<Integer, BigDecimal> pricingMap;
	/*
	 * Number of seconds to keep alive a seat hold request
	 */
	private long seatHoldExpiryInSeconds;
	/*
	 * Total number of seats across all levels
	 */
	private int maxSeats;
	/*
	 * Value of lowest level id - level closest to the stage
	 */
	private int minLevelId;
	/*
	 * Value of highest level id - level farthest to the stage
	 */
	private int maxLevelId;
	/**
	 * Start Venue Configuration
	 */
	
	/**
	 * Start live data
	 */
	/*
	 * Map containing the seats that are not reserved per level.
	 * It doesn't take seat hold data into consideration.
	 */
	private Map<Integer, Integer> nonReservedSeatMap;
	/*
	 * Map containing seat hold requests per level for the venue
	 */
	private Map<Integer, Integer> seatHoldMap;
	/*
	 * Map containing active seat hold request information.
	 * This map has to be purged based on the above time after 
	 * spawning thread/task if a reservation request doesn't come in
	 */
	private Map<Long, SeatHold> customerSeatHoldMap;
	
	/**
	 * End live data
	 */

	public Map<Long, SeatHold> getCustomerSeatHoldMap() {
		return customerSeatHoldMap;
	}

	private static TicketsConfig instance = null;
	
	/**
	 * Marking constructor private as per Singleton design pattern 
	 * and not allow multiple instantiations.
	 */
	private TicketsConfig(){
		maxSeatMap = new HashMap<Integer, Integer>();
		pricingMap = new HashMap<Integer, BigDecimal>();
		nonReservedSeatMap = new HashMap<Integer, Integer>();
		seatHoldMap = new HashMap<Integer, Integer>();
		customerSeatHoldMap = new HashMap<Long, SeatHold>();
	}

	/**
	 * Singleton instance creation
	 * @return
	 */
	public static TicketsConfig getInstance() {
		if (instance == null) {
			// Thread Safe
			synchronized (TicketsConfig.class) {
				if (instance == null) {
					instance = new TicketsConfig();
					// look up other application/resource/database and load the config
					// Temporarily calling below method
					getInstance().loadConfig();
					loadMinMaxLevelIds();
					setMaxSeats();
					
					// load from database both non reserved and seat hold maps
					loadNonReservedSeatmap();
					
					
				}
			}

		}
		return instance;
	}

	private Map<Integer, Integer> getMaxSeatMap() {
		return maxSeatMap;
	}

	public Map<Integer, BigDecimal> getPricingMap() {
		return pricingMap;
	}
	
	public Map<Integer, Integer> getNonReservedSeatMap() {
		return nonReservedSeatMap;
	}
	
	public Map<Integer, Integer> getSeatHoldMap() {
		return seatHoldMap;
	}
	
	// called only once at the time of instantiation
	private static void setMaxSeats(){	
		for(Integer levelId : getInstance().getMaxSeatMap().keySet())
			getInstance().maxSeats += getInstance().getMaxNumSeats(levelId); 
	}
	
	public int getMaxSeats(){
		return maxSeats;
	}

	public int getMaxNumSeats(Integer levelId) {
		return getMaxSeatMap().get(levelId);
	}
	
	public int getNonReservedSeats(Integer levelId) {
		return getNonReservedSeatMap().get(levelId);
	}
	
	public int getHeldSeats(Integer levelId) {
		return getSeatHoldMap().get(levelId);
	}

	public BigDecimal perSeatPrice(Integer levelId) {
		return pricingMap.get(levelId);
	}

	public long getSeatHoldExpiryInSeconds() {
		return seatHoldExpiryInSeconds;
	}

	public Set<Integer> getLevelIdsSet() {
		return maxSeatMap.keySet();
	}
	
	public int getTotalNonReservedSeats(){
		int nonReservedSeats = 0;
		for(Integer levelId : nonReservedSeatMap.keySet())
			nonReservedSeats += nonReservedSeatMap.get(levelId);
		return nonReservedSeats;
	}
	
	public int getTotalHeldSeats(){
		int heldSeats = 0;
		for(Integer levelId : seatHoldMap.keySet())
			heldSeats += seatHoldMap.get(levelId);
		return heldSeats;
	}
	
	/**
	 * At the beginning of application, there are no seat reserved so copying max seats into non reserved
	 * and seat hold map to empty
	 * 
	 * In case when the application is brought down, these two maps need to be loaded from db instead of below assumptions
	 */
	private static void loadNonReservedSeatmap(){
		for(Integer levelId : getInstance().maxSeatMap.keySet()){
			getInstance().nonReservedSeatMap.put(levelId, getInstance().maxSeatMap.get(levelId));
			getInstance().seatHoldMap.put(levelId, 0);
		}
	}
	
	private static void loadMinMaxLevelIds(){
		Collection<Integer> unsorted = getInstance().maxSeatMap.keySet();
		List<Integer> list = new ArrayList<Integer>(unsorted);
		Collections.sort(list);	
		getInstance().minLevelId = list.get(0);
		getInstance().maxLevelId = list.get(list.size()-1); 
	}
	
	public int getMinLevelId(){
		return minLevelId;
	}
	
	public int getMaxLevelId(){
		return maxLevelId;
	}
	
	/**
	 * Temporary method to load configuration.
	 * 
	 */
	private void loadConfig() {
		Map<Integer, Integer> seatMap = new HashMap<Integer, Integer>();
		seatMap.put(1, 25 * 50);
		seatMap.put(2, 20 * 100);
		seatMap.put(3, 15 * 100);
		seatMap.put(4, 15 * 100);
		this.maxSeatMap = seatMap;
		Map<Integer, BigDecimal> pricingMap = new HashMap<Integer, BigDecimal>();
		pricingMap.put(1, BigDecimal.valueOf(100));
		pricingMap.put(2, BigDecimal.valueOf(75));
		pricingMap.put(3, BigDecimal.valueOf(50));
		pricingMap.put(4, BigDecimal.valueOf(40));
		this.pricingMap = pricingMap;
	}

}
