package ba.etf.unsa.rpr;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class MojiRezultatiController implements Initializable {

   public ObservableList<Predmet> lista = FXCollections.observableArrayList();

    @FXML
    public ListView<Predmet> lview;

    public MojiRezultatiController(ObservableList<Predmet> temp)
    {
        lista=temp;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lview.setItems(lista);
        lview.refresh();
    }
}
