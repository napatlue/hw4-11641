import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class Recommender {

  public static Parameter param;
  
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
    
    //Set Parameter
    param = new Parameter("cosine", "weight");
     
		SparseVectorMap userMat = new SparseVectorMap();
		SparseVectorMap movMat = new SparseVectorMap();
		String infile = trainFile;
		//infile = "a.txt"; 
		int numRate1 = 0;
		int numRate3 = 0;
		int numRate5 = 0;
		int count = 0;
		double sumRate = 0;
		
		BufferedReader br = new BufferedReader(new FileReader(infile));
	    try {
	
	        String line = br.readLine();
	        line = br.readLine(); // skip first line
	        while (line != null) {

	          String[] tokens = line.split(",");
	          int movieID = Integer.parseInt(tokens[0]);
	          int userID = Integer.parseInt(tokens[1]);
	          int rating = Integer.parseInt(tokens[2]);
	          
	          //Statistic Exploration
	          if(movieID == 4321)
	          {
  	          if(rating == 1)
  	            numRate1++;
  	          else if (rating == 3)
  	            numRate3++;
  	          else if(rating == 5)
  	            numRate5++;
  	          
  	          sumRate+=rating;
  	          count++;
	          }
	          
	          
	          //doing imputation now!
	          rating -= 3;
	          
	          //userMat.add(userID, movieID, rating);
	          movMat.add(movieID,userID,rating);
	          line = br.readLine();
	        }
	       // String everything = sb.toString();
	    } finally {
	        br.close();
	    }
	    
	    //Preprocessing compute average
	    //userMat.computeAverageStat();
	    movMat.computeAverageStat();
	    //preprocessing again
	    //userMat.preProcessing();
	    movMat.preProcessing();
	    
	    //System.out.println("Number of User :" + userMat.Size());
	    //System.out.println("Number of mov :" + movMat.Size());
	    
	    
	    //Output stat
	    System.out.println("Number of movies rated" + count);
	    System.out.println("Number of rating 1: "+numRate1);
	    System.out.println("Number of rating 3: "+numRate3);
	    System.out.println("Number of rating 5: "+numRate5);
	    
	    sumRate = sumRate/count;
	    System.out.println("Average rating: "+sumRate);
	    
	   // double pp = userMat.predictRatingMean(1234576, 1, 5);
	    
	    //Predict rating
	    //testFile = "test-small.csv";
	    
      //double pp = userMat.predictRatingMean(6, 1, 4);
      //System.out.println(pp);
	    //testFile = "test.txt";
	   // movMat.predictRating(4321, 1, 5);
	    
	    BufferedReader br2 = new BufferedReader(new FileReader(testFile));
	    BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
	    int i =1;
      try {
  
          String line = br2.readLine();
          line = br2.readLine(); // skip first line
          while (line != null) {

            String[] tokens = line.split(",");
            int movieID = Integer.parseInt(tokens[0]);
            int userID = Integer.parseInt(tokens[1]);

            
            //double pred = userMat.predictRating(userID, movieID, 10);
            double pred = movMat.predictRating(movieID, userID, 10);
            
            
            System.out.println(pred + "\t" + ++i);
            bw.write(pred+"\n");
           // result += pred + "\n"; 
            line = br2.readLine();
          }
         // String everything = sb.toString();
      } finally {
          br2.close();
          bw.close();
      }
	    
	   //double pred = userMat.predictRatingCos(529849, 7, 10);
      //double pred = userMat.predictRatingMean(6, 1, 3);
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