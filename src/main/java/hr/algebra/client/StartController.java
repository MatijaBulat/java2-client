package hr.algebra.client;

import hr.algebra.client.model.Player;
import hr.algebra.client.utils.SceneUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class StartController {
    @FXML
    private TextField playerNameTf;
    @FXML
    private Button startGameBtn;
    private static Player player;

    public void startGame() throws IOException {
        player = new Player(playerNameTf.getText());

        SceneUtil.setNewSceneToStage("game-view.fxml", "Yahtzee", 924, 527);
    }
    public static Player getPlayer() { return player; }
}