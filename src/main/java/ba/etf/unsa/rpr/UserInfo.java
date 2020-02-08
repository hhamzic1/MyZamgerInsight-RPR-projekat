package ba.etf.unsa.rpr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class UserInfo {
    private final String sid;
    private final int id;
    private int godinaUpisa;
    private int zadnjaGodina;
    private String imePrezime;
    private int brojIndeksa;
    private String spol, otac, majka, jmbg, trenutniGrad, gradRodjenja, trenutnaDrzava, drzavaRodjenja;
    private LocalDate datumRodjenja;
    private Map<String, Integer> mapaRezultata;
    private Map<String, Integer> mapaNepolozenih;
    private Map<String, Integer> mapaPolozenih;
    private Map<Integer, String> imenaMapa = new HashMap<>();
    private Predmeti predmeti = new Predmeti();
    private Map<String, Student> mojaGeneracijaSve; //studenti moje prve godine sa njihovim ostvarenim rezultatima (polje String je broj indexa)
    private ObservableList<Student> obsListaStudenata = FXCollections.observableArrayList();

    public Map<String, Student> getMojaGeneracijaSve() {
        return mojaGeneracijaSve;
    }

    UserInfo(final String sid, final int id) {
        this.sid = sid;
        this.id = id;
    }

    public void setZadnjaGodina(int zgodina) {
        this.zadnjaGodina = zgodina;
    }

    public int dajZadnjuGodinu() {
        return this.zadnjaGodina;
    }

    public void setGodinaUpisa(int gupisa) {
        this.godinaUpisa = gupisa;
    }

    public int dajGodinuUpisa() {
        return this.godinaUpisa;
    }

    public int dajBrojIndexa() {
        return this.brojIndeksa;
    }

    public String dajImePrezime() {
        return this.imePrezime;
    }

    public void setImePrezime(String s) {
        this.imePrezime = s;
    }

    public void setBrojIndeksa(int ind) {
        this.brojIndeksa = ind;
    }


    public void setRezultati(Map<String, Integer> temp) { //user
        this.mapaRezultata = temp;
    }

    public void ispisiNepolozene() {
        StringBuilder s = new StringBuilder();
        Map<String, Integer> tempMapa = this.dajNepolozene();
        for (Map.Entry<String, Integer> it : tempMapa.entrySet()) {
            s.append(it.getKey()).append(" ").append(it.getValue()).append("\n");
        }
        System.out.print(s);
    }

    public Map<String, Integer> dajPolozene() {
        double temp = this.dajProsjek();
        return this.mapaPolozenih;

    }

    public void ispisiPolozene() {
        StringBuilder s = new StringBuilder();
        Map<String, Integer> tempMapa = this.dajPolozene();
        for (Map.Entry<String, Integer> it : tempMapa.entrySet()) {
            s.append(it.getKey()).append(" ").append(it.getValue()).append("\n");
        }
        System.out.print(s);
    }

    public void ispisiSveRezultate() {
        StringBuilder s = new StringBuilder();
        for (Map.Entry<String, Integer> it : mapaRezultata.entrySet()) {
            s.append(it.getKey()).append(" ").append(it.getValue()).append("\n");
        }
        System.out.print(s);
    }

    public Map<String, Integer> dajNepolozene() {
        double temp = this.dajProsjek();
        return this.mapaNepolozenih;
    }

    public double dajProsjek() {
        int brPredmeta = 0;
        double suma = 0;
        this.mapaNepolozenih = new LinkedHashMap<>();
        this.mapaPolozenih = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> it : mapaRezultata.entrySet()) {
            if (it.getValue() == 5) {
                mapaNepolozenih.put(it.getKey(), it.getValue());
                continue;
            }
            brPredmeta++;
            suma += it.getValue();
            mapaPolozenih.put(it.getKey(), it.getValue());
        }
        return suma / brPredmeta;

    }

    public double dajProsjekPrveGodine() {
        double suma = 0;
        int brPredmeta = 0;
        for (Map.Entry<String, Integer> it : mapaRezultata.entrySet()) {
            if (it.getValue() == 5) continue;
            brPredmeta++;
            suma += it.getValue();
        }
        return suma / brPredmeta;
    }

    public String getSid() {
        return sid;
    }

    public int getId() {
        return id;
    }

    public void setImenaIndeksiEtf()
    {
        RezultatiDAO dao = RezultatiDAO.getInstance();
        try {
            imenaMapa = dao.dajImenaEtf();
        }catch (Exception e)
        {
            System.out.println("nije fino setovano imena i indeksi studenata sa etfa");
        }

        RezultatiDAO.removeInstance();
    }


    public ArrayList<String> dajExtendedInfos(String adresa) { //o useru
        ArrayList<String> lista = new ArrayList<>();
        URL url = null;
        try {
            url = new URL(adresa);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader ulaz = null;
        try {
            ulaz = new BufferedReader(new InputStreamReader(url.openStream(),
                    StandardCharsets.UTF_8));
            String json = "", line = null;
            while ((line = ulaz.readLine()) != null) {
                json = json + line;
            }
            JSONObject obj = new JSONObject(json);
            lista.add(obj.getString("sex"));
            lista.add(obj.getString("dateOfBirth"));
            lista.add(String.valueOf(obj.getBigInteger("jmbg")));
            JSONObject obj2 = obj.getJSONObject("placeOfBirth");
            lista.add(obj2.getString("name"));
            lista.add(obj2.getString("country"));
            JSONObject obj3=obj.getJSONObject("addressPlace");
            lista.add(obj3.getString("name"));
            lista.add(obj3.getString("country"));
            lista.add(obj.getString("fathersName")+" "+obj.getString("fathersSurname"));
            lista.add(obj.getString("mothersName")+" "+obj.getString("mothersSurname"));

            ulaz.close();
            return lista;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setExtendedInfos(ArrayList<String> infos) {
        if(infos.size()==0) throw new IllegalArgumentException("nije fino pročitan JSON o extended infos");
        this.spol=infos.get(0);
        this.datumRodjenja = LocalDate.parse(infos.get(1));
        this.jmbg=infos.get(2);
        this.gradRodjenja=infos.get(3);
        this.drzavaRodjenja=infos.get(4);
        this.trenutniGrad=infos.get(5);
        this.trenutnaDrzava=infos.get(6);
        this.otac=infos.get(7);
        this.majka=infos.get(8);
    }

    public String getImePrezime() {
        return imePrezime;
    }

    public String getSpol() {
        return spol;
    }

    public String getOtac() {
        return otac;
    }

    public String getMajka() {
        return majka;
    }

    public String getJmbg() {
        return jmbg;
    }

    public String getTrenutniGrad() {
        return trenutniGrad;
    }

    public String getGradRodjenja() {
        return gradRodjenja;
    }


    public String getTrenutnaDrzava() {
        return trenutnaDrzava;
    }

    public String getDrzavaRodjenja() {
        return drzavaRodjenja;
    }

    public LocalDate getDatumRodjenja() {
        return datumRodjenja;
    }

    public void inicijalizujGeneraciju()
    {
        this.mojaGeneracijaSve=new ConcurrentHashMap<>();
        int value=12; //id IM1
        if(this.dajGodinuUpisa()==2016 || this.dajGodinuUpisa()==2015) value = 2092; //pokupi sa IM1 studente, jer to svi moraju slušat
        String link = dajIzvjestaj(value, this.dajGodinuUpisa());
        Map<String, Integer> mapaPredmetaPrve = this.predmeti.dajPredmetePrveGodine(); //predmeti na prvoj godini
        if(this.dajGodinuUpisa()==2015) mapaPredmetaPrve.remove("Inženjerska matematika 2 - stari");
        if(this.dajGodinuUpisa()==2014) {
            mapaPredmetaPrve.remove("Inženjerska matematika 1 - stari");
            mapaPredmetaPrve.remove("Osnove elektrotehnike - stari");
        }

        ArrayList<String> indexi = dajIndexeNaPredmetu(link, this.dajGodinuUpisa()); //strapa text preko JSoupa i vrati arraylistu indexa
        assert indexi != null;
        Stream<String> paralelniStream = indexi.parallelStream();

        paralelniStream.forEach( ind -> {
            this.mojaGeneracijaSve.putIfAbsent(ind.replaceAll("[^\\d]",""), new Student(imenaMapa.get(Integer.parseInt(ind.replaceAll("[^\\d]",""))), ind.replaceAll("[^\\d]","")));
        });

        for(Map.Entry<String, Student> it : this.mojaGeneracijaSve.entrySet()){
            if(it.getValue().getImePrezime()==null) it.getValue().setImePrezime("Nepoznata osoba");
        }

    }

    public void formirajBachelor() {
        Map<String, Integer> mapaGodinaPredmeta = this.predmeti.dajPredmetePrveGodine();
        AtomicInteger l = new AtomicInteger(0); //brojač godina

        do {
            if(l.get()==1){
                mapaGodinaPredmeta=this.predmeti.dajPredmeteDrugeGodine();
            }
            if(l.get()==2){
                mapaGodinaPredmeta=this.predmeti.dajPredmeteTreceGodine();
            }
            mapaGodinaPredmeta.entrySet()
                    .parallelStream()
                    .forEach(it -> {
                        if (!daLiPostojiIzvjestaj(dajIzvjestaj(it.getValue(), this.dajGodinuUpisa() + l.get()))) return;
                        if(it.getKey().equals("Završni rad") && this.dajGodinuUpisa()+l.get()>=2017) return; //od 2017 se na završnom može dobiti ocjena 12 što se ne uračunava u prosjek :D
                        ArrayList<String> indeksi = dajIndexeNaPredmetu(dajIzvjestaj(it.getValue(), this.dajGodinuUpisa() + l.get()), 2015);
                        ArrayList<Integer> ocjene = dajOcjeneNaPredmetu(dajIzvjestaj(it.getValue(), this.dajGodinuUpisa() + l.get()), this.dajGodinuUpisa());
                        if (indeksi == null || ocjene == null || indeksi.size() != ocjene.size()) {
                            indeksi = dajIndexeNaPredmetu(dajIzvjestaj(it.getValue(), this.dajGodinuUpisa() + l.get()), 2017);
                            if (indeksi == null || ocjene == null || indeksi.size() != ocjene.size())
                                throw new IllegalArgumentException("Greška u strappanju");
                        }
                        popuniStudenteOcjenama(spojiIndekseIOcjeneUArrayList(indeksi, ocjene), it); //saljem sa trenutnog izvjestaja indexe, ocjene, i predmet ciji smo izvjestaj otvorili(odnosno it je ime predmeta + id predmeta na zamgeru)
                    });
            if(l.get()==0) {
                izbaciPonovce(this.mojaGeneracijaSve);
            }
            l.getAndIncrement();

        } while (l.get() != 3);


        obsListaStudenata.addAll(getMojaGeneracijaSve().values());
        obsListaStudenata.sort(Student::compareTo);
    }

    public ArrayList<Pair<String, Integer>> spojiIndekseIOcjeneUArrayList(ArrayList<String> indeksi, ArrayList<Integer> ocjene) {

        ArrayList<Pair<String, Integer>> lista = new ArrayList<>();
        for(int i = 0; i< indeksi.size(); i++)
        {
            lista.add(new Pair<String,Integer>(indeksi.get(i), ocjene.get(i)));
        }
        return lista;
    }

    public Map<String, Integer> getMapaRezultata() {
        return mapaRezultata;
    }

    private void popuniStudenteOcjenama(ArrayList<Pair<String,Integer>> lista, Map.Entry<String, Integer> it) { //it je ime predmeta i id predmeta
        Stream<Pair<String, Integer>> paralelniStream = lista.parallelStream();
        paralelniStream.forEach(p -> {
            ubaciUStudenta(p, it);
        });
    }

    private void ubaciUStudenta(Pair<String, Integer> p, Map.Entry<String, Integer> it) {
        if(mojaGeneracijaSve.get(p.getKey())==null) return;
        mojaGeneracijaSve.get(p.getKey()).getMapaPredmeta().put(it.getKey().replaceAll("\\s-\\snovi","").replaceAll("\\s-\\sAiE","").replaceAll("\\s-\\sTK","").replaceAll("\\s-\\sRI","").replaceAll("\\s-\\sEE","").replaceAll("\\s-\\sstari","").replaceAll("\\sv1","").replaceAll("\\sv2","").replaceAll("\\sv3","").replaceAll("\\sv4",""), p.getValue());
    }


    private static void izbaciPonovce(Map<String, Student> temp) {
        temp.entrySet().removeIf(e-> (e.getValue().getMapaPredmeta().size()<10));
    }



    public void prikaziRezultateMojeGeneracije(){

        for(Map.Entry<String, Student> it : this.mojaGeneracijaSve.entrySet())
        {
            System.out.println(it.getValue());
        }
    }


    public ObservableList<Student> getObsListaStudenata() {
        return obsListaStudenata;
    }

    public static ArrayList<String> dajIndexeNaPredmetu(String url, int i){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            ArrayList<String> indexi = new ArrayList<>();
            if(i<2016){
                for(Element red : doc.select("table")){
                    String index = red.select("td:nth-of-type(2)").text().replaceAll("[^\\d/ ]", "").replaceAll("\\s{2,}", " ").trim();
                    if(index.equals("")) continue;
                    String[] temp=index.split(" ");
                    for(String s : temp){
                        if(s.length()<5 && s.length()>2) continue;
                        if(s.contains("/")){
                            s=s.substring(s.lastIndexOf("/")+1,s.length());
                        }
                        indexi.add(s);
                    }
                }
                return indexi;
            }
            for(Element red : doc.select("table")){
                String index = red.select("td:nth-of-type(2)").text();
                String[] temp=index.substring(index.lastIndexOf(".")+2,index.length()).split(" ");
                indexi.addAll(Arrays.asList(temp));
            }
            return indexi;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Integer> dajOcjeneNaPredmetu(String url, int i){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            ArrayList<Integer> ocjene=new ArrayList<>();
            for (Element red : doc.select("td")) {
                if(red.select("td:last-of-type").text().length()>2 || red.select("td:last-of-type").text().equals("")) continue;
                String ocj = red.select("td:last-of-type").text().replaceAll("[^\\d/ ]", "").replaceAll("\\s{2,}", " ").trim();
                if(ocj.equals("/")) ocj="5";
                ocjene.add(Integer.parseInt(ocj));
            }
            return ocjene;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String dajIzvjestaj(Integer value, int godina) {
        godina=godina-2004;
        return "https://zamger.etf.unsa.ba/index.php?sta=izvjestaj/predmet&predmet="+value+"&ag="+godina+"&skrati=da";
    }

    private static boolean daLiPostojiIzvjestaj(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            String s = doc.select("b").text();
            if(s.equals("GREŠKA: Izvještaj ne postoji")) return false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
