package vptapahtumat;

import java.io.OutputStream;
import java.io.PrintStream;

public class Peli {
  private int peliID;
  
  private static int seuraavaNro = 1;
  
  private String nimi;
  
  public int getPeliID() {
    return this.peliID;
  }
  
  public void vastaaTyhja() {
    this.nimi = "Tyhja";
  }
  
  public String getPeliNimi() {
    return this.nimi;
  }
  
  public void asetaNimi(String pelinNimi) {
    this.nimi = pelinNimi;
  }
  
  public void tulosta(OutputStream os) {
    PrintStream out = new PrintStream(os);
    out.println("Peli: " + this.nimi);
  }
  
  public String toString() {
    return String.valueOf(this.peliID) + "|" + this.nimi;
  }
  
  private void setID(int nr) {
    this.peliID = nr;
    if (this.peliID >= seuraavaNro)
      seuraavaNro = this.peliID + 1; 
  }
  
  public int rekisteroi() {
    this.peliID = seuraavaNro;
    seuraavaNro++;
    return this.peliID;
  }
  
  public void parse(String rivi) {
    String[] s = rivi.split("\\|");
    setID(Integer.parseInt(s[0]));
    this.nimi = s[1];
  }
  
  public boolean equals(Object obj) {
    if (obj == null)
      return false; 
    return toString().equals(obj.toString());
  }
  
  public int hashCode() {
    return this.peliID;
  }
  
  public static void main(String[] args) {
    Peli peli = new Peli();
    peli.asetaNimi("SFV");
    peli.tulosta(System.out);
    Peli diablo = new Peli();
    diablo.asetaNimi("Diablo");
    diablo.rekisteroi();
    diablo.tulosta(System.out);
    System.out.println(diablo.getPeliID());
    Peli diablo2 = new Peli();
    System.out.println(diablo2.getPeliID());
    diablo2.asetaNimi("Diablo 2");
    diablo2.rekisteroi();
    diablo2.tulosta(System.out);
    System.out.println(diablo2.getPeliID());
  }
}
