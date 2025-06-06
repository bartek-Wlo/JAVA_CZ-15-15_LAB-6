package pwr.bw275470.java_2_okienkowa_javafx.Controllers;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class ObrazController {
    public ImageView imageView;

    public void setImage(Image image) {
        if (image != null) {
            imageView.setImage(image);
        }
    }
}
