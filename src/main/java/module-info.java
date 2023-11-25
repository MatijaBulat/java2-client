module hr.algebra.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.logging;
    requires java.naming;
    requires java.desktop;


    opens hr.algebra.client to javafx.fxml;
    exports hr.algebra.client;
}