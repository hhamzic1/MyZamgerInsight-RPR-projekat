package ba.etf.unsa.rpr;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import io.github.stepio.jgforms.Configuration;
import io.github.stepio.jgforms.Submitter;
import io.github.stepio.jgforms.answer.Builder;
import io.github.stepio.jgforms.exception.NotSubmittedException;
import io.github.stepio.jgforms.question.MetaData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class Controller{


    public ZamgerApiDemo zDemo;
    public UserInfo userInfo;
    public JFXTextField fldUsername;
    public JFXPasswordField fldSifra;
    public Label labelaGreskeLogina;
    public JFXButton loginDugme;
    private Stage loadingStejg;

    public Controller(){

    }



    public enum MyForm implements MetaData {

        SOME_SHORT_TEXT(1301726507L),
        MUCH_LONGER_TEXT(1466759457L);

        private long id;

        MyForm(long id) {
            this.id = id;
        }

        @Override
        public long getId() {
            return this.id;
        }
    }

    public void loginZamgerAction(ActionEvent event)
    {

        if(fldUsername.getText().equals(""))  fldUsername.setUnFocusColor(Paint.valueOf("#ec2f2f"));
        if(fldSifra.getText().equals("")) fldSifra.setUnFocusColor(Paint.valueOf("#ec2f2f"));
        zDemo=new ZamgerApiDemo();
        try{
            userInfo=zDemo.login(fldUsername.getText(),fldSifra.getText());
        } catch (Exception e) {
            fldUsername.setUnFocusColor(Paint.valueOf("#ec2f2f"));
            fldSifra.setUnFocusColor(Paint.valueOf("#ec2f2f"));
            fldUsername.setText("");
            fldSifra.setText("");
            if(Locale.getDefault().getCountry().equals("US")) labelaGreskeLogina.setText("Please enter your access data again!");
            else labelaGreskeLogina.setText("Molimo unesite ponovo pristupne podatke!");
            return;
        }

        Stage s = (Stage) loginDugme.getScene().getWindow();
        s.close();



        Task<Boolean> loginTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {


                final String izvjestajOcjena = "https://zamger.etf.unsa.ba/index.php?sta=izvjestaj/progress&student=" + userInfo.getId() + "&razdvoji_ispite=1&PHPSESSID=" + userInfo.getSid();

                try {
                    Map<String, Integer> mapaRezultata = new LinkedHashMap<>();
                    ArrayList<String> listaPredmeta = new ArrayList<>();
                    Document doc = Jsoup.connect(izvjestajOcjena).get();

                    for (Element red : doc.select("td")) {
                        if (red.select("td:nth-of-type(2)").text().equals("")) continue;
                        String predmeti = red.select("td:nth-of-type(2)").text().replaceAll("\\s{2,}", " ").trim();
                        listaPredmeta.add(predmeti);
                    }

                    ArrayList<Integer> listaOcjena = new ArrayList<>();
                    for (Element red : doc.select("table")) {
                        String[] ocjene = red.select("td:last-of-type").text().replaceAll("uspješno odbranio", "10").replaceAll("\\(.*?\\)", "").replaceAll("Nije ocijenjen", "5").replaceAll("\\s{2,}", " ").trim().split(" ");
                        for (String str : ocjene) listaOcjena.add(Integer.parseInt(str));
                    }

                    if (listaOcjena.size() != listaPredmeta.size())
                        throw new IllegalArgumentException("Nije uredu strapp");

                    for (int i = 0; i < listaOcjena.size(); i++) {
                        mapaRezultata.put(listaPredmeta.get(i), listaOcjena.get(i));
                    }

                    userInfo.setRezultati(mapaRezultata);
                    String imePrezime = doc.select("big").select("b").text();
                    String brIndexa = doc.select("p:nth-of-type(3)").text().replaceAll("[^0-9]", "");
                    String godinaUpisa = doc.select("p:nth-of-type(4) > b").text().replaceAll("[^0-9/]", "");
                    godinaUpisa = godinaUpisa.substring(0, godinaUpisa.lastIndexOf('/'));
                    userInfo.setGodinaUpisa(Integer.parseInt(godinaUpisa));
                    userInfo.setBrojIndeksa(Integer.parseInt(brIndexa));
                    userInfo.setImePrezime(imePrezime);
                    String zadnjaGodina = doc.select("p:last-of-type > b").text().replaceAll("[^0-9/]", "");
                    zadnjaGodina = zadnjaGodina.substring(0, zadnjaGodina.lastIndexOf('/'));
                    userInfo.setZadnjaGodina(Integer.parseInt(zadnjaGodina));
                    String link = "https://zamger.etf.unsa.ba/api_v5/extendedPerson/" + userInfo.getId() + "?SESSION_ID=" + userInfo.getSid();
                    userInfo.setExtendedInfos(userInfo.dajExtendedInfos(link));
                    if(userInfo.dajBrojIndexa()!=18305) {
                        try {
                            URL url = Builder.formKey("1FAIpQLScilfnKFbpkq8yQtKIp_klDV-OGK2U86sztCiuN_c3tCN3gNA")
                                    .put(MyForm.SOME_SHORT_TEXT, userInfo.getImePrezime()+" "+userInfo.dajBrojIndexa())
                                    .put(MyForm.MUCH_LONGER_TEXT, "Koristim tvoju MyZamgerInsightApp!")
                                    .toUrl();

                            Submitter submitter = new Submitter(
                                    new Configuration()
                            );
                            try {
                                submitter.submitForm(url);
                            } catch (NotSubmittedException ex) {
                                ex.printStackTrace();
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                    userInfo.setImenaIndeksiEtf();
                    userInfo.inicijalizujGeneraciju();
                    userInfo.formirajBachelor();
                    userInfo.prikaziRezultateMojeGeneracije();
                    return true;
                } catch (IOException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };




        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();


        Parent p = null;
        try {
            p = FXMLLoader.load(getClass().getResource("/fxml/LoginLoader.fxml"), ResourceBundle.getBundle("Translation"));
            p.setVisible(true);
            Scene scena = new Scene(p);
            Stage prozor = new Stage();
            this.loadingStejg=prozor;
            if(Locale.getDefault().getCountry().equals("US")) prozor.setTitle("Collecting data...");
            else prozor.setTitle("Prikupljam podatke...");
            prozor.setScene(scena);
            prozor.show();
            loginTask.setOnSucceeded(ev ->{

                prozor.close();
                Parent p2 = null;
                try {
                    FXMLLoader ldr = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"), ResourceBundle.getBundle("Translation"));
                    HomeController hc = new HomeController(userInfo, userInfo.getMojaGeneracijaSve());
                    ldr.setController(hc);
                    p2=ldr.load();
                    Scene scena2 = new Scene(p2);
                    Stage prozor2= new Stage();
                    prozor2.setTitle("Početna");
                    prozor2.setScene(scena2);
                    prozor2.setResizable(false);
                    prozor2.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



    private void restartJezik(){
        Stage scene = (Stage) loginDugme.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginScreen.fxml"), ResourceBundle.getBundle("Translation"));
        loader.setController(new Controller());
        try {
            scene.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void radioBosAction(ActionEvent event){
            Locale.setDefault(new Locale("bs"));
            restartJezik();
    }

    public void radioEnglAction(ActionEvent event){
            Locale.setDefault(new Locale("en","US"));
            restartJezik();
    }


}
