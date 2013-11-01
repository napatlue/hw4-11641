import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.io.MatrixVectorReader;
import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;

public class Recommender {

	public static void main(String[] argv) throws FileNotFoundException, IOException {
		String trainFile = argv[0];
		String testFile = argv[1];
		String outputFile = argv[2];

		// If you see these outputs, it means you have successfully compiled and run the code.
		// Then you can remove these three lines if you want.
		System.out.println("Training File : " + trainFile);
		System.out.println("Test File : " + testFile);
		System.out.println("Output File : " + outputFile);

		// Implement your recommendation modules using trainFile and testFile.
		// And output the prediction scores to outputFile.
		 
		//Start timer to get running time
    long startTime = System.nanoTime();
     
		SparseVectorMap userMat = new SparseVectorMap();
		//SparseVectorMap movMat = new SparseVectorMap();
		String infile = trainFile;
		//file = "a.txt"; 
		BufferedReader br = new BufferedReader(new FileReader(infile));
	    try {
	
	        String line = br.readLine();
	        line = br.readLine(); // skip first line
	        while (line != null) {

	          String[] tokens = line.split(",");
	          int movieID = Integer.parseInt(tokens[0]);
	          int userID = Integer.parseInt(tokens[1]);
	          int rating = Integer.parseInt(tokens[2]);
	          
	          //doing imputation now!
	          rating -= 3;
	          
	          userMat.add(userID, movieID, rating);
	    //      movMat.add(movieID,userID,rating);
	          line = br.readLine();
	        }
	       // String everything = sb.toString();
	    } finally {
	        br.close();
	    }
	    
	    //Preprocessing compute average
	    userMat.computeAverageStat();
	    //movMat.computeAverageStat();
	    
	    System.out.println("Number of User :" + userMat.Size());
	    //System.out.println("Number of mov :" + movMat.Size());
	    
	    //Predict rating
	    
	    BufferedReader br2 = new BufferedReader(new FileReader(testFile));
	    BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
      try {
  
          String line = br2.readLine();
          line = br2.readLine(); // skip first line
          while (line != null) {

            String[] tokens = line.split(",");
            int movieID = Integer.parseInt(tokens[0]);
            int userID = Integer.parseInt(tokens[1]);

            //predict using user similarity
            double pred = userMat.predictRatingCos(userID, movieID, 10);
            System.out.println(pred);
           // bw.write(pred+"\n");
           // result += pred + "\n"; 
            line = br2.readLine();
          }
         // String everything = sb.toString();
      } finally {
          br2.close();
          bw.close();
      }
	    
	   // double pred = userMat.predictRatingCos(529849, 7, 10);
	    //System.out.println(pred);
      long endTime = System.nanoTime();
      long duration = endTime - startTime;
      
      System.out.println(duration);
	    
	    
	    /* test code */
	    //System.out.println(userMat.getKNN(6, 3));
	    //userMat.predictRatingCos(6, 1, 3);
	    //userMat.predictRatingCos(6, 3, 3);
	    //movMat.getKNN(12, 5);
	   //System.out.println(movMat);
	   //System.out.println(userMat);
	   System.out.println("finish");
	}

}