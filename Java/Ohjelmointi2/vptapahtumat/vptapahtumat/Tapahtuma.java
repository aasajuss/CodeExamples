package vptapahtumat;

import fi.jyu.mit.fxgui.Dialogs;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Comparator;

public class Tapahtuma {
  private int tunnusNro;
  
  private String nimi;
  
  private String maa;
  
  private String kaupunki;
  
  private String paivamaara;
  
  private String palkinto;
  
  private int peliID;
  
  public Tapahtuma() {
    this.nimi = "";
    this.maa = "";
    this.kaupunki = "";
    this.paivamaara = "";
    this.palkinto = "";
    this.peliID = 0;
  }
  
  private static int seuraavaNro = 1;
  
  public int getPeliID() {
    return this.peliID;
  }
  
  public String getNimi() {
    return this.nimi;
  }
  
  public String getMaa() {
    return this.maa;
  }
  
  public String getKaupunki() {
    return this.kaupunki;
  }
  
  public String getPvm() {
    return this.paivamaara;
  }
  
  public String getPalkinto() {
    return this.palkinto;
  }
  
  public int getTunnusNro() {
    return this.tunnusNro;
  }
  
  public void setPeliID(int ID) {
    this.peliID = ID;
  }
  
  private void setTunnusNro(int nr) {
    this.tunnusNro = nr;
    if (this.tunnusNro >= seuraavaNro)
      seuraavaNro = this.tunnusNro + 1; 
  }
  
  public void vastaaTestiArvot() {
    this.nimi = "Tyhja";
    this.maa = "Tyhja";
    this.kaupunki = "Tyhja";
    this.paivamaara = "Tyhja";
    this.palkinto = "Tyhja";
    this.peliID = 3;
  }
  
  public int rekisteroi() {
    this.tunnusNro = seuraavaNro;
    seuraavaNro++;
    return this.tunnusNro;
  }
  
  public void asetaTiedot(String tapahtumanNimi, String tapahtumanMaa, String tapahtumanKaupunki, String tapahtumanPaivamaara, String tapahtumanPalkinto) {
    this.nimi = tapahtumanNimi;
    this.maa = tapahtumanMaa;
    this.kaupunki = tapahtumanKaupunki;
    this.paivamaara = tapahtumanPaivamaara;
    this.palkinto = tapahtumanPalkinto;
  }
  
  public void parse(String rivi) {
    String[] s = rivi.split("\\|");
    if (s.length > 7)
      Dialogs.showMessageDialog("Tiedoston tapahtuma " + s[2] + " sisältää liikaa kenttiä!"); 
    setTunnusNro(Integer.parseInt(s[0]));
    this.peliID = Integer.parseInt(s[1]);
    this.nimi = s[2];
    this.maa = s[3];
    this.kaupunki = s[4];
    this.paivamaara = s[5];
    this.palkinto = s[6];
  }
  
  public void paivita(String rivi) {
    String[] s = rivi.split("\n");
    this.nimi = s[0].substring(6);
    this.maa = s[1].substring(5);
    this.kaupunki = s[2].substring(10);
    this.paivamaara = s[3].substring(12);
    this.palkinto = s[4].substring(10);
  }
  
  public boolean equals(Object tapahtuma) {
    if (tapahtuma == null)
      return false; 
    return toString().equals(tapahtuma.toString());
  }
  
  public int hashCode() {
    return this.tunnusNro;
  }
  
  public String anna() {
    try {
      return this.nimi;
    } catch (Exception ex) {
      return "";
    } 
  }
  
  public void tulosta(OutputStream os) {
    PrintStream out = new PrintStream(os);
    out.println("Nimi: " + this.nimi);
    out.println("Maa: " + this.maa);
    out.println("Kaupunki: " + this.kaupunki);
    out.println("Päivämäärä: " + this.paivamaara);
    out.println("Palkinto: " + this.palkinto);
  }
  
  public String toString() {
    return String.valueOf(this.tunnusNro) + "|" + this.peliID + "|" + this.nimi + "|" + this.maa + "|" + this.kaupunki + "|" + this.paivamaara + 
      "|" + this.palkinto;
  }
  
  public static class Vertailija implements Comparator<Tapahtuma> {
    public int compare(Tapahtuma j1, Tapahtuma j2) {
      String s1 = j1.getNimi();
      String s2 = j2.getNimi();
      return s1.compareTo(s2);
    }
  }
  
  public static void main(String[] args) {
    Tapahtuma t = new Tapahtuma();
    t.nimi = "eSportsEvent";
    t.maa = "USA";
    t.kaupunki = "New York";
    t.paivamaara = "11.1.2014";
    t.palkinto = "500 000$";
    t.rekisteroi();
    t.tulosta(System.out);
    System.out.println(t.getTunnusNro());
    System.out.println("-----------------------------------");
    Tapahtuma blizzCon = new Tapahtuma();
    blizzCon.asetaTiedot("BlizzCon", "USA", "California", "8.11.2016", "250 000€");
    System.out.println(blizzCon.getTunnusNro());
    blizzCon.rekisteroi();
    blizzCon.tulosta(System.out);
    System.out.println(blizzCon.getTunnusNro());
  }
}
