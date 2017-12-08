package hw2;

/**
 *  @author YOUR NAME SHOULD GO HERE
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;



public class AuctionServer
{
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected. 
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer()
	{
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance()
	{
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */





	/* Statistic variables and server constants: Begin code you should likely leave alone. */


	/**
	 * Server statistic variables and access methods:
	 */
	private int soldItemsCount = 0;
	private int revenue = 0;

	public int soldItemsCount()
	{
		return this.soldItemsCount;
	}

	public int revenue()
	{
		return this.revenue;
	}



	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
	public static final int serverCapacity = 80; // The maximum number of active items at a given time.


	/* Statistic variables and server constants: End code you should likely leave alone. */



	/**
	 * Some variables we think will be of potential use as you implement the server...
	 */

	// List of items currently up for bidding (will eventually remove things that have expired).
	private List<Item> itemsUpForBidding = new ArrayList<Item>();


	// The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
	private int lastListingID = -1; 

	// List of item IDs and actual items.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

	// List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
	private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>(); 




	// List of sellers and how many items they have currently up for bidding.
	private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();



	// Object used for instance synchronization if you need to do it at some point 
	// since as a good practice we don't use synchronized (this) if we are doing internal
	// synchronization.
	//
	// private Object instanceLock = new Object(); 
	private HashMap<String, Integer> threeItemsUnder75 = new HashMap<>();
	private HashMap<String, Integer> fiveItemsUnbid = new HashMap<>();
	private HashSet<String> disqualifedSellers = new HashSet<>();
	
	
	



	/*
	 *  The code from this point forward can and should be changed to correctly and safely 
	 *  implement the methods as needed to create a working multi-threaded server for the 
	 *  system.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */


	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * @param sellerName Name of the <code>Seller</code>
	 * @param itemName Name of the <code>Item</code>
	 * @param lowestBiddingPrice Opening price
	 * @param biddingDurationMs Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
	 */
	public int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   Make sure there's room in the auction site.
		//   If the seller is a new one, add them to the list of sellers.
		//   If the seller has too many items up for bidding, don't let them add this one.
		//   Don't forget to increment the number of things the seller has currently listed.

//		pseudo code:
//		ADD LOCK
//			IF size of items listed >= serverCapacity
//				RETURN -1
//			END IF
//			IF the seller is disqualified
				// RETURN -1
			// END IF
//			IF the seller submit an Item with < 75 
//				IF sellerAgainstRule contains the seller
//					IF times of sellerAgainstRule >= 2
						// ADD the seller to the disqualifedSellers LIST
//						RETURN -1
//					ELSE 
//						INCREMENT times of sellerAgainstRule
//					END IF
//				ELSE
//					ADD the seller to the sellerAgainstRule list
//				END IF
//			END IF	
//			
//			IF the seller exist in the itemsPerSeller list
//				IF number of things the seller has currently listed >= maxSellerItems
//					RETURN -1
//				END IF
//			ELSE
//				Add seller to itemsPerSeller list
//			END IF
//			
//			INCREMENT lastListingID
//			INIT item with sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs
//			Add the item to the itemsUpForBidding List
//			Add the lastListingID and item to the itemsAndIDs List
//			Add the lastListingID and lowestBiddingPrice to the highestBids List
//			INCREMENT number of things the seller has currently listed
//			RETURN lastListingID
//		RELEASE LOCK
		
		synchronized(this.itemsUpForBidding){
			if(itemsUpForBidding.size() >= serverCapacity){
				System.out.println(sellerName + " failed, exceed serverCapacity: "+itemsUpForBidding.size());
				return -1;
			}
			if(disqualifedSellers.contains(sellerName)){
//				System.out.println(sellerName + " disqualified");
				return -1;
			}
			if(lowestBiddingPrice < 75) {
				if(threeItemsUnder75.containsKey(sellerName)) {
					int itemLowerThanRule = threeItemsUnder75.get(sellerName);
					if(itemLowerThanRule >= 2) {
						System.out.println(sellerName + " disqualified, 3 items under 75" );
						disqualifedSellers.add(sellerName);
						return - 1;
					}else {
						threeItemsUnder75.put(sellerName, itemLowerThanRule + 1);
					}
				}else {
					threeItemsUnder75.put(sellerName, 1);
				}
			}
			if(itemsPerSeller.containsKey(sellerName)){
				
				int itemsNumOfSeller = itemsPerSeller.get(sellerName);
				if(itemsNumOfSeller >= maxSellerItems){
					System.out.println(sellerName + " failed, exceeds maxSellerItems: "+itemsNumOfSeller);
					return -1;
				}

			}else{
				itemsPerSeller.put(sellerName,0);
			}
			
			int itemId = ++lastListingID;
			Item item = new Item(sellerName, itemName, itemId, lowestBiddingPrice, biddingDurationMs);
			itemsUpForBidding.add(item);
			itemsAndIDs.put(itemId, item);
			//highestBids.put(itemId, lowestBiddingPrice);
			int itemsNumOfSeller = itemsPerSeller.get(sellerName);
			itemsPerSeller.put(sellerName, itemsNumOfSeller + 1);
			System.out.println(sellerName + " "+item + " " +lowestBiddingPrice +" succeed");
			return itemId;
		}
		
	}



	/**
	 * Get all <code>Items</code> active in the auction
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */
	public List<Item> getItems()
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//    Don't forget that whatever you return is now outside of your control.
		//pseudo code:
		//initialize List items with deep copy of itemsUpForBidding
		//RETURN list
		List<Item> items = new ArrayList<>(itemsUpForBidding);
		return items;
	}


	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * @param bidderName Name of the <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @param biddingAmount Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */
	public boolean submitBid(String bidderName, int listingID, int biddingAmount)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   See if the item exists.
		//   See if it can be bid upon.
		//   See if this bidder has too many items in their bidding list.
		//   Get current bidding info.
		//   See if they already hold the highest bid.
		//   See if the new bid isn't better than the existing/opening bid floor.
		//   Decrement the former winning bidder's count
		//   Put your bid in place
		//
//		pseudo code:
//		ADD LOCK
//			IF itemsAndIDs doesn't contain key listingID
//				RETURN FALSE
//			END IF
//			IF the item is expired
//				RETURN false
//			END IF
//			IF itemsPerBuyer contains Key bidderName
//				IF number of item bidder has bid on >= bidderName
//					RETURN -1
//				END IF
//			ELSE
//				Add bidder to itemsPerBidder list
//			END IF
//			IF highestBidders contains key listingID
//				GET formerWinner of the listingID
//				IF formerWinner EQUALS TO bidderName
//					RETURN FALSE
//				END IF
//				DECREMENT number of things the formerWinner has bid
//			END IF		
//			INCREMENT number of things the bidder has bid
//			PUT the lastListingID and biddingAmount to the highestBids List
//			PUT the lastListingID and bidderName to the highestBidders List
//			RETURN lastListingID
//		RELEASE LOCK
		synchronized(this.itemsPerSeller){
			if(!itemsAndIDs.containsKey(listingID)) {
				System.out.println(listingID + " doesn't exist");
				return false;
			}
			if(itemsAndIDs.get(listingID).biddingOpen()){
				return false;
			}
			if(itemsPerBuyer.containsKey(bidderName)) {
				if(itemsPerBuyer.get(bidderName) >= maxBidCount) {//if this bidder has too many items in their bidding list.
					System.out.println("[failure]" +bidderName + " has too many items " + itemsPerBuyer.get(bidderName));
					return false;
				}
			}else {
				itemsPerBuyer.put(bidderName, 0);
			}
			if(highestBidders.containsKey(listingID)) {
				String formerWinner = highestBidders.get(listingID);
				if(formerWinner.equals(bidderName)) {
					System.out.println("[failure]" + bidderName + " holds the highest bid");
					return false;
				}
				itemsPerBuyer.put(formerWinner, itemsPerBuyer.get(formerWinner) - 1);
			}
//			int floorBid = itemPrice(listingID);
//			if(floorBid >= biddingAmount) {
//				System.out.println(biddingAmount + "is not enough");
//				return false;
//			}
			itemsPerBuyer.put(bidderName, itemsPerBuyer.get(bidderName) + 1);
			highestBids.put(listingID, biddingAmount);
			highestBidders.put(listingID, bidderName);
			System.out.println("[success]" + bidderName + " " +listingID +" "+ biddingAmount+" placed");
			return true;
		}
	}

	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * @param bidderName Name of <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
	 * 2 (open) if this <code>Item</code> is still up for auction<br>
	 * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
	 */
	public int checkBidStatus(String bidderName, int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   If the bidding is closed, clean up for that item.
		//     Remove item from the list of things up for bidding.
		//     Decrease the count of items being bid on by the winning bidder if there was any...
		//     Update the number of open bids for this seller
		
//		pseudo code:
//		IF itemsAndIDs doesn't contains key listingID
//			RETURN 3
//		END IF
//		IF the item is still open
//			RETURN 2
//		ELSE
//			IF
//				REMOVE the item from itemsUpForBidding
//			END IF
//			IF the item is bid
//				GET winnerName from highestBidders
//				REMOVE listingID from highestBidders
//				GET winnerCost from highestBids
//				REMOVE listingID from highestBids
//				INCREMENT soldItemsCount
//				INCREMENT revenue by winnerCost
//				IF winnerName EQUALS bidderName
//					RETURN 1
//				ELSE
//					RETURN 3
//				END IF
//			ELSE	
//				IF LIST fiveItemUnbid contains key item.seller()
//					GET itemUnbid from LIST fiveItemUnbid with key sellerName
//					IF itemUnbid >= 4
//						ADD sellerName to the LIST disqualifedSellers
//					ELSE
//						INCREMENT itemUnbid
//					END IF
//				ELSE
//					PUT the seller in the LIST fiveItemUnbid
//				END IF
//				RETURN 3
//			END IF
//		END IF
		synchronized(this.itemsUpForBidding) {
			if(!itemsAndIDs.containsKey(listingID)) {
				return 3;
			}
			Item item = itemsAndIDs.get(listingID);
			if(item.biddingOpen()) {
				return 2;
			}else {
				if(itemsUpForBidding.contains(item)) {
					itemsUpForBidding.remove(item);
				}
				if(!itemUnbid(listingID)) {
					String winnerName = highestBidders.remove(listingID);
					int winnerCost = highestBids.remove(listingID);
					soldItemsCount++;
					revenue += winnerCost;
					if(winnerName.equals(bidderName)) {
						return 1;
					}
				}else{
					if(fiveItemsUnbid.containsKey(item.seller())) {
						int itemUnbid = fiveItemsUnbid.get(item.seller());
						if(itemUnbid >= 4) {
							System.out.println(item.seller() +" disqualified, 3 items unbid");
							disqualifedSellers.add(item.seller());
						}else {
							fiveItemsUnbid.put(item.seller(), itemUnbid + 1);
						}
					}else {
						fiveItemsUnbid.put(item.seller(), 1);
					}
					return 3;
				}
				return 3;
			}
		}
		
		//return -1;
	}

	/**
	 * Check the current bid for an <code>Item</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been made,
	 * -1 if no <code>Item</code> exists
	 */
	public int itemPrice(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
//		pseudo code:
//		IF itemsAndIDs doesn't contains key listingID
//			RETURN -1
//		END IF
//		IF CALL itemUnbid with listingID
//			RETURN lowestBiddingPrice 
//		ELSE
//			RETURN highestBids
//		END IF
		if(!itemsAndIDs.containsKey(listingID)) {
			return -1;
		}
		if(itemUnbid(listingID)) {
			return itemsAndIDs.get(listingID).lowestBiddingPrice();
		}else {
			return highestBids.get(listingID);
		}
		
	}

	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist, false otherwise
	 */
	public Boolean itemUnbid(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
//		pseudo code:
//		IF itemsAndIDs doesn't contains key listingID
//			RETURN true
//		END IF
//		IF highestBids doesn't contains key listingID
//			RETURN true
//		ELSE 
//			RETURN false
//		END IF
		if(!itemsAndIDs.containsKey(listingID)) {
			return true;
		}
		return !highestBids.containsKey(listingID);
	}


}
 