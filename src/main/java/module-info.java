module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires jdk.compiler;
    requires java.desktop;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}