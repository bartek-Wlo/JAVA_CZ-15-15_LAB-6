module pwr.bw275470.java_2_okienkowa_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires javafx.graphics;
    requires java.desktop;


    opens pwr.bw275470.java_2_okienkowa_javafx to javafx.fxml;
    exports pwr.bw275470.java_2_okienkowa_javafx;
    exports pwr.bw275470.java_2_okienkowa_javafx.Controllers;
    opens pwr.bw275470.java_2_okienkowa_javafx.Controllers to javafx.fxml;
    exports pwr.bw275470.java_2_okienkowa_javafx.utils;
    opens pwr.bw275470.java_2_okienkowa_javafx.utils to javafx.fxml;
}