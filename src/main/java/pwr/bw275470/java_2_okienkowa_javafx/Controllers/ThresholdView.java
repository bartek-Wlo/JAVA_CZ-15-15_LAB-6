package pwr.bw275470.java_2_okienkowa_javafx.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.function.IntConsumer;

public class ThresholdView {

    @FXML private TextField thresholdField;
    @FXML private ImageView previewImage;
    @FXML private Label errorLabel;
    @FXML private Button cancelButton;

    private IntConsumer thresholdListener;

    public void setThresholdListener(IntConsumer listener) {
        this.thresholdListener = listener;
    }

    public void setImagePreview(Image image) {
        previewImage.setImage(image);
    }

    @FXML
    public void onExecute() {
        try {
            int value = Integer.parseInt(thresholdField.getText());
            if (value < 0 || value > 255) {
                errorLabel.setText("Wartość musi być w zakresie 0–255");
                return;
            }
            if (thresholdListener != null) thresholdListener.accept(value);
            closeWindow();
        } catch (NumberFormatException e) {
            errorLabel.setText("Wprowadź poprawną liczbę całkowitą.");
        }
    }

    @FXML
    public void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
