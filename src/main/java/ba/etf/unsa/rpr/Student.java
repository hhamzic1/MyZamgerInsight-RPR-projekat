package ba.etf.unsa.rpr;

import java.util.HashMap;
import java.util.Map;

public class Student implements Comparable<Student> {
    private final String imePrezime;
    private final String brojIndeksa;
    private Map<String, Integer> mapaPredmeta;
    private Map<String,Integer> polozeni;
    private Map<String,Integer> nepolozeni;
    private double prosjekBachelor;

    public double getProsjekBachelor() {
        return prosjekBachelor;
    }

    public Student(String ime, String index){
        this.imePrezime=ime;
        this.brojIndeksa=index;
        this.mapaPredmeta=new HashMap<>();
        this.polozeni=new HashMap<>();
        this.nepolozeni=new HashMap<>();
    }

    public String getImePrezime() {
        return imePrezime;
    }

    public String getBrojIndeksa() {
        return brojIndeksa;
    }

    public Map<String, Integer> getMapaPredmeta() {
        return mapaPredmeta;
    }
    public void setMapaPredmeta(Map<String, Integer> mapaPredmeta) {
        this.mapaPredmeta = mapaPredmeta;
    }

    public void setProsjekBachelor()
    {
        int suma = 0;
        int brPredmeta=0;
        for(Map.Entry<String, Integer> it : this.getMapaPredmeta().entrySet())
        {
            if(it.getValue()>=6){
                polozeni.put(it.getKey(), it.getValue());
                suma+=it.getValue();
                brPredmeta++;
                continue;
            }
            nepolozeni.put(it.getKey(), it.getValue());
        }
        if(suma==0) {
            this.prosjekBachelor=5.;
            return;
        }
        double prosjek = (double) suma / brPredmeta;
        double temp = Math.round(prosjek * 100);
        this.prosjekBachelor = temp / 100;
    }


    public Map<String, Integer> getPolozeni() {
        return polozeni;
    }

    public Map<String, Integer> getNepolozeni() {
        return nepolozeni;
    }

    @Override
    public String toString() {
        this.setProsjekBachelor();
        StringBuilder s = new StringBuilder();
        s.append(this.getImePrezime()).append(" (prosjek: "+this.getProsjekBachelor()+")");
        for(Map.Entry<String, Integer> it : this.getNepolozeni().entrySet())
        {
            s.append("\n  -"+it.getKey()+" - nije položen");
        }
        return String.valueOf(s);
    }

    @Override
    public int compareTo(Student o2) {
        if(this.getNepolozeni().size()==0 && o2.getNepolozeni().size()==0)
        {
            if(this.getProsjekBachelor() > o2.getProsjekBachelor()) return -1;
            else if(this.getProsjekBachelor() < o2.getProsjekBachelor()) return 1;
            else
            {
                return 0;
            }
        }
        else if(this.getPolozeni().size() > o2.getPolozeni().size()) return -1;
        else if(this.getPolozeni().size() < o2.getPolozeni().size())
        {
            return 1;
        }
        else
        {
            if(this.getProsjekBachelor() > o2.getProsjekBachelor()) return -1;
            else if(this.getProsjekBachelor() < o2.getProsjekBachelor()) return 1;
            else
            {
                return 0;
            }
        }
    }
}
