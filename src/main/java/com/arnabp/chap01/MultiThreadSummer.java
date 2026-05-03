package com.arnabp.chap01;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Write a multi-threaded program that sums numbers 1 to 10,000, 
// by splitting the numbers in half and assigning each half to a 
// separate thread to add and then join the results together. 
public class MultiThreadSummer {

    // incremented every time a sumThread is created.
    private int threadCount;

    // constructor
    public MultiThreadSummer(){
        threadCount = 0;
    }

    public int getTotalThreadCount(){
        return threadCount;
    }

    
    // encapsulation.
    // inner class used to create thread.
    private class sumThread implements Callable<Double> {

        

        // reference to array being read from.
        private final Double[] taskArray;
        private final int start; // start index of subarray.
        private final int end; // end index for subarray.

        private final ExecutorService servicer;

        private int idCount = 0;

        //constructor - gets arrray and range it needs to sum up.
        public sumThread(Double[] taskArray, int start, int end, ExecutorService servicer){

            // increment count of outer class instance.
            threadCount++;
            idCount = threadCount;

            this.taskArray = taskArray;
            this.start = start; // left most index of subarray
            this.end = end; // right most index

            this.servicer = servicer; // reference to servicer.

        }

        public int getIdCount(){
            return idCount;
        }


        
        // recursively called. returns sum of sub arrays.
        // UNTESTED METHOD
        @Override
        public Double call() throws Exception {

            

            // print name of thread created.
            System.out.println( "Started Thread: \t" + this.getIdCount());

            
            // check base condition:
            // when length of sub array less than or equal to 2.
            // case 1) when length of sub array is 2. Return sum of both elements.
            // case 2) when length of sub array is 1. Return single value.

            // length of sub array:
            // (End Index - Start Index) + 1 = length of sub array.
            int subLength = Math.abs(end - start) + 1;
            if (subLength == 2){
                // returns sum of two elements
                System.out.println("Retreiving= Th-"+ Thread.currentThread().getName() + "\tstart:" + start + "\tend:" + end );
                Double sum = taskArray[start] + taskArray[end];
                System.out.println( "Th-"+ Thread.currentThread().getName() + "\tReturning Sum:\t" + sum);
                return (sum); 
            }
            else if(subLength < 2){ 
                // return single value.
                System.out.println( "Th-"+ Thread.currentThread().getName() + "\tReturning Val:\t" + taskArray[start]);
                return taskArray[start];
            }
            // base case not met.
            // thus create 2 new threads and wait for them to retreive 2 sums.
            // Take 2 sums, add them, then return that sum.

            // IMPORTANT! THIS CAUSED SERIOUS BUGS BEFORE!
            // OLD code: int mid = (subLength /2) 
            // did not produce mid point relative to the subarray. Gave left sub array wrong end index. 
            // Gave Right sub array wrong start index.

            // get midpoint of sub array. Shift value by starting index.
            int mid = (subLength /2) + start;

            // create 2 sub arrays, split array in half.
            // declared like this so we can print out ID numbers.
            sumThread leftThread = new sumThread(taskArray, start, mid - 1, this.servicer);
            sumThread rightThread = new sumThread(taskArray, mid, end, this.servicer);

            // redeclare as callable.
            Callable<Double> leftChild = leftThread;
            Callable<Double> rightChild = rightThread;

            // use ExecutorService to run new child threads...
            // Begin executing them here.
            Future<Double> lFuture = servicer.submit(leftChild);
            Future<Double> rFuture = servicer.submit(rightChild);

            // Busy waiting???:
            System.out.println("T" + this.getIdCount() + " - busy waiting for: L=" + leftThread.getIdCount() + " R=" + rightThread.getIdCount());

            // what to do if 2nd() get is finished first?
            // could be blocking eachother. 
            // java how to get thread to switch to another task that is blocking too long?
            // blocking operations.
            Double leftSum =  lFuture.get();
            Double rightSum = rFuture.get();

            // total sum from two child threads.
            Double totalRecursiveSum = leftSum + rightSum;

            // return, end thread.
            return totalRecursiveSum;
        }

    } 

    // sums array of sums:
    @SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch"})
    public Double sumArray(Double[] a){
        
        // executer service
        ExecutorService fatherService = Executors.newFixedThreadPool(10);

        //Future<Double> futureResult = service.submit(null);

        // limit number of layers of recursion. halving array. becuase too much is bad?
        int length = a.length;

        // create first thread.
        Callable<Double> starterThread = new sumThread(a,0,length - 1, fatherService);

        // submit task for execution, begin summing.
        Future<Double> future = fatherService.submit(starterThread);

        // code ran asynchronously.
        System.out.println("LOADING...");

        // RETREIVE THE OVERALL SUM.
        // Blocking operation - program blocks here until future retrieved
        Double overallSum = null; // stays null if exception occured.
        try{
            overallSum = future.get();
        }
        catch(InterruptedException e){
            e.printStackTrace(); 
        } catch(Exception e){
            e.printStackTrace();
        }

        if(overallSum == null){
            System.out.println("SUMMING ERROR");
            return null;
        }

        // sucess.
        // shutdown executor service to end method.
        // waits for all threads to finish.
        fatherService.shutdown();

        // sucess.
        return overallSum;
        
        
    }



}