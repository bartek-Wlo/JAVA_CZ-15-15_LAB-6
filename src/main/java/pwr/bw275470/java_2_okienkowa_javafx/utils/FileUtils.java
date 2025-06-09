package pwr.bw275470.java_2_okienkowa_javafx.utils;

import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;

public class FileUtils {

    /**
     * Wzorzec wyrażenia regularnego, który dopasowuje niedozwolone znaki w nazwach plików
     * dla systemu Windows. Uwzględnia również znaki kontrolne i białe znaki (np. spację).
     */
    private static final Pattern ILLEGAL_CHARS_PATTERN = Pattern.compile("[\\\\/:*?\"<>|\\p{Cntrl}\\s]");

    private FileUtils() {
    }

    /**
     * Sprawdza, czy nazwa pliku jest prawidłowa.
     * Metoda sprawdza, czy nazwa nie zawiera niedozwolonych znaków,
     * takich jak: \ / : * ? " < > | oraz białych i kontrolnych znaków.
     *
     * @param fileName Nazwa pliku do sprawdzenia.
     * @return true, jeśli nazwa jest prawidłowa (nie zawiera niedozwolonych znaków), false w przeciwnym razie.
     */
    public static boolean isFileNameValid(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        return !ILLEGAL_CHARS_PATTERN.matcher(fileName).find();
    }


    public static Image resizeImage(Image source, int targetWidth, int targetHeight) {
        if (source == null || targetWidth <= 0 || targetHeight <= 0) {
            return source;
        }
        WritableImage resizedImage = new WritableImage(targetWidth, targetHeight);
        PixelWriter pw = resizedImage.getPixelWriter();

        Canvas canvas = new Canvas(targetWidth, targetHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(source, 0, 0, targetWidth, targetHeight);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT); // jeśli chcesz przezroczystość
        canvas.snapshot(params, resizedImage);

        return resizedImage;
    }


    public static Image rotateImage(Image source, double angleDegrees) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();

        boolean swap = Math.abs(angleDegrees) % 180 == 90;
        int newWidth = swap ? height : width;
        int newHeight = swap ? width : height;

        Canvas canvas = new Canvas(newWidth, newHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.translate(newWidth / 2.0, newHeight / 2.0);
        gc.rotate(angleDegrees);
        gc.drawImage(source, -width / 2.0, -height / 2.0);

        WritableImage rotated = new WritableImage(newWidth, newHeight);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        if (!Platform.isFxApplicationThread()) {
            final Image[] result = new Image[1];
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                result[0] = canvas.snapshot(params, rotated);
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result[0];
        } else {
            return canvas.snapshot(params, rotated);
        }
    }


    public static Image generateNegativeImage(Image source) {
        if (source == null) return null;

        int width = (int) source.getWidth();
        int height = (int) source.getHeight();

        WritableImage output = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                Color negative = new Color(
                        1.0 - color.getRed(),
                        1.0 - color.getGreen(),
                        1.0 - color.getBlue(),
                        color.getOpacity()
                );
                writer.setColor(x, y, negative);
            }
        }

        return output;
    }


    public static Image thresholdImage(Image source, int threshold) {
        if (source == null) return null;

        int width = (int) source.getWidth();
        int height = (int) source.getHeight();

        WritableImage output = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
                int intensity = (int) (brightness * 255);
                Color resultColor = intensity < threshold ? Color.BLACK : Color.WHITE;
                writer.setColor(x, y, resultColor);
            }
        }

        return output;
    }


    /*/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\*/
    /*\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/  Krawędzie  \_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/*/
    /*/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\_/‾\*/
    public static Image contourImage(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        PixelReader reader = inputImage.getPixelReader();
        WritableImage outputImage = new WritableImage(width, height);
        PixelWriter writer = outputImage.getPixelWriter();

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                Color current = reader.getColor(x, y);
                Color right = reader.getColor(x + 1, y);
                Color bottom = reader.getColor(x, y + 1);

                double diffRight = Math.abs(current.getRed() - right.getRed()) +
                        Math.abs(current.getGreen() - right.getGreen()) +
                        Math.abs(current.getBlue() - right.getBlue());

                double diffBottom = Math.abs(current.getRed() - bottom.getRed()) +
                        Math.abs(current.getGreen() - bottom.getGreen()) +
                        Math.abs(current.getBlue() - bottom.getBlue());

                double edge = Math.min(1.0, diffRight + diffBottom);
                Color edgeColor = new Color(edge, edge, edge, 1.0);
                writer.setColor(x, y, edgeColor);
            }
        }

        return outputImage;
    }


    /** Zapisuwane logów do pliku
     * @param log Wiadomość wpisana do logu
     */
    public static void logSaver(String log) {
        final Path logFilePath = Paths.get(System.getProperty("user.home"), "Documents", "log.txt");

        try{ Files.createDirectories(logFilePath.getParent()); // Validacja istnienia /Documents
            // Walidacja jeżeli log.txt nie istenieje
            if (!Files.exists(logFilePath)) {  Files.createFile(logFilePath); }

            try (BufferedWriter writer = Files.newBufferedWriter(logFilePath, StandardOpenOption.APPEND)) {
                writer.write(log);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Błąd podczas zapisu logu: " + e.getMessage());
        }
    }
}