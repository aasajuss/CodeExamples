package vptapahtumat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Tapahtumat implements Iterable<Tapahtuma> {
  private static final int MAX_TAPAHTUMAA = 10;
  
  private int lkm;
  
  private Tapahtuma[] alkiot;
  
  private String tiedosto;
  
  private boolean muutettu;
  
  public Tapahtumat() {
    this.lkm = 0;
    this.alkiot = new Tapahtuma[10];
    this.tiedosto = "sisalto/tapahtumat.dat";
  }
  
  public boolean getMuutettu() {
    return this.muutettu;
  }
  
  public void setMuutettu(boolean uusi) {
    this.muutettu = uusi;
  }
  
  public void lisaa(Tapahtuma tapahtuma) throws SailoException {
    if (this.lkm >= this.alkiot.length)
      throw new SailoException("Liikaa alkioita pösilö!"); 
    this.alkiot[this.lkm] = tapahtuma;
    this.lkm++;
    this.muutettu = true;
  }
  
  public Tapahtuma anna(int i) throws IndexOutOfBoundsException {
    if (i < 0 || this.lkm <= i)
      throw new IndexOutOfBoundsException("Laiton indeksi: " + i); 
    return this.alkiot[i];
  }
  
  public Tapahtuma annaTunnusNro(int nro) {
    for (int i = 0; i < this.lkm; i++) {
      if (this.alkiot[i].getTunnusNro() == nro)
        return this.alkiot[i]; 
    } 
    return null;
  }
  
  public void lueTiedostosta(String tied) throws SailoException {
    setTiedostonNimi(tied);
    try {
      Exception exception2, exception1 = null;
    } catch (FileNotFoundException e) {
      throw new SailoException("Tiedosto " + this.tiedosto + " ei aukea");
    } catch (IOException e) {
      throw new SailoException("Ongelmia tiedoston kanssa: " + e.getMessage());
    } 
  }
  
  public void lueTiedostosta() throws SailoException {
    lueTiedostosta(this.tiedosto);
  }
  
  public void poista(int id) {
    int ind = 0;
    try {
      ind = etsiId(id);
    } catch (SailoException e) {
      e.printStackTrace();
    } 
    if (ind < 0)
      return; 
    this.lkm--;
    for (int i = ind; i < this.lkm; i++)
      this.alkiot[i] = this.alkiot[i + 1]; 
    this.alkiot[this.lkm] = null;
    this.muutettu = true;
  }
  
  public int etsiId(int id) throws SailoException {
    for (int i = 0; i < this.lkm; i++) {
      if (id == this.alkiot[i].getTunnusNro())
        return i; 
    } 
    return -1;
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
  
  public void setTiedostonNimi(String nimi) {
    this.tiedosto = nimi;
  }
  
  public int getLkm() {
    return this.lkm;
  }
  
  public class TapahtumatIterator implements Iterator<Tapahtuma> {
    private int kohdalla = 0;
    
    public boolean hasNext() {
      return (this.kohdalla < Tapahtumat.this.getLkm());
    }
    
    public Tapahtuma next() throws NoSuchElementException {
      if (!hasNext())
        throw new NoSuchElementException("Ei löydy!"); 
      return Tapahtumat.this.anna(this.kohdalla++);
    }
    
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Ei poisteta!");
    }
  }
  
  public Iterator<Tapahtuma> iterator() {
    return new TapahtumatIterator();
  }
  
  public Collection<Tapahtuma> etsi() {
    Collection<Tapahtuma> loytyneet = new ArrayList<>();
    for (Tapahtuma t : this)
      loytyneet.add(t); 
    return loytyneet;
  }
  
  public Collection<Tapahtuma> etsi(String hakuehto) {
    String ehto = "*";
    if (hakuehto != null)
      ehto = hakuehto; 
    List<Tapahtuma> loytyneet = new ArrayList<>();
    for (Tapahtuma t : this) {
      if (t.anna().toUpperCase().startsWith(ehto.toUpperCase()))
        loytyneet.add(t); 
    } 
    Collections.sort(loytyneet, new Tapahtuma.Vertailija());
    return loytyneet;
  }
  
  public static void main(String[] args) {
    Tapahtumat tapahtumat = new Tapahtumat();
    Tapahtuma t1 = new Tapahtuma();
    Tapahtuma t2 = new Tapahtuma();
    t1.asetaTiedot("Tnimi", "Japan", "Tokyo", "1.1.2001", "200");
    t2.asetaTiedot("Ttapahtuma", "Ruotsi", "Stockholm", "12.12.2012", "100000");
    t1.rekisteroi();
    t2.rekisteroi();
    try {
      tapahtumat.lisaa(t1);
      tapahtumat.lisaa(t2);
      System.out.println("---------Tapahtumatesti!----------");
      for (int i = 0; i < tapahtumat.getLkm(); i++) {
        Tapahtuma tapahtuma = tapahtumat.anna(i);
        System.out.println("Tapahtuma nro: " + i);
        tapahtuma.tulosta(System.out);
        System.out.println("-----------------------");
      } 
    } catch (SailoException e) {
      System.out.println(e.getMessage());
    } 
  }
}
