package vptapahtumat;

import fxVptapahtumat.PvmTarkistaja;
import java.util.Collection;
import java.util.List;

public class Kasittelija {
  private Tapahtumat tapahtumat = new Tapahtumat();
  
  private Pelit pelit = new Pelit();
  
  public int getTapahtumaLkm() {
    return this.tapahtumat.getLkm();
  }
  
  public boolean onkoMuutettu() {
    if (this.tapahtumat.getMuutettu() || this.pelit.getMuutettu())
      return true; 
    return false;
  }
  
  public void poista(int nro) {
    this.tapahtumat.poista(nro);
  }
  
  public void setTapahtumiaMuokattu() {
    this.tapahtumat.setMuutettu(true);
  }
  
  public void lisaa(Tapahtuma tapahtuma) throws SailoException {
    this.tapahtumat.lisaa(tapahtuma);
  }
  
  public int onkoPeliOlemassa(String nimi) {
    for (Peli p : this.pelit) {
      if (p.getPeliNimi().equalsIgnoreCase(nimi))
        return p.getPeliID(); 
    } 
    return -1;
  }
  
  public boolean onkoPvmOikea(String pvm) {
    PvmTarkistaja pt = new PvmTarkistaja(pvm);
    return pt.tarkista();
  }
  
  public int onkoTapahtumaOlemassa(String nimi) {
    for (Tapahtuma t : this.tapahtumat) {
      if (t.getNimi().equalsIgnoreCase(nimi))
        return t.getTunnusNro(); 
    } 
    return -1;
  }
  
  public void lisaa(Peli peli) throws SailoException {
    this.pelit.lisaa(peli);
  }
  
  public Collection<Tapahtuma> etsi(String hakuehto) throws SailoException {
    return this.tapahtumat.etsi(hakuehto);
  }
  
  public Tapahtuma annaTapahtuma(int i) throws IndexOutOfBoundsException {
    return this.tapahtumat.anna(i);
  }
  
  public Tapahtuma annaTapahtumaTunnusNro(int nro) throws IndexOutOfBoundsException {
    return this.tapahtumat.annaTunnusNro(nro);
  }
  
  public List<Peli> annaPelit(Tapahtuma tapahtuma) {
    return this.pelit.annaPelit(tapahtuma.getPeliID());
  }
  
  public List<Peli> annaPelit() {
    return this.pelit.annaPelit();
  }
  
  public void lueTiedostosta() throws SailoException {
    this.tapahtumat = new Tapahtumat();
    this.pelit = new Pelit();
    this.tapahtumat.lueTiedostosta();
    this.pelit.lueTiedostosta();
  }
  
  public void tallenna() throws SailoException {
    String virhe = "";
    try {
      this.tapahtumat.tallenna();
    } catch (SailoException ex) {
      virhe = ex.getMessage();
    } 
    try {
      this.pelit.tallenna();
    } catch (SailoException ex) {
      virhe = String.valueOf(virhe) + ex.getMessage();
    } 
    if (!"".equals(virhe))
      throw new SailoException(virhe); 
  }
  
  public static void main(String[] args) throws SailoException {
    Kasittelija k = new Kasittelija();
    Tapahtuma t1 = new Tapahtuma();
    Tapahtuma t2 = new Tapahtuma();
    t1.asetaTiedot("Tnimi", "Japan", "Tokyo", "1.1.2001", "200");
    t2.asetaTiedot("Ttapahtuma", "Ruotsi", "Stockholm", "12.12.2012", "100000");
    k.lisaa(t1);
    k.lisaa(t2);
    Peli p1 = new Peli();
    Peli p2 = new Peli();
    p1.asetaNimi("Heroes 3");
    p2.asetaNimi("Civ 4");
    k.lisaa(p1);
    k.lisaa(p2);
    System.out.println("============= Käsittelijän testi =================");
    for (int i = 0; i < k.getTapahtumaLkm(); i++) {
      Tapahtuma t = k.annaTapahtuma(i);
      System.out.println("Tapahtuma paikassa: " + i);
      t.tulosta(System.out);
      System.out.println("-------------------------------------------");
    } 
    List<Peli> loytyneet = k.annaPelit();
    for (Peli peli : loytyneet) {
      peli.tulosta(System.out);
      System.out.println("----------------------");
    } 
  }
}
