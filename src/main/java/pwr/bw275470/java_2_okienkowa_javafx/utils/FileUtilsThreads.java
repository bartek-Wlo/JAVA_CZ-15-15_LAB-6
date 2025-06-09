package pwr.bw275470.java_2_okienkowa_javafx.utils;
import java.util.stream.IntStream;

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

public class FileUtilsThreads {
    public static Image contourImageThreads(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        PixelReader reader = inputImage.getPixelReader();
        WritableImage outputImage = new WritableImage(width, height);
        PixelWriter writer = outputImage.getPixelWriter();

        IntStream.range(1, height - 1).parallel().forEach(y -> {
//        for (int y = 1; y < height - 1; y++) {
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
        });

        return outputImage;
    }

    public static Image thresholdImageThreas(Image source, int threshold) {
        if (source == null) return null;

        int width = (int) source.getWidth();
        int height = (int) source.getHeight();

        WritableImage output = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        IntStream.range(0, height - 1).parallel().forEach(y -> {
//        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
                int intensity = (int) (brightness * 255);
                Color resultColor = intensity < threshold ? Color.BLACK : Color.WHITE;
                writer.setColor(x, y, resultColor);
            }
        });

        return output;
    }


    public static Image generateNegativeImageThreads(Image source) {
        if (source == null) return null;

        int width = (int) source.getWidth();
        int height = (int) source.getHeight();

        WritableImage output = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        IntStream.range(1, height - 1).parallel().forEach(y -> {
//        for (int y = 0; y < height; y++) {
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
        });

        return output;
    }
}