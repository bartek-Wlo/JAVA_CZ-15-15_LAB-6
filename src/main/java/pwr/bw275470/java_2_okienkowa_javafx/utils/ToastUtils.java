package pwr.bw275470.java_2_okienkowa_javafx.utils;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ToastUtils {
    private ToastUtils() { } // private == nie da się tego stworzyć

    /** Wyświetla powiadomienie typu "Toast" na środku okna.
     *
     * @param ownerNode Dowolny element (np. przycisk, etykieta) z okna, w którym ma się pojawić powiadomienie.
     * @param message   Wiadomość do wyświetlenia.
     */
    public static void showToast(Node ownerNode, String message) {
        if (ownerNode == null || ownerNode.getScene() == null || ownerNode.getScene().getWindow() == null) {
            System.err.println("Error: Toast can't be shown: owner node is not attached to a window.");
            return;
        }

        Popup ToastOkno = new Popup();
        ToastOkno.setAutoHide(true); // Automatycznie znika po kliknięciu gdzie indziej

        Label label = getLabel(message);
        ToastOkno.getContent().add(label);

        Stage oknoRodzic = (Stage) ownerNode.getScene().getWindow(); // Wykrywa, o które okno rodzic chodzi
        ToastOkno.show(oknoRodzic); // W odniesieniu do rodzicielskiego okna będzie wyświetlony Toast.

        // Wyznaczamy współrzędne (idealnie na środku) po show by znać wymiary "ToastOkno".
        ToastOkno.setX(oknoRodzic.getX() + oknoRodzic.getWidth() / 2 - ToastOkno.getWidth() / 2);
        ToastOkno.setY(oknoRodzic.getY() + oknoRodzic.getHeight() / 2 - ToastOkno.getHeight() / 2);

        PauseTransition delay = new PauseTransition(Duration.seconds(3)); // Zamknij po 3 [s].
        delay.setOnFinished(e -> ToastOkno.hide());
        delay.play();
    }

    /** Wyświetla powiadomienie typu "Toast" na środku okna.
     *
     * @param message Wiadomość wpisywana do Label, reprezentujący Toast.
     */
    private static Label getLabel(String message) {
        Label label = new Label(message);
        label.setStyle(
                "-fx-background-color: #323232; " +     // tło
                    "-fx-background-radius: 50px;" +    // zaokrąglenia rogów
                        "-fx-text-fill: white; " +      // kolor tekstu
                        "-fx-font-size: 20px; " +       // wielkość czcionki
                        "-fx-padding: 20px 40px; " +    // zmniejszyłem padding horyzontalny, dostosuj wg potrzeb
                        "-fx-border-radius: 50px; " +   // Zaokrąglenie ramki
                        "-fx-border-color: black; " +   // Kolor ramki
                        "-fx-border-width: 2px; "       // grubość ramki
        );
        return label;
    }

    public static void showAlertToast(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("[.NET][CZ 15:15][LAB 6] Alert!");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

}