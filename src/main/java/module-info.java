module hr.algebra.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;


    opens hr.algebra.client to javafx.fxml;
    exports hr.algebra.client;
}