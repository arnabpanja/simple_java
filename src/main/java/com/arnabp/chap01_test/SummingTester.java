package com.arnabp.chap01_test;

import com.arnabp.chap01.MultiThreadSummer;

public class SummingTester {

    public static void main(String[] args) {
        
        var sumMachine = new MultiThreadSummer();

        // sum = 15
        Double[] smallOddSizedArray= {1.0, 2.0, 3.0, 4.0, 5.0};

        Double[] theBigOne = new Double[40];

        // fills in 40 element large array.
        for (int i = 0; i < theBigOne.length; i++) {
            // cast int to double.
            theBigOne[i] = (double)(i + 1);
        }

        System.out.println("TEST 1: ");

        Double result1 = sumMachine.sumArray(smallOddSizedArray);


        System.out.println("SUM 1 = " + result1);

        //820.0
        System.out.println("TEST 2: ");
        Double result2 = sumMachine.sumArray(theBigOne);
        System.out.println("SUM 2 = " + result2);
        
        

    }
    
}