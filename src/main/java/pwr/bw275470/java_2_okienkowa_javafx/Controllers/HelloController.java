package pwr.bw275470.java_2_okienkowa_javafx.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;

public class HelloController {
    private static final Logger logger = Logger.getLogger(HelloController.class.getName());

    @FXML
    private Label welcomeText;

    @FXML
    public Label secondLabel;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Laboratorium 6\n" +
                "Wielowątkowość i GUI w Java!");
    }

    @FXML
    protected void onOpenNewWindowClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("obraz-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 200);
            Stage ToOkno = new Stage();
            ToOkno.setTitle("Drugie okno");
            ToOkno.setScene(scene);
            ToOkno.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Błąd podczas ładowania obraz-view.fxml", e);
        }
    }

    @FXML
    protected void onOpenNewModalWindowClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("obraz-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 200);
            Stage ToOkno = new Stage();
            ToOkno.setTitle("Drugie okno");
            ToOkno.setScene(scene);

            ToOkno.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Ustaw tryb modalny (blokuje inne okna aplikacji)
            ToOkno.showAndWait(); // Blokuje aż nowe okno zostanie zamknięte
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Błąd podczas ładowania obraz-view.fxml", e);
        }
    }

    @FXML
    protected void ChangeToNewWindowClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("obraz-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 200);

            Stage ToOkno = (Stage) welcomeText.getScene().getWindow(); // Pobierz obecny Stage (główne okno) na podstawie kontrolki
            ToOkno.setTitle("Drugie okno");
            ToOkno.setScene(scene);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Błąd podczas ładowania obraz-view.fxml", e);
        }
    }
}