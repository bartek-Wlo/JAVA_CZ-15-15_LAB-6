package pwr.bw275470.java_2_okienkowa_javafx.Controllers;

import static pwr.bw275470.java_2_okienkowa_javafx.utils.ToastUtils.showToast;
import static pwr.bw275470.java_2_okienkowa_javafx.utils.FileUtils.isFileNameValid;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SaveView {

    @FXML private TextField saveFileName; // Nazwa zapisywanego pliku
    @FXML private Label fileNameErrorLabel; // Za mało liter < 3
    @FXML private Label alertLabel; // Nie zmodyfikowany obraz

    private StringBuilder message;
    private Image daneGraficzneObrazu;
    private boolean obrazZmodyfikowany;
    private static final Logger logger = Logger.getLogger(SaveView.class.getName());


    public void saveImage(Image daneObrazu, StringBuilder msg, boolean stanObrazu) {
        this.daneGraficzneObrazu = daneObrazu;
        this.message = msg;
        this.obrazZmodyfikowany = stanObrazu;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (!obrazZmodyfikowany) { alertLabel.setVisible(true); }
        });
        saveFileName.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 100) {
                saveFileName.setText(oldText);
            }
            if (newText.length() > 3) fileNameErrorLabel.setVisible(false);
        });
    }

    @FXML
    protected void onCancelClick() {
        this.message.setLength(0); this.message.append("Anulowano zapisywanie");
        closeWindow();
    }


    @FXML
    protected void onSaveClick() {
        fileNameErrorLabel.setVisible(false);
        alertLabel.setVisible(false);

        if (daneGraficzneObrazu == null) {
            this.message.setLength(0);      this.message.append("ERROR: brak obrazu");
            closeWindow();                  return;
        }
        if(daneGraficzneObrazu.isError()) {
            this.message.setLength(0);      this.message.append("ERROR: obraz jest uszkodzony!");
            closeWindow();                  return;
        }

        String fileName = saveFileName.getText().trim();
        if (fileName.length() < 3) {
            fileNameErrorLabel.setVisible(true);
            return;
        }
        if (!isFileNameValid(fileName)) {
            showToast(saveFileName, "Nazwa pliku zawiera niedozwolone znaki (np. / \\ : * ? \" < > |)");
            return; // Przerwij działanie metody
        }

        if (!fileName.toLowerCase().endsWith(".jpg")) {
            fileName += ".jpg";
        }

        File picturesDir = new File(System.getProperty("user.home"), "Pictures");
        String fullPath = Paths.get(picturesDir.getAbsolutePath(), fileName).toString();
        this.saveImageToFile(daneGraficzneObrazu, fullPath);
    }

    private void saveImageToFile(Image daneObrazu, String filePath) {
        int width = (int) daneObrazu.getWidth();
        int height = (int) daneObrazu.getHeight();
        PixelReader pixelReader = daneObrazu.getPixelReader();
        if (pixelReader == null) return;

        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                int rgb =
                        ((int)(color.getRed() * 255) << 16) |
                                ((int)(color.getGreen() * 255) << 8) |
                                ((int)(color.getBlue() * 255));
                bImage.setRGB(x, y, rgb);
            }
        }
        try {
            ImageIO.write(bImage, "jpg", new File(filePath));
            System.out.println("Obraz zapisany do: " + filePath);
            this.message.setLength(0);
            this.message.append("Obraz zapisany do: ").append(filePath);
            showToast(saveFileName,"Obraz zapisany do: " + filePath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Błąd podczas zapisywania \""+filePath+"\": ", e);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) alertLabel.getScene().getWindow();
        stage.close();
    }
}
