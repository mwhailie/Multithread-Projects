package hw2;

public class Test {
	public void testcase1(){
		int nrSellers = 1;
		Thread[] sellerThreads = new Thread[nrSellers];
		Seller[] sellers = new Seller[nrSellers];
		for (int i = 0; i < nrSellers; ++i) {
			sellers[i] = new Seller(AuctionServer.getInstance(), "Seller" + i, 100, 50, 1);
			sellerThreads[i] = new Thread(sellers[i]);
			sellerThreads[i].start();
		}
	}
	public static void main(String[] args) {
		Test t = new Test();
		t.testcase1();
		
//		int nrSellers = 50;
//		int nrBidders = 20;
//
//		Thread[] sellerThreads = new Thread[nrSellers];
//		Thread[] bidderThreads = new Thread[nrBidders];
//		Seller[] sellers = new Seller[nrSellers];
//		Bidder[] bidders = new Bidder[nrBidders];
//
//		// Start the sellers
//		for (int i = 0; i < nrSellers; ++i) {
//			sellers[i] = new Seller(AuctionServer.getInstance(), "Seller" + i, 100, 50, i);
//			sellerThreads[i] = new Thread(sellers[i]);
//			sellerThreads[i].start();
//		}
//
//		// Start the buyers
//		for (int i = 0; i < nrBidders; ++i) {
//			bidders[i] = new Bidder(AuctionServer.getInstance(), "Buyer" + i, 1000, 20, 150, i);
//			bidderThreads[i] = new Thread(bidders[i]);
//			bidderThreads[i].start();
//		}
//
//		// Join on the sellers
//		for (int i = 0; i < nrSellers; ++i) {
//			try {
//				sellerThreads[i].join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//
//		// Join on the bidders
//		for (int i = 0; i < nrBidders; ++i) {
//			try {
//				sellerThreads[i].join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//
//		// TODO: Add code as needed to debug
//		System.out.println("soldItemsCount " +AuctionServer.getInstance().soldItemsCount());
//		System.out.println("revenue "+ AuctionServer.getInstance().revenue());
	}
}
