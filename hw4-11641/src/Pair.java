public class Pair<L,R> implements Comparable<Object>{

  private L left;


  private R right;

  public void setLeft(L left) {
    this.left = left;
  }

  public void setRight(R right) {
    this.right = right;
  }
  
  public Pair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public L getLeft() { return left; }
  public R getRight() { return right; }

  @Override
  public int hashCode() { return left.hashCode() ^ right.hashCode(); }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof Pair)) return false;
    Pair pairo = (Pair) o;
    return this.left.equals(pairo.getLeft()) &&
           this.right.equals(pairo.getRight());
  }

  @Override
  public int compareTo(Object o) {
    // TODO Auto-generated method stub

    Pair pairo = (Pair) o;
    float r1 = (Float)this.right;
    float r2 = (Float)pairo.right;
    
    int id1 = (Integer)this.left;
    int id2 = (Integer)pairo.left;
    
    if(r1-r2>0)
      return -1;
    else if(r1-r2<0)
      return 1;
    else //tie score
      if(id1<id2)
        return -1;
      else
        return 1;
  }

  public String toString(){
    return "(" + this.left + "," + this.right+")";
  }
}