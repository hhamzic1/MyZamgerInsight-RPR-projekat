package ba.etf.unsa.rpr;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RezultatiDAO {

    private static RezultatiDAO instance;
    private Connection conn = null;
    private PreparedStatement ubaciStudentaUpit, ubaciOcjenuUpit, dajStudentaPoIndeksuUpit, dajPredmeteZaStudentaUpit, dajImenaEtfUpit;

    public static RezultatiDAO getInstance() {
        if (instance == null) {
            instance = new RezultatiDAO();
        }
        return instance;
    }

    public static void removeInstance() {
        if (instance == null) return;
        instance.close();
        instance = null;
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private RezultatiDAO() {
        File dbfile = new File("bazaprojekat.db");
        dbfile.delete();
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:bazaprojekat.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ubaciStudentaUpit=conn.prepareStatement("REPLACE INTO student VALUES(?,?)");//ime, broj indeksa
        } catch (SQLException e) {
            regenerisiBazu();
            try {
                ubaciStudentaUpit = conn.prepareStatement("REPLACE INTO student VALUES(?,?)");
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }

        try
        {
            ubaciOcjenuUpit=conn.prepareStatement("REPLACE INTO ocjena VALUES(?,?,?)"); //broj indeksa, naziv predmeta, ocjena iz predmeta
            dajStudentaPoIndeksuUpit=conn.prepareStatement("SELECT ime, broj_indeksa FROM student WHERE broj_indeksa=?");
            dajPredmeteZaStudentaUpit=conn.prepareStatement("SELECT o.naziv_predmeta, o.ocjena FROM ocjena o, student s WHERE s.broj_indeksa=o.student_id AND s.broj_indeksa=?");
            dajImenaEtfUpit=conn.prepareStatement("SELECT * from etfIndeksi");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void regenerisiBazu () {
        Scanner ulaz = null;
        ulaz = new Scanner(this.getClass().getResourceAsStream("/bazaprojekat.db.sql"), "UTF-8");
        String upitSql = "";
        while (ulaz.hasNext()) {
            upitSql += ulaz.nextLine();
            if (upitSql.charAt(upitSql.length() - 1) == ';') {
                try {
                    Statement stmt = conn.createStatement();
                    stmt.execute(upitSql);
                    upitSql = "";
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        ulaz.close();
    }

    public void ubaciStudenta(Map<String, Student> tempMapa)
    {

        try
        {
            StringBuilder studentUpit = new StringBuilder("INSERT INTO student VALUES\n");
            StringBuilder ocjenaUpit = new StringBuilder("INSERT INTO ocjena VALUES\n");

            for (Map.Entry<String, Student> it : tempMapa.entrySet()) {
                studentUpit.append("(\"").append(it.getValue().getImePrezime()).append("\",").append(it.getValue().getBrojIndeksa()).append("),\n");
                for(Map.Entry<String, Integer> iter : it.getValue().getMapaPredmeta().entrySet())
                {
                    ocjenaUpit.append("(").append(it.getValue().getBrojIndeksa()).append(",\"").append(iter.getKey()).append("\",").append(iter.getValue()).append("),\n");
                }

            }
            studentUpit = new StringBuilder(studentUpit.substring(0, studentUpit.length() - 2));
            studentUpit.append(";");
            ocjenaUpit = new StringBuilder(ocjenaUpit.substring(0, ocjenaUpit.length() - 2));
            ocjenaUpit.append(";");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(studentUpit.toString());
            Statement stmt2 = conn.createStatement();
            stmt2.executeUpdate(ocjenaUpit.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConn(){
        return this.conn;
    }

    public Map<Integer, String> dajImenaEtf() {
            Map<Integer, String> rezultat = new HashMap<>();
        try {
            ResultSet rs = dajImenaEtfUpit.executeQuery();
            while(rs.next())
            {
                rezultat.put(rs.getInt(1), rs.getString(2));
            }
            return rezultat;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("nije fino uzeto iz baze");
            return null;
        }

    }

    public void obrisiTrenutnePodatke() {

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("DELETE FROM student");
            stmt.close();
            stmt=conn.createStatement();
            stmt.execute("DELETE FROM ocjena");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

