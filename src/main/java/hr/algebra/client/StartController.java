package hr.algebra.client;

import hr.algebra.client.utils.SceneUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class StartController {
    @FXML
    private TextField playerNameTf;
    @FXML
    private Button startGameBtn;
    private static String playerName;

    public void startGame() throws IOException {
        playerName = playerNameTf.getText();

        SceneUtil.setNewSceneToStage("game-view.fxml", "Yahtzee", 600, 500);
    }
    public static String getPlayerName() {
        return playerName;
    }
}