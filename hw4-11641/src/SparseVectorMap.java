import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SparseVectorMap {

  Map<Integer, Map<Integer,Integer>> mat;
  /**
   * store sum of rating across key1 to calculate average later;
   */
  Map<Integer, Pair<Double,Integer>> sumData;
  //Map<String,Double> simCache; //in case I want to improve performance
  Map<Integer, Double> avgMap;
  
  public SparseVectorMap() {
    mat = new HashMap<Integer, Map<Integer,Integer>>();
    //simCache = new HashMap<String, Double>();
    sumData = new HashMap<Integer, Pair<Double,Integer>>();
    avgMap = new HashMap<Integer, Double>();
    // TODO Auto-generated constructor stub
  }

  public void add(int key1, int key2, int rating){
    if(mat.containsKey(key1))
    {
      Map<Integer,Integer> vec = mat.get(key1);
      vec.put(key2, rating);
      
      Pair<Double,Integer> p = sumData.get(key1);
      p.setLeft(p.getLeft()+rating);
      p.setRight(p.getRight()+1);
      
    }
    else
    {
      Map<Integer,Integer> vec = new HashMap<Integer, Integer>();
      vec.put(key2, rating);
      mat.put(key1, vec);
      
      Pair<Double,Integer> p = new Pair<Double, Integer>((double)rating, 1);
      sumData.put(key1, p);
      
    }
  }
  
  public KNN getKNN(int keyQ,int k){
    KNN result = new KNN(k);
    
    //ArrayList result = new ArrayList<Integer>();
    Map<Integer, Integer> v1 = mat.get(keyQ); 
    
    Iterator itr1 = mat.entrySet().iterator();
    while (itr1.hasNext()) {
        double sim;
        Map.Entry pairs = (Map.Entry)itr1.next();
        int key = (Integer)pairs.getKey();
        if(key==keyQ){
          continue;
        }
        Map<Integer, Integer> v2 = (Map<Integer, Integer>) pairs.getValue(); 
        sim = computeCosineSimilarity(v1, v2);
       // sim = computeDot(v1, v2);
        //int rating1 = (Integer)pairs.getValue();
        System.out.println(sim);
        result.Add(new Pair(key, sim));
        
    }
    return result;
    
  }
  
  public double computeDot(Map<Integer,Integer> v1, Map<Integer,Integer> v2){
    double result = 0;
    Iterator itr1 = v1.entrySet().iterator();
    while (itr1.hasNext()) {
        Map.Entry pairs = (Map.Entry)itr1.next();
        int key = (Integer)pairs.getKey();
        int rating1 = (Integer)pairs.getValue();
        
        if(v2.containsKey(key))
        {
          int rating2 = v2.get(key);
          result += rating1*rating2;
        }
    }
    return result;
  }

  public double computeCosineSimilarity(Map<Integer, Integer> v1,
          Map<Integer, Integer> v2) {
    // TODO :: compute cosine similarity between two sentences
    double cosine_similarity = computeDot(v1, v2);
    
    double v1Size = computeVectorSize(v1);
    double v2Size = computeVectorSize(v2);
    return cosine_similarity/(v1Size*v2Size);
  }
  
  public double computeVectorSize(Map<Integer, Integer> vec) {
    double v=0.0;

    // compute vector size
    Iterator it = vec.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry pairs = (Map.Entry)it.next();

        int rating = (Integer)pairs.getValue();
        v += Math.pow(rating,2);
    }
    
    return Math.sqrt(v);
  }
  
  public void computeAverageStat()
  {
    Iterator it = sumData.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry pairs = (Map.Entry)it.next();
        int key1 = (Integer) pairs.getKey();
        Pair<Double, Integer> p = (Pair<Double, Integer>) pairs.getValue();
        
        double sum = (Double)p.getLeft();
        int n = (Integer)p.getRight();
        
        double avg = sum/n;
        
        avgMap.put(key1, avg);
    }
    
    //we don't need this anymore
    sumData.clear();
  }
  
  public double predictRatingCos(int key1,int key2, int k){
    double result = 0;
    KNN nn = getKNN(key1, k);
    SortedArrayList<Pair<Integer,Double>> l = nn.getNnList();
    double sumW = 0;
    for(int i=0 ;i<l.size();i++)
    {
      Pair<Integer,Double> p = l.get(i);
      int k1 = (Integer)p.getLeft();
      double weight = (Double)p.getRight();
      weight = (weight+1)/2; // change to [0,1] space 
      sumW += weight;
      double ui;
      Map<Integer,Integer> uiVec = mat.get(k1);
      if(uiVec.containsKey(key2)){
        ui = uiVec.get(key2);
      }
      else
      {
        ui = avgMap.get(key1);
      }
      
      System.out.println("ui= "+ui + " weight= "+weight);
      result += ui*weight;
    }
    result = result/sumW; //Normalize it
    System.out.println("rating: "+result);
    return result;
  }
  
  public String toString(){
    return mat.toString();
    
  }
}
