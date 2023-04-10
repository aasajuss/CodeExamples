package vptapahtumat;

public class SailoException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public SailoException(String viesti) {
    super(viesti);
  }
}
