package hw3;

import java.util.LinkedList;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	private Customer customer;
	public LinkedList<Food> completedFoodList;
	
	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while(true) {
				//YOUR CODE GOES HERE...
//				synchronized (Simulation.currentOrders) {
//					while (Simulation.currentOrders.size() == 0) {
//						Simulation.currentOrders.wait();
//					}
//					customer = Simulation.currentOrders.poll();
//				
//					completedFoodList = new LinkedList<Food>();
//					Simulation.currentOrders.notifyAll();
//					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, customer.getOrder(),
//							customer.getOrderNum()));
//				}
				
				
				
//				HERE IS WHERE I USED THE NONBLOCKING FUNCTION THAT I IMPLEMENTED
//				do {	
					customer = Simulation.currentOrders.poll();
//					Thread.sleep(5);
//					System.out.println("Something goes wrong hereeeeeeeeeeeeeeeeeeeeeeeeeeee");
//				}while(customer == null);
				completedFoodList = new LinkedList<Food>();
				Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, customer.getOrder(),
						customer.getOrderNum()));
				
				for (Food food : customer.getOrder()) {
					if (food.equals(FoodType.burger)) {
						synchronized (Simulation.Grill.cookAnItemList) {
							while (Simulation.Grill.cookAnItemList.size() == Simulation.events.get(0).simParams[3]) {
								Simulation.Grill.cookAnItemList.wait();
							}
							Simulation.Grill.makeFood(this, food, customer.getOrderNum());
							Simulation.Grill.cookAnItemList.notifyAll();
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, food, customer.getOrderNum()));
						}
					}

					else if (food.equals(FoodType.fries)) {
						synchronized (Simulation.Fryer.cookAnItemList) {
							while (Simulation.Fryer.cookAnItemList.size() == Simulation.events.get(0).simParams[3]) {
								Simulation.Fryer.cookAnItemList.wait();
							}
							Simulation.Fryer.makeFood(this, food, customer.getOrderNum());
							Simulation.Fryer.cookAnItemList.notifyAll();
							Simulation
									.logEvent(SimulationEvent.cookStartedFood(this, food, customer.getOrderNum()));
						}
					}

					else if (food.equals(FoodType.coffee)) {
						synchronized (Simulation.CoffeeMaker2000.cookAnItemList) {
							while (Simulation.CoffeeMaker2000.cookAnItemList
									.size() == Simulation.events.get(0).simParams[3]) {
								Simulation.CoffeeMaker2000.cookAnItemList.wait();
							}
							Simulation.CoffeeMaker2000.makeFood(this, food, customer.getOrderNum());
							Simulation.CoffeeMaker2000.cookAnItemList.notifyAll();
							Simulation
									.logEvent(SimulationEvent.cookStartedFood(this, food, customer.getOrderNum()));
						}
					}
				}

				synchronized (completedFoodList) {
					while (completedFoodList.size() != customer.getOrder().size()) {
						completedFoodList.wait();
					}
					completedFoodList.notifyAll();
				}

				synchronized (Simulation.completedOrders) {
					Simulation.completedOrders.add(customer);
					Simulation.completedOrders.notifyAll();
					Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, customer.getOrderNum()));
				}
			}
		}catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}