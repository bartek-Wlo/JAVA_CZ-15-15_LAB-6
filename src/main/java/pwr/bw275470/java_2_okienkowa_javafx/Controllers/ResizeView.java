package pwr.bw275470.java_2_okienkowa_javafx.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Locale;

public class ResizeView {

    @FXML private TextField widthField;
    @FXML private TextField heightField;
    @FXML private Label errorLabel;
    @FXML private TextField scaleField;


    private int originalWidth;
    private int originalHeight;
    private ResizeListener resizeListener;

    public void initialize() {
        // Optional: add listener to accept only digits
        widthField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d{0,4}")) {
                widthField.setText(oldVal);
            }
        });
        heightField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d{0,4}")) {
                heightField.setText(oldVal);
            }
        });

        widthField.textProperty().addListener((obs, oldVal, newVal) -> suggestScaleIfTooLarge());
        heightField.textProperty().addListener((obs, oldVal, newVal) -> suggestScaleIfTooLarge());
    }

    /** Wywoływane przy tworzeniu okna **/
    public void setOriginalSize(int width, int height) {
        this.originalWidth = width;
        this.originalHeight = height;
    }
    /** Wywoływane przy tworzeniu okna **/
    public void setResizeListener(ResizeListener listener) {
        this.resizeListener = listener;
    }

    @FXML
    private void onResize() {
        String w = widthField.getText();
        String h = heightField.getText();

        if (w.isEmpty()) {
            errorLabel.setText("Pole szerokości jest wymagane");
            return;
        }

        if (h.isEmpty()) {
            errorLabel.setText("Pole wysokości jest wymagane");
            return;
        }

        int width = Integer.parseInt(w);
        int height = Integer.parseInt(h);

        if (width <= 0 || width > 3000 || height <= 0 || height > 3000) {
            errorLabel.setText("Wymiary muszą być w zakresie 1–3000");
            return;
        }

        if (resizeListener != null) {
            resizeListener.onResizeConfirmed(width, height);
        }

        closeDialog();
    }

    @FXML
    private void onRestoreOriginal() {
        widthField.setText(String.valueOf(originalWidth));
        heightField.setText(String.valueOf(originalHeight));
        errorLabel.setText("");
    }

    @FXML
    private void onCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) widthField.getScene().getWindow();
        stage.close();
    }

    public interface ResizeListener {
        void onResizeConfirmed(int width, int height);
    }


    @FXML
    public void onScaleApply() {
        errorLabel.setText("");

        try {
            double scale = Double.parseDouble(scaleField.getText());
            if (scale <= 0) {
                errorLabel.setText("Współczynnik musi być większy od 0");
                return;
            }

            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());

            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);

            widthField.setText(String.valueOf(scaledWidth));
            heightField.setText(String.valueOf(scaledHeight));

        } catch (NumberFormatException e) {
            errorLabel.setText("Niepoprawne wartości – wpisz liczby.");
        }
    }

    @FXML
    public void suggestScaleIfTooLarge() {
        try {
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());
            int max = Math.max(width, height);

            if (max > 3000) {
                double scale = 3000.0 / max;
                scaleField.setText(String.format(Locale.US, "%.6f", scale));
            } else {
                scaleField.clear();
            }
        } catch (NumberFormatException e) {
            scaleField.clear();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) errorLabel.getScene().getWindow();
        stage.close();
    }
}
