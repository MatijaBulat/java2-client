package hr.algebra.client;

import hr.algebra.client.model.*;
import hr.algebra.client.network.ClientChatThread;
import hr.algebra.client.network.ClientGameHandler;
import hr.algebra.client.utils.JndiHelper;
import hr.algebra.client.utils.ScoreUtil;
import hr.algebra.rmi.RemoteService;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import javax.naming.NamingException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameController {
    private static int MAX_ROLLS = 3;
    private static int MIN_ROLLS = 0;
    private static int BONUS_POINTS = 50;
    private static final String RMI_PORT_KEY = "rmi.port";
    private static final String MESSAGE_FORMAT = "%s: %s";
    private static final int FONT_SIZE = 15;
    private ObservableList<Node> messages;
    private ClientGameHandler clientGameHandler;
    private Dice dice = new Dice();
    private ImageView[] diceImage = new ImageView[dice.getDice().length];
    private ImageView[] diceSelectedImage = new ImageView[dice.getDice().length];
    private volatile Player player = new Player();
    private volatile Player playerTwo = new Player();
    private int rollsRemaining = MAX_ROLLS;
    private boolean playerTurn = true;
    private boolean isPlayerTwoColumnSet = false;
    @FXML
    private AnchorPane scorePane;
    @FXML
    private GridPane grid;
    @FXML
    private ImageView die0;
    @FXML
    private ImageView die1;
    @FXML
    private ImageView die2;
    @FXML
    private ImageView die3;
    @FXML
    private ImageView die4;
    @FXML
    private ImageView die0Selected;
    @FXML
    private ImageView die1Selected;
    @FXML
    private ImageView die2Selected;
    @FXML
    private ImageView die3Selected;
    @FXML
    private ImageView die4Selected;
    @FXML
    private VBox rollingPane;
    @FXML
    private Button btnRoll;
    @FXML
    private Label lblRollsLeft;
    private Random random;
    private RemoteService remoteService;
    private Registry registry;
    @FXML
    private TextField tfMessage;
    @FXML
    private Button btnSend;
    @FXML
    private ScrollPane spContainer;
    @FXML
    private VBox vbMessages;
    @FXML
    private Label lblOpponentsTurn;

    @FXML
    public void initialize() {
        initClientGameHandler();
        initRemoteService();
        setPlayer();

        random = new Random();

        setupBoard();
        initDiceImages();
        updateRollLabel();
    }

    private void initClientGameHandler() {
        clientGameHandler = new ClientGameHandler(this);
        clientGameHandler.listenForPlayerUpdatesAndProcess();
    }

    private void initRemoteService() {
        try {
            registry = LocateRegistry.getRegistry(Integer.parseInt(JndiHelper.getValueFromConfiguration(RMI_PORT_KEY)));
            remoteService = (RemoteService) registry.lookup(RemoteService.REMOTE_OBJECT_NAME);
        } catch (NotBoundException | NamingException | IOException e) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, "NotBoundException | NamingException | IOException", e);
        }
        initMessages();
    }

    private void initMessages() {
        messages = FXCollections.observableArrayList();
        Bindings.bindContentBidirectional(messages, vbMessages.getChildren());

        receiveMessages();
    }

    private void receiveMessages() {
        ClientChatThread clientChatThread = new ClientChatThread(this);
        clientChatThread.setDaemon(true);
        clientChatThread.start();
    }

    @FXML
    private void sendMessage() throws RemoteException {
        if (!tfMessage.getText().trim().isEmpty()) {
            String msg = String.format(
                    MESSAGE_FORMAT,
                    player.getName(),
                    tfMessage.getText().trim());

            remoteService.sendMessage(msg);
            tfMessage.clear();
        }
    }

    public void addMessage(String message) {
        Label label = new Label();
        label.setFont(new Font(FONT_SIZE));
        label.setText(message);

        messages.add(label);
        moveScrollPane();
    }

    private void moveScrollPane() {
        spContainer.applyCss();
        spContainer.layout();
        spContainer.setVvalue(1D);
    }

    private void setPlayer() {
        player.setName(StartController.getPlayerName());
    }

    public void setupBoard() {
        for (int i = 0; i < ScoreType.values().length; i++) {
            grid.add(new Label(ScoreType.values()[i].getValue()), 0, i + 1);
        }

        setUpPlayerColumn(player, 1);
        clientGameHandler.sendPlayerState(player);
    }

    private synchronized void setUpPlayerColumn(Player player, int columnNum) {
        double width = grid.getColumnConstraints().get(1).getPrefWidth();
        double height = grid.getRowConstraints().get(0).getPrefHeight();

        StackPane panePlayer = new StackPane();
        grid.add(panePlayer, columnNum, 0);

        Rectangle rec = new Rectangle();
        rec.setWidth(width);
        rec.setHeight(height - 5);
        rec.setOpacity(0);
        rec.setFill(Color.web("transparent"));
        panePlayer.getChildren().add(rec);

        Label labelName = new Label();
        panePlayer.getChildren().add(labelName);

        labelName.setText(player.getName());

        for (ScoreType scoreType : ScoreType.values()) {
            int row = scoreType.ordinal() + 1;

            StackPane paneScore = new StackPane();
            grid.add(paneScore, columnNum, row);

            Rectangle rectangle = new Rectangle();
            rectangle.setWidth(width);
            rectangle.setHeight(height);
            rectangle.setOpacity(0);
            paneScore.getChildren().add(rectangle);

            Label labelScore = new Label();
            paneScore.getChildren().add(labelScore);

            if (scoreType == ScoreType.SUM || scoreType == ScoreType.BONUS || scoreType == ScoreType.TOTAL) {
                int score = player.getScores().getOrDefault(scoreType, 0);
                labelScore.setTextFill(Color.web("C8584A"));
                labelScore.setText(Integer.toString(score));
            }
        }
    }

    private void initDiceImages() {
        diceImage[0] = die0;
        diceImage[1] = die1;
        diceImage[2] = die2;
        diceImage[3] = die3;
        diceImage[4] = die4;

        diceSelectedImage[0] = die0Selected;
        diceSelectedImage[1] = die1Selected;
        diceSelectedImage[2] = die2Selected;
        diceSelectedImage[3] = die3Selected;
        diceSelectedImage[4] = die4Selected;
    }

    public void setOpponentPlayer(Player player) {
        if (!isPlayerTwoColumnSet) {
            setUpPlayerColumn(player, 2);
            isPlayerTwoColumnSet = true;
        }
        playerTwo = player;
        updatePlayerCells(player, 2);
        startPlayerTurn();

        if (checkIfDone()) {
            completeGame();
        }
    }

    private void startPlayerTurn() {
        playerTurn = true;
        resetRollButton();
        lblOpponentsTurn.setVisible(false);
    }

    private synchronized void updatePlayerCells(Player player, int columnNum) {
        Map<ScoreType, Integer> scores = player.getScores();

        for (ScoreType scoreType : ScoreType.values()) {
            int row = scoreType.ordinal() + 1;

            StackPane stackPane = (StackPane) getNodeFromGridPane(grid, columnNum, row);
            stackPane.setCursor(Cursor.DEFAULT);
            stackPane.setOnMouseClicked(null);

            Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
            rectangle.setOpacity(0);

            Label label = (Label) stackPane.getChildren().get(1);
            String text;

            if (scoreType == ScoreType.SUM || scoreType == ScoreType.BONUS || scoreType == ScoreType.TOTAL) {
                int score = scores.getOrDefault(scoreType, 0);
                text = Integer.toString(score);
            } else if (scores.containsKey(scoreType)) {
                int score = scores.get(scoreType);
                text = score > 0 ? Integer.toString(score) : "x";
            } else {
                text = "";
            }
            label.setText(text);
        }
    }

    public void roll() {
        rollsRemaining--;

        disableMouseInput(player);
        updateRollLabel();

        dice.roll();

        ParallelTransition parallelTransition = new ParallelTransition();

        for (int i = 0; i < dice.getDice().length; i++) {
            if (!dice.getDie(i).isSelected()) {
                animateDie(diceImage[i]);
                diceImage[i].setDisable(true);

                double pause = new Random().nextDouble() + 0.5;
                final int diceIndex = i;

                PauseTransition delay = new PauseTransition(Duration.seconds(pause));
                delay.setOnFinished(event -> updateDiceImage(diceIndex));
                parallelTransition.getChildren().add(delay);
            }
        }
        parallelTransition.play();
        parallelTransition.setOnFinished(event -> showPossibleScores(player));
    }

    public void animateDie(ImageView die) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < 10; i++) {
            int randomValue = random.nextInt(6) + 1;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * 50), event -> {
                String imagePath = "C:\\Users\\Matija\\Desktop\\java2-game\\Client\\src\\main\\resources\\hr\\algebra\\client\\images\\die" + randomValue + ".png";
                die.setImage(new Image("file:" + imagePath));
            });
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void disableMouseInput(Player player) {
        disableRollButton();
        updatePlayerCells(player, 1);
    }

    private void resetRollButton() {
        rollsRemaining = MAX_ROLLS;
        updateRollLabel();
        enableRollButton();
    }

    private void disableRollButton() {
        btnRoll.setDisable(true);
    }

    private void enableRollButton() {
        boolean state = rollsRemaining <= MIN_ROLLS;
        btnRoll.setDisable(state);
    }

    private void updateRollLabel() {
        lblRollsLeft.setText(Integer.toString(rollsRemaining));
    }

    private void updateDiceImage(int index) {
        FaceValue faceValue = dice.getDie(index).getFaceValue();

        switch (faceValue) {
            case ONE:
                diceImage[index].setImage(new Image("C:\\Users\\Matija\\Desktop\\java2-game\\Client\\src\\main\\resources\\hr\\algebra\\client\\images\\die1.png"));
                break;
            case TWO:
                diceImage[index].setImage(new Image("C:\\Users\\Matija\\Desktop\\java2-game\\Client\\src\\main\\resources\\hr\\algebra\\client\\images\\die2.png"));
                break;
            case THREE:
                diceImage[index].setImage(new Image("C:\\Users\\Matija\\Desktop\\java2-game\\Client\\src\\main\\resources\\hr\\algebra\\client\\images\\die3.png"));
                break;
            case FOUR:
                diceImage[index].setImage(new Image("C:\\Users\\Matija\\Desktop\\java2-game\\Client\\src\\main\\resources\\hr\\algebra\\client\\images\\die4.png"));
                break;
            case FIVE:
                diceImage[index].setImage(new Image("C:\\Users\\Matija\\Desktop\\java2-game\\Client\\src\\main\\resources\\hr\\algebra\\client\\images\\die5.png"));
                break;
            case SIX:
                diceImage[index].setImage(new Image("C:\\Users\\Matija\\Desktop\\java2-game\\Client\\src\\main\\resources\\hr\\algebra\\client\\images\\die6.png"));
                break;
            default:
                diceImage[index].setImage(null);
                break;
        }
        diceImage[index].setDisable(false);
    }

    private void showPossibleScores(Player player) {
        int column = 1;
        ScoreUtil scoreUtil = new ScoreUtil(dice.getFaceValues());
        final Map<ScoreType, Integer> possibleScores = scoreUtil.scoreMap();

        for (ScoreType scoreType : ScoreType.values()) {
            int row = scoreType.ordinal() + 1;

            if (scoreType == ScoreType.SUM || scoreType == ScoreType.BONUS || scoreType == ScoreType.TOTAL) {
                continue;
            }
            if (player.getScores().containsKey(scoreType)) {
                continue;
            }
            makeScoreAvailable(scoreType, player, possibleScores.get(scoreType), column, row);
        }
        enableRollButton();
    }

    private void makeScoreAvailable(ScoreType scoreType, Player player, final int score, int column, int row) {
        Node node = getNodeFromGridPane(grid, column, row);

        StackPane stackPane = (StackPane) node;
        Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
        Label label = (Label) stackPane.getChildren().get(1);

        String color = score > 0 ? "#ffc515" : "transparent";

        rectangle.setFill(Color.web(color));
        rectangle.setOpacity(1.0);

        String text = score > 0 ? Integer.toString(score) : "x";
        label.setText(text);

        stackPane.setCursor(Cursor.HAND);
        stackPane.setOnMouseClicked(event -> selectScore(scoreType, score, player));
    }

    private void selectScore(ScoreType scoreType, int score, Player player) {
        setPlayerScore(scoreType, score, player);
        updatePlayerCells(player, 1);

        if (!checkIfDone()) {
            resetDice();
            disableRollButton();

            lblOpponentsTurn.setVisible(true);
            playerTurn = false;

            clientGameHandler.sendPlayerState(player);
        } else {
            completeGame();
        }
    }

    public void completeGame() {
        var result = determineWinner(player, playerTwo);

        if (result != null) {
            showAlert("Player " + result.getName() + " won!\n Score: " + result.getScores().get(ScoreType.TOTAL));
        } else {
            showAlert("It’s a draw.");
        }
    }

    private void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Game over!");
        alert.setContentText(text);
        alert.show();
    }

    public Player determineWinner(Player player1, Player player2) {
        int scorePlayer1 = player1.getScores().get(ScoreType.TOTAL);
        int scorePlayer2 = player2.getScores().get(ScoreType.TOTAL);

        if (scorePlayer1 > scorePlayer2) {
            return player1;
        } else if (scorePlayer2 > scorePlayer1) {
            return player2;
        } else {
            return null;
        }
    }

    private boolean checkIfDone() {
        var scoreLength = ScoreType.values().length;
        return player.getScores().size() == scoreLength && playerTwo.getScores().size() == scoreLength;
    }

    private void setPlayerScore(ScoreType scoreType, int score, Player player) {
        player.setScore(scoreType, score);
        Map<ScoreType, Integer> scores = player.getScores();

        int sum = 0;
        int total = 0;

        for (ScoreType type : scores.keySet()) {
            if (type == ScoreType.SUM || type == ScoreType.BONUS || type == ScoreType.TOTAL) {
                continue;
            }

            if (type.ordinal() < ScoreType.SUM.ordinal()) {
                sum += scores.get(type);
            } else {
                total += scores.get(type);
            }
        }

        int bonus = sum >= 63 ? BONUS_POINTS : 0;

        player.setScore(ScoreType.BONUS, bonus);
        player.setScore(ScoreType.SUM, sum);
        player.setScore(ScoreType.TOTAL, sum + bonus + total);
    }

    public void selectDie(Event event) {
        ImageView imageView = (ImageView) event.getSource();
        int cleanId = Integer.parseInt(imageView.getId().substring("die".length()));

        Die die = dice.getDie(cleanId);
        boolean selected = die.isSelected();
        die.setSelected(!selected);

        double opacity = (die.isSelected()) ? 1.0 : 0;

        switch (cleanId) {
            case 0:
                die0Selected.setOpacity(opacity);
                break;
            case 1:
                die1Selected.setOpacity(opacity);
                break;
            case 2:
                die2Selected.setOpacity(opacity);
                break;
            case 3:
                die3Selected.setOpacity(opacity);
                break;
            case 4:
                die4Selected.setOpacity(opacity);
                break;
            default:
                break;
        }
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (!(node instanceof Group) && GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private void resetDice() {
        for (ImageView dieImage : diceImage) {
            dieImage.setImage(null);
        }

        for (ImageView dieSelectedImage : diceSelectedImage) {
            dieSelectedImage.setOpacity(0.0);
        }

        for (Die die : dice.getDice()) {
            die.setSelected(false);
        }
    }
}