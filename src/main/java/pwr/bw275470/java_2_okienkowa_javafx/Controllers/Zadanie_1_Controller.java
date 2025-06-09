package pwr.bw275470.java_2_okienkowa_javafx.Controllers;

import static pwr.bw275470.java_2_okienkowa_javafx.utils.ToastUtils.showToast;
import static pwr.bw275470.java_2_okienkowa_javafx.utils.ToastUtils.showAlertToast;
import pwr.bw275470.java_2_okienkowa_javafx.utils.FileUtils;
import pwr.bw275470.java_2_okienkowa_javafx.utils.FileUtilsThreads;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;

public class Zadanie_1_Controller {
    private static final Logger logger = Logger.getLogger(HelloController.class.getName());
    public Button Wczytaj;
    public Button Wykonaj;
    public Button Zapisz;
    public Button Original;
    public Button Edytowany;
    private Image daneGraficzneObrazu;
    private Image zmodyfikowanyObraz = null;
    private boolean modyfikowanyObraz;
    private String windowName = "";

    @FXML private ImageView logoImageView;
    @FXML private ComboBox<String> operationComboBox;
    @FXML private Label messageOut;

    @FXML
    public void initialize() {
        FileUtils.logSaver("============== NOWA INSTACJA PROGRAMU ==============");
        Wczytaj.setDisable(false);
        Wykonaj.setDisable(true);
        Zapisz.setDisable(true);
        Original.setDisable(true);
        Edytowany.setDisable(true);
        operationComboBox.setDisable(true);

        Image image = new Image(Objects.requireNonNull(getClass().getResource("/images/logoPWR.png")).toExternalForm());
        logoImageView.setImage(image);


        operationComboBox.setValue(null);
        operationComboBox.getItems().addAll(
                "Skalowanie obrazu", // Opcje listy
                "Obrót obrazu",
                "Negatyw obrazu",
                "Progowanie obrazu",
                "Konturowanie obrazu"
        );
        /*operationComboBox.setOnAction(event -> {
            String selected = operationComboBox.getValue();
            if (selected != null) {
                switch (selected) {
                    case "Historia użytkownika 1":
                        onOpenNewWindowClick("Zadanie_1-view.fxml");
                        break;
                    case "Historia użytkownika 2":
                        Pair<FXMLLoader, Stage> result = onOpenNewModalWindowClick("Zadanie_1-view.fxml");
                        if(result == null) return;
                        result.getValue().showAndWait();
                        break;
                    case "Historia użytkownika 3":
                        ChangeToNewWindowClick("Zadanie_1-view.fxml");
                        break;
                    default:
                        System.out.println("Nieznana operacja");
                }
            }
        });*/

    }


    @FXML
    protected void onExecuteOperationClick() {
        String selected = operationComboBox.getValue();
        if (selected == null) {
            System.out.println("Nie wybrano operacji");
            onWykonajButtonClick("null");
            showToast(operationComboBox, "ERROR: Nie wybrano operacji do wykonania");
            return;
        }

        if(modyfikowanyObraz == false) zmodyfikowanyObraz = daneGraficzneObrazu;
        Pair<FXMLLoader, Stage> result;
        switch (selected) {
            case "Skalowanie obrazu":
                windowName = "Skalowanie obrazu";
                result = onOpenNewModalWindowClick("resize-view.fxml");
                onWykonajButtonClick("Skalowanie obrazu");
                if(result == null) return;
                ResizeView resController = result.getKey().getController();
                resController.setOriginalSize((int)daneGraficzneObrazu.getWidth(), (int)daneGraficzneObrazu.getHeight());
                resController.setResizeListener((newWidth, newHeight) -> {
                    zmodyfikowanyObraz = FileUtils.resizeImage(zmodyfikowanyObraz, newWidth, newHeight);
                });
                result.getValue().showAndWait();
                modyfikowanyObraz = true; Edytowany.setDisable(false);
                break;

            case "Obrót obrazu":
                windowName = "Obrót obrazu";
                result = onOpenNewModalWindowClick("rotate-view.fxml");
                onWykonajButtonClick("Obrót obrazu");
                if(result == null) return;
                RotateView rotController = result.getKey().getController();
                rotController.setImagePreview(zmodyfikowanyObraz);
                rotController.setRotateListener(angle -> {
                    zmodyfikowanyObraz = FileUtils.rotateImage(zmodyfikowanyObraz, angle);
                });
                result.getValue().showAndWait();
                modyfikowanyObraz = true; Edytowany.setDisable(false);
                break;

            case "Negatyw obrazu":
                onWykonajButtonClick("Negatyw obrazu");
//                try { zmodyfikowanyObraz = FileUtils.generateNegativeImage(zmodyfikowanyObraz);
                try { zmodyfikowanyObraz = FileUtilsThreads.generateNegativeImageThreads(zmodyfikowanyObraz);
                    showToast(Wykonaj,"Wykonano negatyw obrazu.");
                    modyfikowanyObraz = true; Edytowany.setDisable(false);
                } catch (Exception e) { showAlertToast("Nie udało się wykonać negatywu:\n"+e); }
                break;

            case "Progowanie obrazu":
                windowName = "Progowanie obrazy";
                result = onOpenNewModalWindowClick("threshold-view.fxml");
                onWykonajButtonClick("Progowanie obrazu");
                if(result == null) return;
                ThresholdView thresholdViewController = result.getKey().getController();
                thresholdViewController.setImagePreview(zmodyfikowanyObraz);
                thresholdViewController.setThresholdListener(thresholdValue -> {
//                    try{zmodyfikowanyObraz = FileUtils.thresholdImage(zmodyfikowanyObraz, thresholdValue);
                    try{zmodyfikowanyObraz = FileUtilsThreads.thresholdImageThreas(zmodyfikowanyObraz, thresholdValue);
                        showToast(Wykonaj, "Progowanie zostało wykonane!");
                    } catch (Exception e) {showAlertToast("Nie udało się wykonać progowania.");}
                });
                result.getValue().showAndWait();
                modyfikowanyObraz = true; Edytowany.setDisable(false);
                break;

            case "Konturowanie obrazu":
                onWykonajButtonClick("Konturowanie obrazu");
                try{zmodyfikowanyObraz = FileUtilsThreads.contourImageThreads(zmodyfikowanyObraz);
//                try{zmodyfikowanyObraz = FileUtils.contourImage(zmodyfikowanyObraz);
                    showToast(Wykonaj, "Konturowanie zostało przeprowadzone pomyślnie!");
                    modyfikowanyObraz = true; Edytowany.setDisable(false);
                } catch (Exception e) { showAlertToast("Nie udało się przeprowadzić konturowania:\n"+e); }
                break;

            default:
                System.out.println("Nieznana operacja");
                onWykonajButtonClick("Nieznana operacja");
        }
    }

    @FXML
    protected void onWykonajButtonClick(String msg) {
        FileUtils.logSaver("  msg: " + msg);
        messageOut.setText(msg);
    }


    @FXML
    protected void onChooseImageClick() {
        FileChooser sysOknoWyboruPlikow = new FileChooser();
        sysOknoWyboruPlikow.setTitle("[.NET][CZ 15:15][LAB 6][ZAD 1] Wybierz plik (obraz) \"*.JPG\"");

        String userDirectoryString = System.getProperty("user.home");
        File initialDirectory = new File(userDirectoryString, "Pictures");

        // Sprawdź, czy ten katalog istnieje, jeśli nie, użyj katalogu domowego
        if (initialDirectory.exists() && initialDirectory.isDirectory()) sysOknoWyboruPlikow.setInitialDirectory(initialDirectory);
        else sysOknoWyboruPlikow.setInitialDirectory(new File(userDirectoryString));


        FileChooser.ExtensionFilter filtrRozszerzen = new FileChooser.ExtensionFilter("Pliki JPG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg");
        sysOknoWyboruPlikow.getExtensionFilters().add(filtrRozszerzen);

        // Otwórz okno "sysOknoWyboruPlikow" wyboru pliku dla okna "logoImageView".
        File wskWskazanyPlik = sysOknoWyboruPlikow.showOpenDialog(getStage());

        ImageFileLoad(wskWskazanyPlik);
        FileUtils.logSaver("Wczytuje plik: "+wskWskazanyPlik.getAbsolutePath());
    }

    // Pomocnicza metoda do pobrania Stage z dowolnego komponentu (np. ImageView, ComboBox itd.)
    private Stage getStage() {
        return (Stage) logoImageView.getScene().getWindow();
    }

    private void ImageFileLoad(File wskWskazanyPlik) {
        if (wskWskazanyPlik != null) {
            System.out.println("Wybrano plik: " + wskWskazanyPlik.getAbsolutePath());
            onWykonajButtonClick("Wybrano plik: " + wskWskazanyPlik.getAbsolutePath());

            String nazwaPlikuObrazu = wskWskazanyPlik.getName().toLowerCase();
            if (!nazwaPlikuObrazu.endsWith(".jpg")) { showToast(Zapisz,"ERROR: Niedozwolony format pliku"); return; }
            else try {
                Image probaZapisuDanych = new Image(wskWskazanyPlik.toURI().toString());
                if (probaZapisuDanych.isError()) {
                    showToast(Zapisz,"ERROR: Nie udało się załadować pliku");
                    return;
                }
                daneGraficzneObrazu = probaZapisuDanych;
                modyfikowanyObraz = false;
                Edytowany.setDisable(true);

//                windowName = "Originally obraz";
//                FXMLLoader fxmlLoader = onOpenNewWindowClick("obraz-view.fxml");
//                ObrazController controller = fxmlLoader.getController();
//                controller.setImage(daneGraficzneObrazu);

                showToast(Zapisz,"Pomyślnie załadowano plik: "+ wskWskazanyPlik.getAbsolutePath());
                Wczytaj.setDisable(false);  Wykonaj.setDisable(false);  Zapisz.setDisable(false);   Original.setDisable(false); operationComboBox.setDisable(false);
            } catch (Exception e) {
                showToast(Zapisz,"ERROR: Nie udało się załadować pliku, wystąpił wyjątek!");
                System.err.println("Błąd przy ładowaniu obrazu: " + e.getMessage());
            }
        } else {
            System.out.println("Nie wybrano pliku.");
            onWykonajButtonClick("Nie wybrano pliku.");
            showToast(Zapisz,"ERROR: Nie wybrano pliku.");
        }
    }

    @FXML
    private void ImageSave() {
        FileUtils.logSaver("Próba zapisania obrazu");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("save-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 960, 540);

            SaveView controller = fxmlLoader.getController();
            StringBuilder toastMsg = new StringBuilder("Błąd!");
            if(!modyfikowanyObraz) zmodyfikowanyObraz = daneGraficzneObrazu;
            controller.saveImage(zmodyfikowanyObraz, toastMsg, modyfikowanyObraz); // ← tu przekazujesz obraz

            Stage toOkno = new Stage();
            toOkno.setTitle("Zapisz obraz");
            toOkno.setScene(scene);
            toOkno.initModality(Modality.APPLICATION_MODAL); // okno modalne
            toOkno.showAndWait(); // czekaj na zamknięcie
            showToast(Zapisz, toastMsg.toString());

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Błąd podczas ładowania save-view.fxml", e);
        }
    }













    /************************************* OTWIERANIE OKIEN ****************************************/

    @FXML protected void OpenNewSecond() {
        windowName = "Nowe okno";
        onOpenNewWindowClick("/pwr/bw275470/java_2_okienkowa_javafx/Zadanie_1-view.fxml");
    }

    @FXML protected void OpenOriginal() {
        windowName = "Originally obraz";
        FXMLLoader fxmlLoader = onOpenNewWindowClick("obraz-view.fxml");
        ObrazController controller = fxmlLoader.getController();
        controller.setImage(daneGraficzneObrazu);
    }

    @FXML protected void OpenEdytowany() {
        windowName = "Edytowany obraz";
        FXMLLoader fxmlLoader = onOpenNewWindowClick("obraz-view.fxml");
        ObrazController controller = fxmlLoader.getController();
        controller.setImage(zmodyfikowanyObraz);
    }

    protected FXMLLoader onOpenNewWindowClick(String NameSource_fxml) {
        FXMLLoader fxmlLoader = null;
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource(NameSource_fxml));
            Scene scene = new Scene(fxmlLoader.load(), 960, 540);
            Stage ToOkno = new Stage();
            ToOkno.setTitle("[.NET][CZ 15:15][LAB 6] "+windowName);
            ToOkno.setScene(scene);
            ToOkno.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Błąd podczas ładowania obraz-view.fxml", e);
            return null;
        }
        return fxmlLoader;
    }


    @FXML protected void OpenModalSecond() {
        windowName = "Nowe Modalne Okno";
        Pair<FXMLLoader, Stage> result;
        result = onOpenNewModalWindowClick("/pwr/bw275470/java_2_okienkowa_javafx/Zadanie_1-view.fxml");
        if(result == null) return;
        result.getValue().showAndWait();
    }
    protected Pair<FXMLLoader, Stage> onOpenNewModalWindowClick(String NameSource_fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(NameSource_fxml));
            Parent root = fxmlLoader.load();
            Scene scena = new Scene(root, 960, 540);
            Stage ToOkno = new Stage();

            ToOkno.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Ustaw tryb modalny (blokuje inne okna aplikacji)
            ToOkno.setTitle("[.NET][CZ 15:15][LAB 6] "+windowName);
            ToOkno.setScene(scena);
            /****** showAndWait() musi być wywołane po użyciu onOpenNewModalWindowClick ******/
//            ToOkno.showAndWait(); // Blokuje aż nowe okno zostanie zamknięte
            return new Pair<>(fxmlLoader, ToOkno);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Błąd podczas ładowania obraz-view.fxml", e);
            return null;
        }
    }


    @FXML protected void ChangeNewSecond() {
        windowName = "Nowe Okno zastępujące  stare";
        ChangeToNewWindowClick("/pwr/bw275470/java_2_okienkowa_javafx/Zadanie_1-view.fxml");
    }
    protected FXMLLoader ChangeToNewWindowClick(String NameSource_fxml) {
        FXMLLoader fxmlLoader = null;
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource(NameSource_fxml));
            Scene scene = new Scene(fxmlLoader.load(), 960, 540);

            Stage ToOkno = (Stage) logoImageView.getScene().getWindow(); // Pobierz obecny Stage (główne okno) na podstawie zdjęcia logo PWR
            ToOkno.setTitle("[.NET][CZ 15:15][LAB 6] "+windowName);
            ToOkno.setScene(scene);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Błąd podczas ładowania obraz-view.fxml", e);
            return null;
        }
        return fxmlLoader;
    }
}