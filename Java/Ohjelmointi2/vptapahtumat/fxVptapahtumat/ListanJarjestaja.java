package fxVptapahtumat;

import java.util.Comparator;
import vptapahtumat.Tapahtuma;

public class ListanJarjestaja {
  public static class JarjestaNimi implements Comparator<Tapahtuma> {
    public int compare(Tapahtuma o1, Tapahtuma o2) {
      String p1 = o1.getNimi();
      String p2 = o2.getNimi();
      return p1.compareTo(p2);
    }
  }
  
  public static class JarjestaMaa implements Comparator<Tapahtuma> {
    public int compare(Tapahtuma o1, Tapahtuma o2) {
      String p1 = o1.getMaa();
      String p2 = o2.getMaa();
      return p1.compareTo(p2);
    }
  }
  
  public static class JarjestaPaivamaara implements Comparator<Tapahtuma> {
    public int compare(Tapahtuma o1, Tapahtuma o2) {
      String p1 = o1.getPvm();
      String p2 = o2.getPvm();
      return p1.compareTo(p2);
    }
  }
  
  public static class JarjestaPalkinto implements Comparator<Tapahtuma> {
    public int compare(Tapahtuma o1, Tapahtuma o2) {
      String p1 = o1.getPalkinto();
      String p2 = o2.getPalkinto();
      return p1.compareTo(p2);
    }
  }
  
  public static class JarjestaPeli implements Comparator<Tapahtuma> {
    public int compare(Tapahtuma o1, Tapahtuma o2) {
      int p1 = o1.getPeliID();
      int p2 = o2.getPeliID();
      return p1 - p2;
    }
  }
}
