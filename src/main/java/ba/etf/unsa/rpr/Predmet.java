package ba.etf.unsa.rpr;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class Predmet {
    private SimpleStringProperty naziv = new SimpleStringProperty("");
    private SimpleIntegerProperty ocjena;

    public String getNaziv() {
        return naziv.get();
    }

    public SimpleStringProperty nazivProperty() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv.set(naziv);
    }

    public int getOcjena() {
        return ocjena.get();
    }

    public SimpleIntegerProperty ocjenaProperty() {
        return ocjena;
    }

    public void setOcjena(int ocjena) {
        this.ocjena.set(ocjena);
    }

    public Predmet(String naziv, Integer ocj){
        this.naziv.set(naziv);
        ocjena = new SimpleIntegerProperty(0);
        this.ocjena.set(ocj);
    }

    @Override
    public String toString() {
        return naziv.get()+" (ocjena: "+ocjena.get()+")";
    }
}
