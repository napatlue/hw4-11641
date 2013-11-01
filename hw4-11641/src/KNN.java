
public class KNN {
  public SortedArrayList<Pair<Integer,Float>> nnList;
  public SortedArrayList<Pair<Integer, Float>> getNnList() {
    return nnList;
  }

  public int maxSize; //number of nearest neightbor
  public int currentSize;
  public KNN(int k) {
    // TODO Auto-generated constructor stub
    nnList = new SortedArrayList<Pair<Integer,Float>>();
    this.maxSize = k;
    this.currentSize = 0;
  }
  public void Add(Pair<Integer,Float> p){
    nnList.insertSorted(p);
    currentSize++;
    
    if(currentSize > maxSize){
      nnList.remove(maxSize);
      currentSize--;
    }
  }

  public String toString(){
    return nnList.toString();
    
  }
  
}
