import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SparseVectorMap {

  Map<Integer, Map<Integer,Integer>> mat;
  /**
   * store sum of rating across key1 to calculate average later;
   */
  Map<Integer, Pair<Float,Integer>> sumData;
  //Map<String,Float> simCache; //in case I want to improve performance
  Map<Integer, Float> avgMap;
  Map<Integer, Float> sizeCache;
  
  public SparseVectorMap() {
    mat = new HashMap<Integer, Map<Integer,Integer>>();
    //simCache = new HashMap<String, Float>(1000*1000);
    sumData = new HashMap<Integer, Pair<Float,Integer>>();
    avgMap = new HashMap<Integer, Float>();
    sizeCache = new HashMap<Integer, Float>();
    // TODO Auto-generated constructor stub
  }

  public void add(int key1, int key2, int rating){
    if(mat.containsKey(key1))
    {
      Map<Integer,Integer> vec = mat.get(key1);
      vec.put(key2, rating);
      
      Pair<Float,Integer> p = sumData.get(key1);
      p.setLeft(p.getLeft()+rating);
      p.setRight(p.getRight()+1);
      
    }
    else
    {
      Map<Integer,Integer> vec = new HashMap<Integer, Integer>();
      vec.put(key2, rating);
      mat.put(key1, vec);
      
      Pair<Float,Integer> p = new Pair<Float, Integer>((float)rating, 1);
      sumData.put(key1, p);
      
    }
  }
  
  public KNN getKNN(int keyQ,int k){
    KNN result = new KNN(k);
    
    //ArrayList result = new ArrayList<Integer>();
    Map<Integer, Integer> v1 = mat.get(keyQ); 
    float v1Size = computeVectorSize(v1);
    Iterator itr1 = mat.entrySet().iterator();
    while (itr1.hasNext()) {
        float sim;
        Map.Entry pairs = (Map.Entry)itr1.next();
        int key = (Integer)pairs.getKey();
        if(key==keyQ){
          continue;
        }
        Map<Integer, Integer> v2 = (Map<Integer, Integer>) pairs.getValue(); 
        String cacheKey = "";
        if(key < keyQ)
        {
          cacheKey = key+","+keyQ;
        }
        else
        {
          cacheKey = keyQ+","+key;
        }
       /* 
        if(simCache.containsKey(cacheKey))
        {  
          sim = simCache.get(cacheKey);
          
        }
        else
        {
          //sim = computeCosineSimilarity(v1, v2,keyQ,key);
          sim = computeDot(v1, v2);
          simCache.put(cacheKey,sim);
        }
        */
          sim = computeDot(v1, v2);
        //int rating1 = (Integer)pairs.getValue();
       // System.out.println(sim);
        result.Add(new Pair(key, sim));
        
    }
    return result;
    
  }
  


  public float computeDot(Map<Integer,Integer> v1, Map<Integer,Integer> v2){
    float result = 0;
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


  public float computeCosineSimilarity(Map<Integer, Integer> v1,
          Map<Integer, Integer> v2, int k1,int k2) {
    // TODO :: compute cosine similarity between two sentences
    float cosine_similarity = computeDot(v1, v2);
    
    float v1Size = computeVectorSize(v1);
    float v2Size = computeVectorSize(v2);
    return cosine_similarity/(sizeCache.get(k1)*sizeCache.get(k2));
  }
  
  public float computeVectorSize(Map<Integer, Integer> vec) {
    float v=0;

    // compute vector size
    Iterator it = vec.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry pairs = (Map.Entry)it.next();

        int rating = (Integer)pairs.getValue();
        v += Math.pow(rating,2);
    }
    
    return (float) Math.sqrt(v);
  }
  
  public void computeAverageStat()
  {
    Iterator it = sumData.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry pairs = (Map.Entry)it.next();
        int key1 = (Integer) pairs.getKey();
        
        //cache average rating
        Pair<Float, Integer> p = (Pair<Float, Integer>) pairs.getValue();
        
        float sum = (Float)p.getLeft();
        int n = (Integer)p.getRight();
        
        float avg = sum/n;
        
        avgMap.put(key1, avg);
    //    System.out.println("key :"+key1 + " avg :"+avg);
        
        //cache vector size
        Map<Integer,Integer> spVec = mat.get(key1);
        float vs = computeVectorSize(spVec);
        sizeCache.put(key1, vs);
    }
    
    //we don't need this anymore
    sumData.clear();
    sumData = null;
  }
  
  public float predictRatingCos(int key1,int key2, int k){
    float result = 0;
    if(!mat.containsKey(key1))
    {
      return result;
    }
    
    KNN nn = getKNN(key1, k);
    //System.out.println(nn);
    SortedArrayList<Pair<Integer,Float>> l = nn.getNnList();
    float sumW = 0;
    for(int i=0 ;i<l.size();i++)
    {
      Pair<Integer,Float> p = l.get(i);
      int k1 = (Integer)p.getLeft();
      float weight = (Float)p.getRight();
      weight = (weight+1)/2; // change to [0,1] space 
      sumW += weight;
      float ui;
      Map<Integer,Integer> uiVec = mat.get(k1);
      if(uiVec.containsKey(key2)){
        ui = uiVec.get(key2);
      }
      else
      {
        ui = avgMap.get(k1);
    //    System.out.println("avg ui = " + ui);
      }
      
    //  System.out.println("ui= "+ui + " weight= "+weight);
      result += ui*weight;
    }
    result = result/sumW; //Normalize it
   // System.out.println("rating: "+result);
    return (result+3);
  }
  
  public String toString(){
    return mat.toString();
    
  }
  
  public int Size(){
    return mat.size();
  }
}
