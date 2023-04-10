package fxVptapahtumat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import vptapahtumat.Kasittelija;

public class VptapahtumatMain extends Application {
  public void start(Stage primaryStage) {
    try {
      FXMLLoader ldr = new FXMLLoader(getClass().getResource("VptapahtumatView.fxml"));
      Pane root = (Pane)ldr.load();
      AppGUIController appCtrl = (AppGUIController)ldr.getController();
      Scene scene = new Scene((Parent)root);
      scene.getStylesheets().add(getClass().getResource("vptapahtumat.css").toExternalForm());
      primaryStage.setScene(scene);
      primaryStage.setTitle("Videopelitapahtumat");
      primaryStage.setOnCloseRequest(event -> {
            paramAppGUIController.lopeta();
            event.consume();
          });
      Kasittelija kasittelija = new Kasittelija();
      appCtrl.setKasittelija(kasittelija);
      appCtrl.lueTiedosto();
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static void main(String[] args) {
    launch(args);
  }
}
