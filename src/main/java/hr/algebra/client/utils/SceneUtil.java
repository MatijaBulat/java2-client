package hr.algebra.client.utils;

import hr.algebra.client.YahtzeeApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import java.io.IOException;

public class SceneUtil {
    public static void setNewSceneToStage(String viewName, String title, double v, double v1) {
        FXMLLoader fxmlLoader = new FXMLLoader(YahtzeeApplication.class.getResource(viewName));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), v, v1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        YahtzeeApplication.getMainStage().getIcons().add(new Image("C:\\Users\\Matija\\Desktop\\java2-game\\Client\\src\\main\\resources\\hr\\algebra\\client\\images\\yahtzee-icon.jpg"));
        YahtzeeApplication.getMainStage().setTitle(title);
        YahtzeeApplication.getMainStage().setScene(scene);
        YahtzeeApplication.getMainStage().show();
    }
}
