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
  //Map<Pair<Integer,Integer>, Float> simCache; //in case I want to improve performance
  Map<Integer,KNN> cacheKNN;
  Map<Integer, Float> avgMap;
  Map<Integer, Float> sdMap;
  Map<Integer, Float> sizeCache;
  
  ArrayList<Map<Integer, Integer>> spMat;
  ArrayList<Integer> indexToKey;
  Map<Integer, Integer> keyToIndex;
  
  public SparseVectorMap() {
    mat = new HashMap<Integer, Map<Integer,Integer>>();
    //simCache = new HashMap<Pair<Integer,Integer>, Float>();
    sumData = new HashMap<Integer, Pair<Float,Integer>>();
    avgMap = new HashMap<Integer, Float>();
    sdMap = new HashMap<Integer, Float>();
    sizeCache = new HashMap<Integer, Float>();
    cacheKNN = new HashMap<Integer, KNN>();
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
 
    
    //ArrayList result = new ArrayList<Integer>();
    //Map<Integer, Integer> v1 = mat.get(keyQ);
    if(cacheKNN.containsKey(keyQ))
    {
      return cacheKNN.get(keyQ);
    }
    
    KNN result = new KNN(k);
    int rowQ=keyToIndex.get(keyQ);
    Map<Integer, Integer> v1 = spMat.get(rowQ); 
    float v1Size = computeVectorSize(v1);
    for(int i = 0;i<spMat.size();i++){
        float sim = 0f;

        if(i==rowQ){
          continue;
        }
        Map<Integer, Integer> v2 = (Map<Integer, Integer>) spMat.get(i); 

          Pair<Integer,Integer> cacheKey;
         
          //Compute Similarity
          
          //1. Using dot product
          if(Recommender.param.sim == Parameter.SimilarityOption.DOT)
          {
            sim = computeDot(v1, v2);
          }
        //2. Using Cosin Sim
          else if(Recommender.param.sim == Parameter.SimilarityOption.COSINE)
          {
            sim = computeCosineSimilarity(v1, v2, indexToKey.get(rowQ), indexToKey.get(i));
          }
          else if(Recommender.param.sim == Parameter.SimilarityOption.PCC)
          {
            sim = computePCC(v1, v2, indexToKey.get(rowQ), indexToKey.get(i));
          } 
          
          
          
          
          
         /*
          if(i < rowQ)
          {
            cacheKey = new Pair<Integer, Integer>(i, rowQ);
          }
          else
          {
            cacheKey = new Pair<Integer, Integer>(rowQ, i);
          }
         
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
        //int rating1 = (Integer)pairs.getValue();
       // System.out.println(sim);
        
       result.Add(new Pair(indexToKey.get(i), sim));
        
    }
    
  
    cacheKNN.put(keyQ, result);
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
    if(cosine_similarity == 0) return 0;
      
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
  
  public float predictRatingWeight(int key1,int key2, int k){
    float result = 0;
    if(!keyToIndex.containsKey(key1))
    {
      return result;
    }
    
    KNN nn = getKNN(key1, k);
  //  System.out.println(nn);
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
      //Map<Integer,Integer> uiVec = mat.get(k1);
      Map<Integer,Integer> uiVec = spMat.get(keyToIndex.get(k1));
      if(uiVec.containsKey(key2)){
        ui = uiVec.get(key2);
      }
      else
      {
        ui = avgMap.get(k1);
        //System.out.println("avg ui = " + ui);
      }
      
     // System.out.println("ui= "+ui + " weight= "+weight);
      result += ui*weight;
    }
    if(Math.abs(sumW - 0) < 0.0000000001)
    {
      return avgMap.get(key1)+3;
    }
    result = result/sumW; //Normalize it
    
    
    //System.out.println("rating: "+result);
    return (result+3);
  }
  
  public double predictRatingMean(int key1,int key2, int k){
    double result = 0;
    if(!keyToIndex.containsKey(key1))
    {
      return result;
    }
    
    KNN nn = getKNN(key1, k);
    //System.out.println(nn);
    SortedArrayList<Pair<Integer,Float>> l = nn.getNnList();

    for(int i=0 ;i<l.size();i++)
    {
      Pair<Integer,Float> p = l.get(i);
      int k1 = (Integer)p.getLeft();
      //float weight = (float)1/k;
      float ui;
      //Map<Integer,Integer> uiVec = mat.get(k1);
      Map<Integer,Integer> uiVec = spMat.get(keyToIndex.get(k1));
      if(uiVec.containsKey(key2)){
        ui = uiVec.get(key2);
      //  System.out.println(ui);
      }
      else
      {
        ui = avgMap.get(k1);
      //  System.out.println("avg ui = " + ui);
      }
      
      //System.out.println("ui= "+ui + " weight= "+weight);
      result += ui;
    }
    result = result/k; //Normalize it
    //System.out.println("rating: "+result);
    return (result+3);
  }
  
  public String toString(){
    return spMat.toString();
    
  }
  
  public int Size(){
    return spMat.size();
  }

  
  
  
  public void preProcessing() {
    // TODO Auto-generated method stub
    spMat = new ArrayList<Map<Integer,Integer>>(mat.values());
    indexToKey = new ArrayList<Integer>(mat.keySet());
    
    
    keyToIndex = new HashMap<Integer, Integer>((int) Math.ceil(indexToKey.size()/0.75));
    for(int i =0; i<indexToKey.size();i++){
      keyToIndex.put(indexToKey.get(i),i);
    }
    mat.clear();
    mat = null;
    System.gc();
    
  }

  public double predictRating(int key1, int key2, int k) {
    // TODO Auto-generated method stub
    double result = 0;
    if(Recommender.param.com == Parameter.CombineOption.MEAN)
    {
      result = predictRatingMean(key1, key2, k);
    }
    else if(Recommender.param.com == Parameter.CombineOption.WEIGHT)
    {
      result = predictRatingWeight(key1, key2, k);
    }
    return result;
  }
  
  
  public void computeSD()
  {
      for(int i = 0; i<spMat.size();i++)
      {
        int key1 = indexToKey.get(i); // get key for looking in avgMap
        float avg = avgMap.get(key1);
        
        double tmp = 0;
        int n = 0;
        
        Map<Integer, Integer> vec = spMat.get(i);
        Iterator it = vec.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            int key = (Integer)pairs.getKey();
            int rating = (Integer)pairs.getValue();
            tmp += (rating-avg)*(rating-avg);
            n++;
        }
        float sd = (float) Math.sqrt(tmp);
        sdMap.put(key1,sd);
        
      }

  }
  
  public float computePCC(Map<Integer,Integer> v1, Map<Integer,Integer> v2, int keyX, int keyY){
    float result = 0;
    int count = 0;
    Iterator itr1 = v1.entrySet().iterator();
    while (itr1.hasNext()) {
        Map.Entry pairs = (Map.Entry)itr1.next();
        int key = (Integer)pairs.getKey();
        int rating1 = (Integer)pairs.getValue();
        
        if(v2.containsKey(key))
        {
          int rating2 = v2.get(key);
          float xavg = avgMap.get(keyX);
          float yavg = avgMap.get(keyY);
          
          float xx = rating1-xavg;
          float yy = rating2-yavg;
          
          float xsig = sdMap.get(keyX);
          float ysig = sdMap.get(keyY);
          
          if(xsig!=0 && ysig!=0)
          {
            result += (xx/xsig)*(yy/ysig);
            
          }
          count++;
        }
    }
    if(count < Recommender.param.minSimItem)
    {
      return -1;
    }
    return result;
  }
}
