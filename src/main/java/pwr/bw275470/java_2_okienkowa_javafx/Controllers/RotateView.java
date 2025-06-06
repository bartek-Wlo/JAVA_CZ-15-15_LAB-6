package pwr.bw275470.java_2_okienkowa_javafx.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Consumer;

public class RotateView {

    @FXML private Button rotateLeftButton;
    @FXML private Button rotateRightButton;
    @FXML private Button cancelButton;
    @FXML private Button applyButton;

    @FXML private ImageView rightImage;
    @FXML private ImageView leftImage;

    @FXML private ImageView previewImage;

    private Image currentImage;
    private double rotationAngle = 0;

    private Consumer<Double> rotateListener;

    public void setRotateListener(Consumer<Double> listener) {
        this.rotateListener = listener;
    }

    public void setImagePreview(Image image) {
        this.currentImage = image;
        previewImage.setImage(currentImage);
        previewImage.setRotate(rotationAngle);
    }

    @FXML
    public void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/images/rotate_right.png")).toExternalForm());
        rightImage.setImage(image);
        image = new Image(Objects.requireNonNull(getClass().getResource("/images/rotate_left.png")).toExternalForm());
        leftImage.setImage(image);
    }

    @FXML
    public void onRotateLeft() {
        rotationAngle -= 90;
        updateRotationPreview();
    }

    @FXML
    public void onRotateRight() {
        rotationAngle += 90;
        updateRotationPreview();
    }

    @FXML
    public void onApply() {
        if (rotateListener != null) {
            rotateListener.accept(rotationAngle); // Przekazanie kąta
        }
        closeWindow();
    }

    @FXML
    public void onCancel() {
        closeWindow();
    }

    private void updateRotationPreview() {
        previewImage.setRotate(rotationAngle);
    }

    private void closeWindow() {
        Stage stage = (Stage) rotateLeftButton.getScene().getWindow();
        stage.close();
    }
}
