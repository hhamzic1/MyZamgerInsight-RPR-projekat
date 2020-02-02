package ba.etf.unsa.rpr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Locale.setDefault(new Locale("bs"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginScreen.fxml"), ResourceBundle.getBundle("Translation"));
        loader.setController(new Controller());
        Parent root = loader.load();
        primaryStage.setTitle("MyZamsight");
        Scene novaScena = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        primaryStage.setScene(novaScena);
        primaryStage.show();
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);

    }
}