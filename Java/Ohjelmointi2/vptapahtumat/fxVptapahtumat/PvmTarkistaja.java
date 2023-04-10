package fxVptapahtumat;

public class PvmTarkistaja {
  private String tarkistettava;
  
  public PvmTarkistaja(String text) {
    this.tarkistettava = text;
  }
  
  public boolean tarkista() {
    String[] osat = this.tarkistettava.split("\\.");
    int dd = Integer.parseInt(osat[0]);
    int mm = Integer.parseInt(osat[1]);
    int vv = Integer.parseInt(osat[2]);
    if (dd < 0 || dd > 31)
      return false; 
    if (mm < 0 || mm > 12)
      return false; 
    if (vv < 1900 || mm > 2100)
      return false; 
    return true;
  }
}
