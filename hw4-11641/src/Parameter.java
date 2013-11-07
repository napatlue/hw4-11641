
public class Parameter {
  public enum SimilarityOption{
    DOT,COSINE, PCC
  }
  
  public enum CombineOption{
    MEAN,WEIGHT
  };
  
  SimilarityOption sim;
  CombineOption com;
  int minSimItem;
  public Parameter(String sim,String com,int min) {
    // TODO Auto-generated constructor stub
    sim = sim.toLowerCase();
    com = com.toLowerCase();
    if(sim.equals("dot"))
    {
      this.sim = SimilarityOption.DOT;
    }
    else if(sim.equals("cosine"))
    {
      this.sim = SimilarityOption.COSINE;
    }
    else if(sim.equals("pcc"))
    {
      this.sim = SimilarityOption.PCC;
    }
    else
    {
      System.out.println("Wrong Similarity option");
      System.exit(1);
    }
    
    if(com.equals("mean"))
    {
      this.com = CombineOption.MEAN;
    }
    else if(com.equals("weight"))
    {
      this.com = CombineOption.WEIGHT;
    }
    else
    {
      System.out.println("Wrong combine option");
      System.exit(1);
    }
    this.minSimItem = min;
  }

}
