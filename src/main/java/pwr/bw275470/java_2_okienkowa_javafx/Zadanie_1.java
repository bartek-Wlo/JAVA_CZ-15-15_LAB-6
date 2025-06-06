package pwr.bw275470.java_2_okienkowa_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Zadanie_1 extends Application {
    @Override
    public void start(Stage okienko) throws IOException {
        FXMLLoader interfejsGraficznyFXML = new FXMLLoader(HelloApplication.class.getResource("Zadanie_1-view.fxml"));
        Scene scena = new Scene(interfejsGraficznyFXML.load(), 960, 540);
        okienko.setTitle("[.NET][CZ 15:15][LAB 6][ZAD 1] \"Przetwarzanie obrazów 4\"");
        okienko.setScene(scena);
        okienko.show();
    }

    public static void main(String[] args) {
        launch();
    }
}