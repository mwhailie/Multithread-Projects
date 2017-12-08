package com.csye7215.puzzleSolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This file needs to hold your solver to be tested. 
 * You can alter the class to extend any class that extends PuzzleSolver.
 * It must have a constructor that takes in a Puzzle.
 * It must have a solve() method that returns the datatype List<Direction>
 *   which will either be a reference to a list of steps to take or will
 *   be null if the puzzle cannot be solved.
 */
public class StudentMultiPuzzleSolver extends SkippingPuzzleSolver
{
    public StudentMultiPuzzleSolver(Puzzle puzzle)
    {
        super(puzzle);
    }
    Object monitor = new Object();
    boolean solutionFound; // flag if the solution has been found
    List<Direction> solution;
    
//    ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
    ExecutorService executor = new ThreadPoolExecutor(10, Integer.MAX_VALUE, Long.MAX_VALUE, TimeUnit.MINUTES, new LinkedBlockingDeque<>());
    
    
    /** 
     * Multiple Dots runs multi-threaded 
     * 
     */
    private class Dot implements Runnable {
        public Dot parent;
        public Choice choice;  
        public Direction dir;
        
        public Dot(Choice choice, Dot parent, Direction indir) {
            // init
            this.choice = choice;
            this.parent = parent;
            this.dir = indir;
        }
        
        /**
         * @invariant : 1. one Dot can only stay at one position in the maze. 
         *                2. old position is always the last position the dot stayed
         *                3. Dots will not go to positions where other Dots have visited
         *                4. Never go to the old position
         * @pre : constructed when system started
         * @post : 1. Solution found, stop all other threads, update result 
         *          2. Reaches dead end then stop and remove it self from threads
         *          3. interrupted by other threads
         * @exception : ClassFound, InterruptedException 
         * 
         * @description : The Dots together works as BFS, but each Dot works as DFS.
         *                  The Dot search for its next possible choice, if there's only one choice, then go to that position and update it self;
         *                  if multiple choices exist, choose the first choice to go, and create Dot and thread for all the other choices.
         *                  if it's the final position, Solution found, stop all other threads, update result.
         *                  if no choices left, reaches dead end then stop and remove it self from threads.
         *                  Synchronize: When List of threads needs be update(add, remove), the list need to be synchronized.
         * 
         */
        @Override
        public void run() {
            Direction lastDir = null;
            try {
                while (true && !solutionFound) {
                    if (this.choice.isDeadend()) {
//                        executor.remove(this);
                        return ;
                    } else {
                        for (Direction dir : this.choice.choices) {
                            lastDir = dir;
                            Dot nextDot = new Dot(follow(this.choice.at, dir), this, dir);
                            executor.execute(nextDot);
                        }
//                        executor.remove(this);
                        return;
                    }
                }
            } catch (SolutionFound e) {
                if (solutionFound) return;
                solutionFound = true;
                LinkedList<Direction> soln = new LinkedList<Direction>();
                // First save the direction we were going in when the exit was
                // discovered.
                soln.addFirst(lastDir == null ? e.from.reverse() : lastDir);
                Dot curr = this;
                while (curr != null)
                {
                    try
                    {
                        Choice walkBack = followMark(curr.choice.at, curr.choice.from, 1);
                        if (puzzle.display != null)
                        {
                            puzzle.display.updateDisplay();
                        }
                        soln.addFirst(walkBack.from);
                        curr = curr.parent;
                    }
                    catch (SolutionFound e2)
                    {
                        // If there is a choice point at the puzzle entrance, then
                        // record
                        // which direction we should choose.
                        if (puzzle.getMoves(puzzle.getStart()).size() > 1) soln.addFirst(e2.from);
                        if (puzzle.display != null)
                        {
                            markPath(soln, 1);
                            puzzle.display.updateDisplay();
                        }
                        solution = pathToFullPath(soln);
                        executor.shutdownNow();
                        synchronized(monitor){
                            monitor.notify();
                        }
                        return;
                    }
                }
                if (solution == null) {
                    markPath(soln, 1);
                    solution = pathToFullPath(soln);
                }
                executor.shutdownNow();
                synchronized(monitor){
                        monitor.notify();
                }
            }
        }
        
    }
    
    
    /**
     * @invariant : Start at start position for the first Dot, and stops after all thread stop
     * @pre : null
     * @post : solution found or all thread ended but no solution found
     * 
     * @return return List
     */
    public List<Direction> solve() {
        /**
         * initialize the first Dot at the first Position
         */
        try {

            Choice firstChoice = firstChoice(puzzle.getStart());
            Dot firstDot = new Dot(firstChoice(puzzle.getStart()), null, null);
            this.executor.execute(firstDot);
            synchronized(monitor){
                while (!this.executor.isShutdown()) ;monitor.wait();
//                    System.out.println(executor.getActiveCount());
            }
        } catch (SolutionFound e) {
            System.out.println("catch");
        } 
        catch (InterruptedException ex) {
            Logger.getLogger(StudentMultiPuzzleSolver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.solution;
    }
}
