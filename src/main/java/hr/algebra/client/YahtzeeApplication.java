package hr.algebra.client;

import hr.algebra.client.utils.SceneUtil;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class YahtzeeApplication extends Application {
    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;

        SceneUtil.setNewSceneToStage("start-view.fxml", "Yahtzee", 395, 520);
    }
    public static Stage getMainStage() {
        return mainStage;
    }
    public static void main(String[] args) {
        launch();
    }
}