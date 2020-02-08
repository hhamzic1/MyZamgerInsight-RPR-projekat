package ba.etf.unsa.rpr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import net.sf.jasperreports.engine.JRException;


import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class OtherRezultatiController implements Initializable {
    public ObservableList<Student> lista = FXCollections.observableArrayList();
    private Student odabraniStudent;
    public RezultatiDAO dao;

    @FXML
    public ListView<Student> listaVju;
    @FXML
    public TextField labelaIme;
    @FXML
    public TextField labelaPrezime;
    @FXML
    public TextField labelaBrojIndeksa;
    @FXML
    public ComboBox<Predmet> polPredmeti;
    @FXML
    public ComboBox<Predmet> nepolPredmeti;
    @FXML
    public Button btnPretrazi;
    @FXML
    public Label rankBr;
    public Button btnExport;
    public UserInfo trenutniKorisnik;

    public OtherRezultatiController(ObservableList<Student> temp, UserInfo trenutniKorisnik)
    {
        lista=temp;
        this.dao = RezultatiDAO.getInstance();
        this.trenutniKorisnik=trenutniKorisnik;
    }

    public int dajRankTrenutnog(String brInd)
    {
        for(int i = 0; i<lista.size(); i++)
        {
            if(lista.get(i).getBrojIndeksa().equals(brInd))
            {
                i++;
                return i;
            }
        }
        return 0;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listaVju.setItems(lista);
        listaVju.getSelectionModel().selectedItemProperty().addListener((obs, oldKorisnik, newKorisnik) -> {
            odabraniStudent=newKorisnik;
            if(odabraniStudent!=null) {
                String[] temp = odabraniStudent.getImePrezime().split(" ");
                labelaIme.setText(temp[1]);
                labelaPrezime.setText(temp[0]);
                labelaBrojIndeksa.setText(odabraniStudent.getBrojIndeksa());
                ObservableList<Predmet> tempLista = FXCollections.observableArrayList();
                ObservableList<Predmet> tempLista2 = FXCollections.observableArrayList();
                for (Map.Entry<String, Integer> it : odabraniStudent.getMapaPredmeta().entrySet()) {
                    if (it.getValue() > 5) {
                        tempLista.add(new Predmet(it.getKey(), it.getValue()));
                    } else {
                        tempLista2.add(new Predmet(it.getKey(), it.getValue()));
                    }
                }
                polPredmeti.setItems(tempLista);
                nepolPredmeti.setItems(tempLista2);
                rankBr.setText(Integer.toString(dajRankTrenutnog(odabraniStudent.getBrojIndeksa())));
            }
        });
        listaVju.refresh();

    }

    public void btnPretraziAction(ActionEvent event)
    {
        if((labelaIme.getText().equals("") || labelaPrezime.getText().equals("") )&& labelaBrojIndeksa.getText().equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upozorenje");
            alert.setHeaderText(null);
            alert.setContentText("Ne postoji osoba sa tim podacima u bazi");
            alert.showAndWait();
            return;
        }
        int indeks = -1;
        if(labelaBrojIndeksa.getText().equals(""))
        {
            for(int i=0; i<lista.size(); i++)
            {
                    if(lista.get(i)==null || lista.get(i).getImePrezime()==null) continue;
                    if (lista.get(i).getImePrezime().toLowerCase().equals(labelaPrezime.getText().toLowerCase() + " " + labelaIme.getText().toLowerCase())) {
                        indeks = i;
                        break;
                    }
            }
            if(indeks!=-1)
            {
                listaVju.getSelectionModel().select(indeks);
                return;
            }
        }
        else
        {
            for(int i=0; i<lista.size(); i++)
            {
                if(lista.get(i)==null) continue;
                if(lista.get(i).getBrojIndeksa().equals(labelaBrojIndeksa.getText()))
                {
                    indeks=i;
                    break;
                }
            }
            if(indeks!=-1)
            {
                listaVju.getSelectionModel().select(indeks);
                return;
            }
        }
        labelaBrojIndeksa.setText("");
        labelaIme.setText("");
        labelaPrezime.setText("");
        if(Locale.getDefault().getCountry().equals("US")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("The pearson you are looking is not in this generation");
            alert.showAndWait();
        }
       else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upozorenje");
            alert.setHeaderText(null);
            alert.setContentText("Osoba koju tražite nije u ovoj generaciji");
            alert.showAndWait();
        }

    }

    public void exportAction(ActionEvent event){
        dao.obrisiTrenutnePodatke();
        dao.ubaciStudenta(trenutniKorisnik.getMojaGeneracijaSve());
        try {
            new PrintReport().showReport(dao.getConn());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }


}
