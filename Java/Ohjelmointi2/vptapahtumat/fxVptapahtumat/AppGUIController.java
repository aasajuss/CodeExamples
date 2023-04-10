package fxVptapahtumat;

import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ListChooser;
import fi.jyu.mit.fxgui.ModalController;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import vptapahtumat.Kasittelija;
import vptapahtumat.Peli;
import vptapahtumat.SailoException;
import vptapahtumat.Tapahtuma;

public class AppGUIController implements Initializable {
  @FXML
  private ScrollPane panelTapahtuma;
  
  @FXML
  private ListChooser chooserTapahtumat;
  
  @FXML
  private TextField hakuehto;
  
  @FXML
  private CheckBox nimirajaus;
  
  @FXML
  private CheckBox maarajaus;
  
  @FXML
  private CheckBox pvmrajaus;
  
  @FXML
  private CheckBox palkintorajaus;
  
  @FXML
  private CheckBox pelirajaus;
  
  private Kasittelija kasittelija;
  
  private Tapahtuma tapahtumaKohdalla;
  
  private TextArea areaTapahtuma;
  
  private ListView<Tapahtuma> listTapahtumat;
  
  private ObservableList<Tapahtuma> listdataTapahtumat;
  
  private String alkuperainen;
  
  public AppGUIController() {
    this.areaTapahtuma = new TextArea();
    this.listTapahtumat = new ListView();
    this.listdataTapahtumat = FXCollections.observableArrayList();
  }
  
  public void initialize(URL url, ResourceBundle bundle) {
    alusta();
  }
  
  @FXML
  private void handleUusiTapahtuma() {
    uusiTapahtuma();
  }
  
  @FXML
  private void handleMuokataan() {
    muokataan();
  }
  
  @FXML
  private void handleRajaus() {
    if (this.nimirajaus.isSelected()) {
      rajaus("nimi");
    } else if (this.maarajaus.isSelected()) {
      rajaus("maa");
    } else if (this.pvmrajaus.isSelected()) {
      rajaus("paivamaara");
    } else if (this.palkintorajaus.isSelected()) {
      rajaus("palkinto");
    } else if (this.pelirajaus.isSelected()) {
      rajaus("peli");
    } else {
      rajaus("nimi");
    } 
  }
  
  @FXML
  private void handleHaku() {
    hae(0);
  }
  
  @FXML
  private void handleTallenna() {
    tallenna();
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Huomautus");
    alert.setHeaderText(null);
    alert.setContentText("Tallennetaan!");
    alert.setResizable(false);
    alert.showAndWait();
  }
  
  @FXML
  private void handleMuokkaa() {
    muokkaa();
  }
  
  @FXML
  private void handlePoista() {
    poista();
  }
  
  @FXML
  private void handleTietoja() {
    tietoja();
  }
  
  @FXML
  private void handleLopeta() {
    lopeta();
  }
  
  private static class CellPeli extends ListCell<Tapahtuma> {
    protected void updateItem(Tapahtuma item, boolean empty) {
      super.updateItem(item, empty);
      setText(empty ? "" : item.getNimi());
    }
  }
  
  private void alusta() {
    this.panelTapahtuma.setContent((Node)this.areaTapahtuma);
    this.panelTapahtuma.setFitToHeight(true);
    BorderPane parent = (BorderPane)this.chooserTapahtumat.getParent();
    this.listTapahtumat.setPrefHeight(this.chooserTapahtumat.getPrefHeight());
    this.listTapahtumat.setPrefWidth(this.chooserTapahtumat.getPrefWidth());
    parent.setLeft((Node)this.listTapahtumat);
    this.listTapahtumat.setCellFactory(p -> new CellPeli());
    this.listTapahtumat.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> naytaTapahtuma());
    this.areaTapahtuma.setEditable(false);
    this.areaTapahtuma.setPrefWidth(this.panelTapahtuma.getPrefWidth());
  }
  
  private void poista() {
    Tapahtuma t = this.tapahtumaKohdalla;
    if (t == null)
      return; 
    if (Dialogs.showQuestionDialog("Poisto", "Poistetaanko tapahtuma " + t.getNimi() + "?", "Kyllä", "Ei")) {
      this.kasittelija.poista(t.getTunnusNro());
      int index = this.listTapahtumat.getSelectionModel().getSelectedIndex();
      hae(0);
      this.listTapahtumat.getSelectionModel().select(index);
    } else {
      return;
    } 
  }
  
  private void tallenna() {
    try {
      this.kasittelija.tallenna();
      return;
    } catch (SailoException ex) {
      Dialogs.showMessageDialog("Tallennus ei onnistunut! " + ex.getMessage());
      return;
    } 
  }
  
  private void muokataan() {
    final int muokkausnro = ((Tapahtuma)this.listTapahtumat.getSelectionModel().getSelectedItem()).getTunnusNro();
    String textAlk = this.areaTapahtuma.getText();
    String[] osiotAlk = textAlk.split("\n");
    final String alkperTapahtumaNimi = osiotAlk[0].substring(6);
    this.areaTapahtuma.setOnKeyPressed(new EventHandler<KeyEvent>() {
          public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.CONTROL) {
              String text = AppGUIController.this.areaTapahtuma.getText();
              if (!text.equals(AppGUIController.this.alkuperainen)) {
                String[] osiot = text.split("\n");
                String pvm = osiot[3].substring(12);
                String pelinNimi = osiot[5].substring(6);
                String tapahtumanNimi = osiot[0].substring(6);
                AppGUIController.this.uusiPeli(pelinNimi);
                if (AppGUIController.this.kasittelija.onkoTapahtumaOlemassa(tapahtumanNimi) > 0 && !tapahtumanNimi.equals(alkperTapahtumaNimi)) {
                  Dialogs.showMessageDialog("Samanniminen tapahtuma löytyy jo!");
                } else if (AppGUIController.this.kasittelija.onkoPvmOikea(pvm)) {
                  if (AppGUIController.this.kasittelija.onkoPeliOlemassa(pelinNimi) > 0) {
                    int pelinID = AppGUIController.this.kasittelija.onkoPeliOlemassa(pelinNimi);
                    AppGUIController.this.kasittelija.annaTapahtumaTunnusNro(muokkausnro).setPeliID(pelinID);
                    AppGUIController.this.kasittelija.annaTapahtumaTunnusNro(muokkausnro).paivita(text);
                    AppGUIController.this.hae(0);
                    AppGUIController.this.areaTapahtuma.setEditable(false);
                    AppGUIController.this.listTapahtumat.requestFocus();
                    Dialogs.showMessageDialog("Muokkaus vahvistettu!");
                  } else {
                    AppGUIController.this.kasittelija.annaTapahtumaTunnusNro(muokkausnro).paivita(text);
                    AppGUIController.this.hae(0);
                    AppGUIController.this.areaTapahtuma.setEditable(false);
                    AppGUIController.this.listTapahtumat.requestFocus();
                    Dialogs.showMessageDialog("Muokkaus vahvistettu!");
                  } 
                } else {
                  Dialogs.showMessageDialog("Päivämäärä ei ole oikein!");
                } 
              } else {
                AppGUIController.this.areaTapahtuma.setEditable(false);
                AppGUIController.this.listTapahtumat.requestFocus();
              } 
            } 
          }
        });
  }
  
  public void muokkaa() {
    this.alkuperainen = this.areaTapahtuma.getText();
    this.kasittelija.setTapahtumiaMuokattu();
    this.areaTapahtuma.setEditable(true);
    this.areaTapahtuma.requestFocus();
    Dialogs.showMessageDialog("Vahvista muokkauksesi lopuksi painamalla Ctrl!");
  }
  
  public void lopeta() {
    boolean muutettu = this.kasittelija.onkoMuutettu();
    if (muutettu) {
      String s = (String)ModalController.showModal(AppGUIController.class.getResource("ExitView.fxml"), "Tapahtumat", null, "");
      if (s.equals("true")) {
        tallenna();
        Platform.exit();
      } else {
        Platform.exit();
      } 
    } else {
      Platform.exit();
    } 
  }
  
  private void rajaus(String ehto) {
    if (ehto == "nimi") {
      ListanJarjestaja.JarjestaNimi n = new ListanJarjestaja.JarjestaNimi();
      this.listdataTapahtumat.sort(n);
    } else if (ehto == "maa") {
      ListanJarjestaja.JarjestaMaa m = new ListanJarjestaja.JarjestaMaa();
      this.listdataTapahtumat.sort(m);
    } else if (ehto == "paivamaara") {
      ListanJarjestaja.JarjestaPaivamaara p = new ListanJarjestaja.JarjestaPaivamaara();
      this.listdataTapahtumat.sort(p);
    } else if (ehto == "palkinto") {
      ListanJarjestaja.JarjestaPalkinto pa = new ListanJarjestaja.JarjestaPalkinto();
      this.listdataTapahtumat.sort(pa);
    } else if (ehto == "peli") {
      ListanJarjestaja.JarjestaPeli pe = new ListanJarjestaja.JarjestaPeli();
      this.listdataTapahtumat.sort(pe);
    } 
  }
  
  protected void naytaTapahtuma() {
    this.tapahtumaKohdalla = (Tapahtuma)this.listTapahtumat.getSelectionModel().getSelectedItem();
    if (this.tapahtumaKohdalla == null)
      return; 
    this.areaTapahtuma.setText("");
    Exception exception1 = null, exception2 = null;
  }
  
  public void lueTiedosto() {
    try {
      this.kasittelija.lueTiedostosta();
      hae(0);
    } catch (SailoException e) {
      hae(0);
      String virhe = e.getMessage();
      if (virhe != null)
        Dialogs.showMessageDialog(virhe); 
    } 
  }
  
  protected void hae(int pnr) {
    int pnro = pnr;
    if (pnr <= 0) {
      Tapahtuma kohdalla = getTapahtumaKohdalla();
      if (kohdalla != null)
        pnro = kohdalla.getTunnusNro(); 
    } 
    String ehto = this.hakuehto.getText();
    this.listdataTapahtumat.clear();
    this.listTapahtumat.setItems(null);
    int index = 0;
    try {
      Collection<Tapahtuma> tt = this.kasittelija.etsi(ehto);
      int i = 0;
      for (Tapahtuma t : tt) {
        if (t.getTunnusNro() == pnro)
          index = i; 
        this.listdataTapahtumat.add(t);
        i++;
      } 
    } catch (SailoException e) {
      e.printStackTrace();
    } 
    this.listTapahtumat.setItems(this.listdataTapahtumat);
    this.listTapahtumat.getSelectionModel().select(index);
  }
  
  private void tietoja() {
    ModalController.showModal(AppGUIController.class.getResource("AboutView.fxml"), "Tietoja", null, "");
  }
  
  protected void uusiTapahtuma() {
    Tapahtuma uusi = new Tapahtuma();
    uusi.rekisteroi();
    uusi.vastaaTestiArvot();
    try {
      this.kasittelija.lisaa(uusi);
    } catch (SailoException e) {
      Dialogs.showMessageDialog("Luominen ei onnistunut! " + e.getMessage());
      return;
    } 
    hae(uusi.getTunnusNro());
    uusiPeli();
  }
  
  private Tapahtuma getTapahtumaKohdalla() {
    return (Tapahtuma)this.listTapahtumat.getSelectionModel().getSelectedItem();
  }
  
  protected void uusiPeli() {
    boolean loytyi = false;
    Peli uusi = new Peli();
    uusi.vastaaTyhja();
    List<Peli> p = this.kasittelija.annaPelit();
    for (Peli peli : p) {
      if (peli.getPeliNimi().equalsIgnoreCase(uusi.getPeliNimi())) {
        loytyi = true;
        break;
      } 
    } 
    if (!loytyi) {
      uusi.rekisteroi();
      try {
        this.kasittelija.lisaa(uusi);
      } catch (SailoException e) {
        Dialogs.showMessageDialog("Luominen ei onnistunut! " + e.getMessage());
        return;
      } 
      hae(uusi.getPeliID());
    } 
  }
  
  protected void uusiPeli(String nimi) {
    boolean loytyi = false;
    Peli uusi = new Peli();
    uusi.asetaNimi(nimi);
    List<Peli> p = this.kasittelija.annaPelit();
    for (Peli peli : p) {
      if (peli.getPeliNimi().equalsIgnoreCase(uusi.getPeliNimi())) {
        loytyi = true;
        break;
      } 
    } 
    if (!loytyi) {
      uusi.rekisteroi();
      try {
        this.kasittelija.lisaa(uusi);
      } catch (SailoException e) {
        Dialogs.showMessageDialog("Luominen ei onnistunut! " + e.getMessage());
        return;
      } 
      hae(uusi.getPeliID());
    } 
  }
  
  public void setKasittelija(Kasittelija k) {
    this.kasittelija = k;
  }
}
