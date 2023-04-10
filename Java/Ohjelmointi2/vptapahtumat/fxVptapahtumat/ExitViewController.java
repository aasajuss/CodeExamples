package fxVptapahtumat;

import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class ExitViewController implements ModalControllerInterface<String> {
  @FXML
  private BorderPane pane;
  
  private boolean tallennetaan = true;
  
  @FXML
  private void handleOK() {
    this.tallennetaan = true;
    ModalController.closeStage((Node)this.pane);
  }
  
  @FXML
  private void handleCancel() {
    this.tallennetaan = false;
    ModalController.closeStage((Node)this.pane);
  }
  
  public String getResult() {
    if (this.tallennetaan)
      return "true"; 
    return "false";
  }
  
  public void setDefault(String arg0) {
    this.tallennetaan = false;
  }
  
  public void handleShown() {}
}
