package hw3;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;    
	
	private static int runningCounter = 0;
	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
	}
	
	public String toString() {
		return name;
	}

	public List<Food> getOrder() {
		return order;
	}

	public int getOrderNum() {
		return orderNum;
	}

	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		//YOUR CODE GOES HERE...
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		// if table is available
		// get in coffee shop
		synchronized (Simulation.customers) {
			try {
				// the table is full
				while (Simulation.customers.size() >= Simulation.events.get(0).simParams[2]) {//numTable
					Simulation.customers.wait();
				}
				Simulation.customers.notifyAll();
				Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
				Simulation.customers.add(this);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// place an order
//		HERE IS WHERE I USED THE NONBLOCKING FUNCTION THAT I IMPLEMENTED
//		synchronized(Simulation.currentOrders){
//			Simulation.currentOrders.notifyAll();
//			Simulation.currentOrders.add(this);
//			Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, order, orderNum));			
//		}
		Simulation.currentOrders.add(this);
		Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, order, orderNum));	

		// receive an order
		synchronized(Simulation.completedOrders){
			while(!Simulation.completedOrders.contains(this)){
				try {
					Simulation.completedOrders.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, order, orderNum));
			Simulation.completedOrders.notifyAll();
		}
		

		// leave shop
		synchronized (Simulation.customers) {
			Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
			Simulation.customers.notifyAll();
			Simulation.customers.poll();
		}
		
	}
}