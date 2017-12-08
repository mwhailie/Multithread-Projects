package hw3;

import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
//import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Simulation is the main class used to run the simulation.  You may
 * add any fields (static or instance) or any methods you wish.
 */
public class Simulation {
	// List to track simulation events during simulation
	public static List<SimulationEvent> events;  

	// current consumers with table
	public static Queue<Customer> customers = new LinkedList<Customer>();

	// current orders by customers
//	public static Queue<Customer> currentOrders = new LinkedList<Customer>();
	public static NonblockingLinkedList<Customer> currentOrders = new NonblockingLinkedList<Customer>();
	public static ArrayList<Customer> completedOrders = new ArrayList<Customer>();

	public static Machine Grill;
	public static Machine Fryer;
	public static Machine CoffeeMaker2000;

	/**
	 * Used by other classes in the simulation to log events
	 * @param event
	 */
	public static void logEvent(SimulationEvent event) {
		events.add(event);
		System.out.println(event);
	}

	/**
	 * 	Function responsible for performing the simulation. Returns a List of 
	 *  SimulationEvent objects, constructed any way you see fit. This List will
	 *  be validated by a call to Validate.validateSimulation. This method is
	 *  called from Simulation.main(). We should be able to test your code by 
	 *  only calling runSimulation.
	 *  
	 *  Parameters:
	 *	@param numCustomers the number of customers wanting to enter the coffee shop
	 *	@param numCooks the number of cooks in the simulation
	 *	@param numTables the number of tables in the coffe shop (i.e. coffee shop capacity)
	 *	@param machineCapacity the capacity of all machines in the coffee shop
	 *  @param randomOrders a flag say whether or not to give each customer a random order
	 *
	 */
	public static List<SimulationEvent> runSimulation(
			int numCustomers, int numCooks,
			int numTables, 
			int machineCapacity,
			boolean randomOrders
			) {

		//This method's signature MUST NOT CHANGE.  


		//We are providing this events list object for you.  
		//  It is the ONLY PLACE where a concurrent collection object is 
		//  allowed to be used.
		events = Collections.synchronizedList(new ArrayList<SimulationEvent>());

		// Start the simulation
		logEvent(SimulationEvent.startSimulation(numCustomers,
				numCooks,
				numTables,
				machineCapacity));



		// Set things up you might need

		// Start up machines
		Grill = new Machine("Grill", FoodType.burger, machineCapacity);
		Fryer = new Machine("Fryer", FoodType.fries, machineCapacity);
		CoffeeMaker2000 = new Machine("CoffeeMaker2000", FoodType.coffee, machineCapacity);
		logEvent(SimulationEvent.machineStarting(Grill, FoodType.burger, machineCapacity));
		logEvent(SimulationEvent.machineStarting(Fryer, FoodType.fries, machineCapacity));
		logEvent(SimulationEvent.machineStarting(CoffeeMaker2000, FoodType.coffee, machineCapacity));

		// Let cooks in
		Thread[] cooks = new Thread[numCooks];
		for(int i = 0; i < cooks.length; i++) {
			cooks[i] = new Thread(new Cook("Cook " + (i+1)));
		}
		// Build the customers.
		Thread[] customers = new Thread[numCustomers];
		for (int i = 0; i < numCooks; i++) {
			Cook cook = new Cook("Cook" + (i + 1));
			cooks[i] = new Thread(cook);
		}

		for (int i = 0; i < numCooks; i++) {
			cooks[i].start();
		}
		
		LinkedList<Food> order;
		if (!randomOrders) {
			order = new LinkedList<Food>();
			order.add(FoodType.burger);
			order.add(FoodType.fries);
			order.add(FoodType.fries);
			order.add(FoodType.coffee);
			for(int i = 0; i < customers.length; i++) {
				customers[i] = new Thread(
						new Customer("Customer " + (i+1), order)
						);
			}
		}
		else {
			for(int i = 0; i < customers.length; i++) {
				Random rnd = new Random(27);
				int burgerCount = rnd.nextInt(3);
				int friesCount = rnd.nextInt(3);
				int coffeeCount = rnd.nextInt(3);
				order = new LinkedList<Food>();
				for (int b = 0; b < burgerCount; b++) {
					order.add(FoodType.burger);
				}
				for (int f = 0; f < friesCount; f++) {
					order.add(FoodType.fries);
				}
				for (int c = 0; c < coffeeCount; c++) {
					order.add(FoodType.coffee);
				}
				customers[i] = new Thread(
						new Customer("Customer " + (i+1), order)
						);
			}
		}


		// Now "let the customers know the shop is open" by
		//    starting them running in their own thread.
		for(int i = 0; i < customers.length; i++) {
			customers[i].start();
			//NOTE: Starting the customer does NOT mean they get to go
			//      right into the shop.  There has to be a table for
			//      them.  The Customer class' run method has many jobs
			//      to do - one of these is waiting for an available
			//      table...
		}


		try {
			// Wait for customers to finish
			//   -- you need to add some code here...
			for (int i = 0; i < customers.length; i++) {
				customers[i].join();
			}

			// Then send cooks home...
			// The easiest way to do this might be the following, where
			// we interrupt their threads.  There are other approaches
			// though, so you can change this if you want to.
			for(int i = 0; i < cooks.length; i++) {
//				System.out.println("Cooooooooooooooooooooooooooook");
				cooks[i].interrupt();
			}
				
			for(int i = 0; i < cooks.length; i++)
				cooks[i].join();

		}
		catch(InterruptedException e) {
			System.out.println("Simulation thread interrupted.");
		}

		// Shut down machines
		logEvent(SimulationEvent.machineEnding(Grill));
		logEvent(SimulationEvent.machineEnding(Fryer));
		logEvent(SimulationEvent.machineEnding(CoffeeMaker2000));


		// Done with simulation		
		logEvent(SimulationEvent.endSimulation());

		return events;
	}

	/**
	 * Entry point for the simulation.
	 *
	 * @param args the command-line arguments for the simulation.  There
	 * should be exactly four arguments: the first is the number of customers,
	 * the second is the number of cooks, the third is the number of tables
	 * in the coffee shop, and the fourth is the number of items each cooking
	 * machine can make at the same time.  
	 */
	public static void main(String args[]) throws InterruptedException {
		// Parameters to the simulation
		/*
		if (args.length != 4) {
			System.err.println("usage: java Simulation <#customers> <#cooks> <#tables> <capacity> <randomorders");
			System.exit(1);
		}
		int numCustomers = new Integer(args[0]).intValue();
		int numCooks = new Integer(args[1]).intValue();
		int numTables = new Integer(args[2]).intValue();
		int machineCapacity = new Integer(args[3]).intValue();
		boolean randomOrders = new Boolean(args[4]);
		 */
		int numCustomers = 10;
		int numCooks =1;
		int numTables = 5;
		int machineCapacity = 4;
		boolean randomOrders = false;


		// Run the simulation and then 
		//   feed the result into the method to validate simulation.
		System.out.println("Did it work? " + 
				Validate.validateSimulation(
						runSimulation(
								numCustomers, numCooks, 
								numTables, machineCapacity,
								randomOrders
								)
						)
				);
	}
}


