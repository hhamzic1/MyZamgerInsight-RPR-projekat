package ba.etf.unsa.rpr;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;


import javax.swing.*;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class PrintReport extends JFrame {
    public void showReport(Connection conn) throws JRException {
        /*String reportSrcFile = getClass().getResource("/fxml/Izvjestaj.jrxml").getFile();
        String reportsDir = getClass().getResource("/fxml/").getFile();*/

        InputStream file = getClass().getResourceAsStream("/fxml/Izvjestaj.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(file);


        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        // Fields for resources path
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        //parameters.put("reportsDirPath", reportsDir);
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        list.add(parameters);
        JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);
        JRViewer viewer = new JRViewer(print);
        viewer.setOpaque(true);
        viewer.setVisible(true);
        this.add(viewer);
        this.setSize(1000, 500);
        this.setVisible(true);
    }
}
