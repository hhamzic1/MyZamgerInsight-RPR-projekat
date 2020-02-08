package ba.etf.unsa.rpr;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


public class HomeController implements Initializable {

    public UserInfo trenutniKorisnik;
    public UserInfo mockKorisnik;
    public Map<String, Student> mapaStudenata;
    public RezultatiDAO dao;
    public boolean spasenoUBazu=false;
    private final CloseableHttpClient httpclient = HttpClients.createDefault();
    private final ObjectMapper objectMapper = new ObjectMapper();
    public HomeController(UserInfo user, Map<String, Student> l)
    {
        trenutniKorisnik=user;
        mapaStudenata=l;
        try {
            dao = RezultatiDAO.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private Label label;
    public JFXButton logOutBtn;
    public Label lbl_bsc;
    public Label lbl_studenti;
    public Label labelaIme;
    public Label imePrezime;
    public Label brIndeksa;
    public Label brPolozenih;
    public Label brNepolozenih;
    public Label godinaStudija;
    public Label spol;
    public Label danRodjenja;
    public Label mjesecRodjenja;
    public Label godinaRodjenja;
    public Label mjestoRodjenja;
    public Label prosjek;
    public Label lbl_rank;
    public Label specPoruka;
    public Label inicijali;
    public Label cifreIndeksa;
    public String[] nizImePrezime;
    public String imeMajke;
    public String imeOca;
    public Integer rankUGen;


    public ObservableList<Student> listaStd = FXCollections.observableArrayList();

    public Stage tempStejg;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String[] s = trenutniKorisnik.getImePrezime().split(" ");
        nizImePrezime=s;
        if(trenutniKorisnik.getSpol().equals("M")) {
            if(Locale.getDefault().getCountry().equals("US"))
            {
                labelaIme.setText("Hi, greetings "+s[0]);
                brIndeksa.setText("Number of index: "+trenutniKorisnik.dajBrojIndexa());
            }
           else{
               labelaIme.setText("Dobro došao "+s[0]);
                brIndeksa.setText("Broj indeksa: "+trenutniKorisnik.dajBrojIndexa());
            }
        }
        else{
            if(Locale.getDefault().getCountry().equals("US"))
            {
                labelaIme.setText("Hi, greetings "+s[0]);
                brIndeksa.setText("Number of index: "+trenutniKorisnik.dajBrojIndexa());
            }
            else{
                labelaIme.setText("Dobro došla "+s[0]);
                brIndeksa.setText("Number of index: "+trenutniKorisnik.dajBrojIndexa());
            }
        }
        String inc = ""+s[0].charAt(0)+s[1].charAt(0);
        inicijali.setText(inc);
        imePrezime.setText(trenutniKorisnik.getImePrezime());
        brPolozenih.setText(String.valueOf(trenutniKorisnik.dajPolozene().size()));
        brNepolozenih.setText(String.valueOf(trenutniKorisnik.dajNepolozene().size()));

        if(trenutniKorisnik.getMapaRezultata().size()<=10)
        {
            godinaStudija.setText("1");
        }
        else if(trenutniKorisnik.getMapaRezultata().size()>10 && trenutniKorisnik.getMapaRezultata().size()<=22)
        {
            godinaStudija.setText("2");
        }
        else
        {
            godinaStudija.setText("3");
        }
        spol.setText(trenutniKorisnik.getSpol());
        danRodjenja.setText(String.valueOf(trenutniKorisnik.getDatumRodjenja().getDayOfMonth()));
        mjesecRodjenja.setText(String.valueOf(trenutniKorisnik.getDatumRodjenja().getMonth()));
        godinaRodjenja.setText(String.valueOf(trenutniKorisnik.getDatumRodjenja().getYear()));
        mjestoRodjenja.setText(trenutniKorisnik.getGradRodjenja());
        prosjek.setText(String.format("%.2f", trenutniKorisnik.dajProsjek()));
        String[] t1=trenutniKorisnik.getOtac().split(" ");
        String[] t2=trenutniKorisnik.getMajka().split(" ");
        imeOca=t1[0];
        imeMajke=t2[0];
        if(trenutniKorisnik.dajProsjek()>=8)
        {
            if(Locale.getDefault().getCountry().equals("US"))
            {
                specPoruka.setText("Congratulations on average GPA over 8, I think that"+imeOca+" and "+imeMajke+" are proud!");
            }
           else specPoruka.setText("Svaka čast na prosjeku preko 8, mislim da su "+t1[0]+" i "+t2[0]+" ponosni :)" );
        }
        else
        {
            if(Locale.getDefault().getCountry().equals("US")) specPoruka.setText("Just keep studying hard and the GPA will be over 8 I have no doubt about it ;)");
            else specPoruka.setText("Samo nastavi jako učiti i biće prosjek preko 8, ne sumnjam u tebe, nema predaje ;)");
        }
        if(trenutniKorisnik.dajPolozene().size()>=31)
        {
            if(Locale.getDefault().getCountry().equals("US")) {
                lbl_bsc.setText("Completed BSc: yes");
                specPoruka.setText("Congratulations on completed BSc studies, " + imeOca + " and " + imeMajke + " must be proud :)");
            }

            else{
                lbl_bsc.setText("Završio BSc: da");
                specPoruka.setText("Čestitke na završenom BSc, "+t1[0]+" i "+t2[0]+" mora da su ponosni :)");
            }

        }
        else
        {
            if(Locale.getDefault().getCountry().equals("US")) lbl_bsc.setText("Completed BSc: no");
            else lbl_bsc.setText("Završio BSc: ne");
        }

        if(Locale.getDefault().getCountry().equals("US"))  lbl_studenti.setText("Students in generation("+this.mapaStudenata.size()+")");
        else lbl_studenti.setText("Studenata u generaciji("+this.mapaStudenata.size()+")");
        listaStd.clear();
        listaStd.addAll(mapaStudenata.values());
        listaStd.sort((o1,o2)->{
            if(o1.getNepolozeni().size()==0 && o2.getNepolozeni().size()==0)
            {
                if(o1.getProsjekBachelor() > o2.getProsjekBachelor()) return -1;
                else if(o1.getProsjekBachelor() < o2.getProsjekBachelor()) return 1;
                else
                {
                    return 0;
                }
            }
            else if(o1.getPolozeni().size() > o2.getPolozeni().size()) return -1;
            else if(o1.getPolozeni().size() < o2.getPolozeni().size())
            {
                return 1;
            }
            else
            {
                if(o1.getProsjekBachelor() > o2.getProsjekBachelor()) return -1;
                else if(o1.getProsjekBachelor() < o2.getProsjekBachelor()) return 1;
                else
                {
                    return 0;
                }
            }
        });
        for(int i = 0; i<listaStd.size(); i++)
        {
            if(listaStd.get(i).getBrojIndeksa().equals(Integer.toString(trenutniKorisnik.dajBrojIndexa())))
            {
                i++;
                if(Locale.getDefault().getCountry().equals("US")){
                    lbl_rank.setText("Rank in generation("+i+")");
                    rankUGen=i;
                }
                else{
                    lbl_rank.setText("Rank u generaciji("+i+")");
                    rankUGen=i;
                }
                break;
            }
        }


    }

    public void mojiRezultatiAction(ActionEvent event)
    {
        ObservableList<Predmet> listica = FXCollections.observableArrayList();
        for(Map.Entry<String, Integer> it : trenutniKorisnik.getMapaRezultata().entrySet())
        {
            listica.add(new Predmet(it.getKey(), it.getValue()));
        }

        Parent p2 = null;
        try {
            FXMLLoader ldr = new FXMLLoader(getClass().getResource("/fxml/MyResults.fxml"), ResourceBundle.getBundle("Translation"));
            MojiRezultatiController mc = new MojiRezultatiController(listica);
            ldr.setController(mc);
            p2=ldr.load();
            Scene scena2 = new Scene(p2);
            Stage prozor2= new Stage();
            tempStejg=prozor2;
            if(Locale.getDefault().getCountry().equals("US")) prozor2.setTitle("My results");
            else prozor2.setTitle("Moji rezultati");
            prozor2.setScene(scena2);
            prozor2.setResizable(false);
            prozor2.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void rezGeneracijeAction(ActionEvent event)
    {
        Parent p2 = null;
        try {
            FXMLLoader ldr = new FXMLLoader(getClass().getResource("/fxml/OnBoard.fxml"), ResourceBundle.getBundle("Translation"));
            OtherRezultatiController mc = new OtherRezultatiController(listaStd, trenutniKorisnik);
            ldr.setController(mc);
            p2=ldr.load();
            Scene scena2 = new Scene(p2);
            Stage prozor2= new Stage();
            tempStejg=prozor2;
            if(Locale.getDefault().getCountry().equals("US")) prozor2.setTitle("Results of my generation");
            else prozor2.setTitle("Rezultati moje generacije");
            prozor2.setScene(scena2);
            prozor2.show();
            prozor2.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void rezDrugeGenAction()
    {
        List<String> choices = new ArrayList<>();
        for(int i = 2010; i<=Calendar.getInstance().get(Calendar.YEAR); i++)
        {
            if(i==trenutniKorisnik.dajGodinuUpisa()) continue;
            choices.add(Integer.toString(i));
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("2010", choices);
        if(Locale.getDefault().getCountry().equals("US")){
            dialog.setTitle("Choose dialog");
            dialog.setHeaderText("Pick a generation to see BSc results!\nResults may vary because of \n" +
                    "inconsistencies in Zamger reports of older generations\n" +
                    "for relevant results look at passed/failed exams of the students!");
            dialog.setContentText("Pick a year:");
        }
        else {
            dialog.setTitle("Dijalog odabira");
            dialog.setHeaderText("Izaberite generaciju kako bi vidjeli rezultate za BSc!\nRezultati mogu varirati zbog nekonzistentnosti izvještaja u starijim godinama\n" +
                    "za realno stanje gledajte položene/nepoložene predmete studenata!");
            dialog.setContentText("Izaberite godinu:");
        }

        String rez="";
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            rez=result.get();
            mockKorisnik = new UserInfo("mock", -1);
            mockKorisnik.setGodinaUpisa(Integer.parseInt(rez));

            Task<Boolean> novaGen = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    try{
                        mockKorisnik.setImenaIndeksiEtf();
                        mockKorisnik.inicijalizujGeneraciju();
                        mockKorisnik.formirajBachelor();
                        mockKorisnik.prikaziRezultateMojeGeneracije();
                        return true;
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            Thread thread = new Thread(novaGen);
            thread.setDaemon(true);
            thread.start();

            Parent p = null;
            try {
                p = FXMLLoader.load(getClass().getResource("/fxml/LoginLoader.fxml"), ResourceBundle.getBundle("Translation"));
                p.setVisible(true);
                Scene scena = new Scene(p);
                Stage prozor = new Stage();
                if(Locale.getDefault().getCountry().equals("US")) prozor.setTitle("Collecting data...");
                else prozor.setTitle("Prikupljam podatke...");
                prozor.setScene(scena);
                prozor.show();


                novaGen.setOnSucceeded(ev ->{

                    prozor.close();
                    Parent p2 = null;
                    try {
                        FXMLLoader ldr = new FXMLLoader(getClass().getResource("/fxml/OnBoard.fxml"), ResourceBundle.getBundle("Translation"));
                        ObservableList<Student> tempL = FXCollections.observableArrayList();

                        tempL.addAll(mockKorisnik.getMojaGeneracijaSve().values());
                        tempL.sort((o1,o2)->{
                            if(o1.getNepolozeni().size()==0 && o2.getNepolozeni().size()==0)
                            {
                                if(o1.getProsjekBachelor() > o2.getProsjekBachelor()) return -1;
                                else if(o1.getProsjekBachelor() < o2.getProsjekBachelor()) return 1;
                                else
                                {
                                    return 0;
                                }
                            }
                            else if(o1.getPolozeni().size() > o2.getPolozeni().size()) return -1;
                            else if(o1.getPolozeni().size() < o2.getPolozeni().size())
                            {
                                return 1;
                            }
                            else
                            {
                                if(o1.getProsjekBachelor() > o2.getProsjekBachelor()) return -1;
                                else if(o1.getProsjekBachelor() < o2.getProsjekBachelor()) return 1;
                                else
                                {
                                    return 0;
                                }
                            }
                        });

                        OtherRezultatiController mc = new OtherRezultatiController(tempL,mockKorisnik);
                        ldr.setController(mc);
                        p2=ldr.load();
                        Scene scena2 = new Scene(p2);
                        Stage prozor2= new Stage();
                        if(Locale.getDefault().getCountry().equals("US")) prozor2.setTitle("Results of "+mockKorisnik.dajGodinuUpisa()+" generation");
                        else prozor2.setTitle("Rezultati "+mockKorisnik.dajGodinuUpisa()+" generacije");
                        prozor2.setScene(scena2);
                        tempStejg=prozor2;
                        prozor2.setResizable(false);
                        prozor2.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }

    public void instaBtnAction(ActionEvent event)
    {
        String url = "https://www.instagram.com/who.so/";

        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void logOutBtnAction(ActionEvent event)
    {
        Stage prozorHome = (Stage) logOutBtn.getScene().getWindow();
        if(tempStejg!=null) tempStejg.close();
        prozorHome.close();
        Parent p2 = null;
        try {
            FXMLLoader ldr = new FXMLLoader(getClass().getResource("/fxml/LoginScreen.fxml"), ResourceBundle.getBundle("Translation"));
            ldr.setController(new Controller());
            p2 = ldr.load();
            Scene scena2 = new Scene(p2);
            Stage prozor2 = new Stage();
            prozor2.setTitle("MyZamsight");
            prozor2.setScene(scena2);
            prozor2.setResizable(false);
            prozor2.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gitAction(ActionEvent event){
        String url = "https://github.com/hhamzic1";

        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void fbAction(ActionEvent event){

        String url = "https://www.facebook.com/it.is.whoso";

        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void englJezikAction(ActionEvent event){
        Locale.setDefault(new Locale("en", "US"));
        restartJezik();
    }

    public void bosJezikAction(ActionEvent event){
        Locale.setDefault(new Locale("bs"));
        restartJezik();
    }


    private void restartJezik(){
        Stage scene = (Stage) inicijali.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"), ResourceBundle.getBundle("Translation"));
        loader.setController(this);
        try {
            scene.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void aboutMenuAction(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if(Locale.getDefault().getCountry().equals("US")){
            alert.setTitle("About app");
            alert.setWidth(700);
            alert.setHeight(500);
            alert.setHeaderText("About app");
            alert.setContentText("This app is made as a project for Development of program solutions subject 2019/2020. study year\n" +
                    "The app is strictly made for students of ETF and it demands login on Zamger with student access data.\n" +
                    "App uses data that can be easily found on the internet with a little bit better Google search and Zamger reports and Zamger API's as main source of the informations.\n" +
                    "The data is NOT collected ILLEGALY!\n" +
                    "Zamger login is demanded as the author thinks that this should stay in the circles of ETF only!\n" +
                    "The app is developed from scratch by Huso Hamzic.");

            alert.showAndWait();
        }
        else {
            alert.setTitle("O aplikaciji");
            alert.setWidth(700);
            alert.setHeight(500);
            alert.setHeaderText("O aplikaciji");
            alert.setContentText("Aplikacija je napravljena kao projekat za predmet Razvoj programskih rješenja 2019/2020 studijske godine.\n" +
                    "Napravljena je striktno za studente ETF-a jer zahtjeva login sa pristupnim podacima sa Zamgera.\n" +
                    "Aplikacija koristi podatke koji se lahko mogu naći na internetu uz malo bolji Google search i Zamger izvještaje kao i Zamger API-je" +
                    " kako bi došla do istih.\nDo podataka se NE dolazi na ILEGALAN način.\n" +
                    "Zamger login je stavljen kako bi ovo sve ostalo u krugovima ETF-a.\n" +
                    "Aplikaciju je od nule razvio Huso Hamzić.");

            alert.showAndWait();
        }

    }

    public void exitAction(ActionEvent event){
        System.exit(0);
    }

}