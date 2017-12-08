package hw3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;

	//YOUR CODE GOES HERE...
	public final int  capacityIn;
	Queue<Food> cookAnItemList;
	
	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		
		//YOUR CODE GOES HERE...
		this.capacityIn = capacityIn;
		cookAnItemList = new LinkedList<Food>();
	}
	

	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public synchronized void makeFood(Cook cook, Food food, int ordernumber) throws InterruptedException {
		//YOUR CODE GOES HERE...
		cookAnItemList.add(food);
		CookAnItem cookAnItem = new CookAnItem(cook, food, ordernumber);

		Thread cookAnItemthread = new Thread(cookAnItem);
		cookAnItemthread.start();
		
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		private Cook cook;
		private Food foodToBeCooked;
		private int ordernumber;
		private Food cookedFood;
		
		public CookAnItem(Cook cook, Food food, int ordernumber) {
			this.cook = cook;
			this.foodToBeCooked = food;
			this.ordernumber = ordernumber;
		}
		
		public void run() {
			try {
				// YOUR CODE GOES HERE...
				Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, foodToBeCooked));				
				Thread.sleep(machineFoodType.cookTimeMS);	
				
				synchronized (Machine.this.cookAnItemList) {
					cookedFood = cookAnItemList.remove();
					cookAnItemList.notifyAll();
					Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, cookedFood));
				}
				
				synchronized (cook.completedFoodList) {
					cook.completedFoodList.add(cookedFood);
					cook.completedFoodList.notifyAll();
					Simulation.logEvent(SimulationEvent.cookFinishedFood(cook, cookedFood, ordernumber));
				}
				
			} catch (InterruptedException e) {
			}
		}
	}
 

	public String toString() {
		return machineName;
	}
}