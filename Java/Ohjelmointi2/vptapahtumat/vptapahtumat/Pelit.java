package vptapahtumat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Pelit implements Iterable<Peli> {
  private boolean muutettu = false;
  
  private String tiedosto = "sisalto/pelit.dat";
  
  private final Collection<Peli> alkiot = new ArrayList<>();
  
  public void lisaa(Peli p) {
    this.alkiot.add(p);
    this.muutettu = true;
  }
  
  public int getLkm() {
    return this.alkiot.size();
  }
  
  public boolean getMuutettu() {
    return this.muutettu;
  }
  
  public void lueTiedostosta() throws SailoException {
    try {
      Exception exception2, exception1 = null;
    } catch (FileNotFoundException e) {
      throw new SailoException("Tiedosto " + this.tiedosto + " ei aukea");
    } catch (IOException e) {
      throw new SailoException("Ongelmia tiedoston kanssa: " + e.getMessage());
    } 
  }
  
  public void tallenna() throws SailoException {
    if (!this.muutettu)
      return; 
    File ftied = new File(this.tiedosto);
    try {
      Exception exception2, exception1 = null;
    } catch (FileNotFoundException ex) {
      throw new SailoException("Tiedosto " + ftied.getName() + " ei aukea");
    } catch (IOException ex) {
      throw new SailoException("Tiedoston " + ftied.getName() + " kirjoittamisessa ongelmia");
    } 
    this.muutettu = false;
  }
  
  public Iterator<Peli> iterator() {
    return this.alkiot.iterator();
  }
  
  public List<Peli> annaPelit(int tunnusnro) {
    List<Peli> loydetyt = new ArrayList<>();
    for (Peli p : this.alkiot) {
      if (p.getPeliID() == tunnusnro)
        loydetyt.add(p); 
    } 
    return loydetyt;
  }
  
  public List<Peli> annaPelit() {
    List<Peli> loydetyt = new ArrayList<>();
    for (Peli p : this.alkiot)
      loydetyt.add(p); 
    return loydetyt;
  }
  
  public static void main(String[] args) {
    Pelit pelit = new Pelit();
    Peli p1 = new Peli();
    Peli p2 = new Peli();
    Peli p3 = new Peli();
    p1.asetaNimi("Heroes 3");
    p1.rekisteroi();
    p2.asetaNimi("Civ 4");
    p2.rekisteroi();
    p3.asetaNimi("Earthworm Jim");
    p3.rekisteroi();
    pelit.lisaa(p1);
    pelit.lisaa(p2);
    pelit.lisaa(p3);
    System.out.println("---------Pelit-testi!----------");
    List<Peli> kaikkiPelit = pelit.annaPelit();
    List<Peli> yksiPeli = pelit.annaPelit(1);
    for (Peli p : kaikkiPelit) {
      System.out.println(p.getPeliID());
      p.tulosta(System.out);
    } 
    System.out.println("-------------------");
    for (Peli p : yksiPeli) {
      System.out.println(p.getPeliID());
      p.tulosta(System.out);
    } 
  }
}
