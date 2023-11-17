package hr.algebra.client;

import hr.algebra.client.model.*;
import hr.algebra.client.network.ClientChatThread;
import hr.algebra.client.network.ClientGameHandler;
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
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class GameController {
    private static int MAX_ROLLS = 3;
    private static int MIN_ROLLS = 0;
    private static int BONUS_POINTS = 50;
    private static final int RMI_PORT = 1099;
    private static final int RANDOM_PORT_HINT = 0;
    private Dice dice = new Dice();
    private ImageView[] diceImage = new ImageView[dice.getDice().length];
    private ImageView[] diceSelectedImage = new ImageView[dice.getDice().length];
    private List<Player> players = new ArrayList<>();
    private ScoreType currentScoreType = ScoreType.ONES; // used when playing forced
    private int rollsRemaining = MAX_ROLLS;
    private int currentPlayerIndex = 0;
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
    private static final String MESSAGE_FORMAT = "%s: %s";
    private static final int FONT_SIZE = 15;
    private ObservableList<Node> messages;
    private ClientGameHandler clientGameHandler;

    @FXML
    public void initialize() throws IOException {
        //Socket socket = new Socket("localhost", 1989);
        clientGameHandler = new ClientGameHandler(this);
        clientGameHandler.listenForPlayerUpdatesAndProcess();

        random = new Random();
        //List<Player> players = new ArrayList<>();
        players.add(StartController.getPlayer());

        setPlayers(players);
        setupBoard();

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

        updateRollLabel();

        initRemoteService();
    }

    private void initRemoteService() {
        try {
            registry = LocateRegistry.getRegistry(RMI_PORT);
            System.out.println("Registry retrieved");
            remoteService = (RemoteService) registry.lookup(RemoteService.REMOTE_OBJECT_NAME);
            System.out.println("Service retrieved");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
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
                    players.get(0).getName(),
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

   /* GameController gameController = fxmlLoader.getController();
        gameController.setPlayer(player);
        gameController.setupBoard();*/

    /**
     * Setup board based on players and game mode. Should only be called once per game.
     */
    public void setupBoard() {
        grid.add(new Label("Ones"), 0, 1);
        grid.add(new Label("Twos"), 0, 2);
        grid.add(new Label("Threes"), 0, 3);
        grid.add(new Label("Fours"), 0, 4);
        grid.add(new Label("Fives"), 0, 5);
        grid.add(new Label("Sixes"), 0, 6);
        grid.add(new Label("Sum 63"), 0, 7);
        grid.add(new Label("Bonus"), 0, 8);
        grid.add(new Label("1 pair"), 0, 9);
        grid.add(new Label("2 pair"), 0, 10);
        grid.add(new Label("3 of a kind"), 0, 11);
        grid.add(new Label("4 of a kind"), 0, 12);
        grid.add(new Label("Small straight"), 0, 13);
        grid.add(new Label("Large straight"), 0, 14);
        grid.add(new Label("Full house"), 0, 15);
        grid.add(new Label("Chance"), 0, 16);
        grid.add(new Label("Yatzy"), 0, 17);
        grid.add(new Label("Total"), 0, 18);

        for (int column = 1; column < players.size() + 1; column++) {
            double width = grid.getColumnConstraints().get(column).getPrefWidth();
            double height = grid.getRowConstraints().get(0).getPrefHeight();

            StackPane panePlayer = new StackPane();
            grid.add(panePlayer, column, 0);

            Rectangle rec = new Rectangle();
            rec.setWidth(width);
            rec.setHeight(height - 5);
            rec.setOpacity(0);
            rec.setFill(Color.web("transparent"));
            panePlayer.getChildren().add(rec);

            Label labelName = new Label();
            panePlayer.getChildren().add(labelName);

            String playerName = players.get(column - 1).getName();
            labelName.setText(playerName);

            for (ScoreType scoreType : ScoreType.values()) {
                int row = scoreType.ordinal() + 1;

                StackPane paneScore = new StackPane();
                grid.add(paneScore, column, row);

                Rectangle rectangle = new Rectangle();
                rectangle.setWidth(width);
                rectangle.setHeight(height);
                rectangle.setOpacity(0);
                paneScore.getChildren().add(rectangle);

                Label labelScore = new Label();
                paneScore.getChildren().add(labelScore);

                if (scoreType == ScoreType.SUM || scoreType == ScoreType.BONUS || scoreType == ScoreType.TOTAL) {
                    int score = players.get(column - 1).getScores().getOrDefault(scoreType, 0);
                    labelScore.setTextFill(Color.web("C8584A"));
                    labelScore.setText(Integer.toString(score));
                }
            }
            var player = players.get(0);
            clientGameHandler.sendPlayerState(player);
        }
        highlightCurrentPlayer();
    }

    /**
     * Set the player whos turn is next
     */
    private void nextPlayer() {
        Player lastPlayer = players.get(players.size() - 1);
        System.out.println(lastPlayer.getScores());
        currentPlayerIndex = lastPlayer == currentPlayer() ? 0 : currentPlayerIndex + 1;
        highlightCurrentPlayer();

        if (currentPlayerIndex == 0) { // todo move this to a better suited place
            nextScoreType();
        }
    }

    /**
     * Get the current player
     *
     * @return the current player
     */
    private Player currentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Highlight the name of the current player in the GUI
     */
    private void highlightCurrentPlayer() {
        for (int i = 0; i < players.size(); i++) {
            int column = playerToColumnIndex(players.get(i));
            int row = 0;

            StackPane stackPane = (StackPane) getNodeFromGridPane(grid, column, row);
            Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);

            if (i == currentPlayerIndex) {
                rectangle.setOpacity(1.0);
            } else {
                rectangle.setOpacity(0.0);
            }
        }
    }

    /**
     * Update the display of the scores in the given players score boxes
     *
     * @param player to update scores
     */
    private void updatePlayerCells(Player player) {
        int column = playerToColumnIndex(player);
        Map<ScoreType, Integer> scores = player.getScores();

        for (ScoreType scoreType : ScoreType.values()) {
            int row = scoreType.ordinal() + 1;

            StackPane stackPane = (StackPane) getNodeFromGridPane(grid, column, row);
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

    /**
     * Roll the unselected dice
     */
    public void roll() {
        Player player = currentPlayer();
        rollsRemaining--; // decrease remaining rolls for each roll done
        disableMouseInput(player); // to avoid double rolls and locking old score
        updateRollLabel(); // update number of rolls

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
        updatePlayerCells(player);
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

    /**
     * Update the image of the given dice by index
     *
     * @param index dice index [0-4]
     */
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
                diceImage[index].setImage(null); // should never occur
                break;
        }

        diceImage[index].setDisable(false);
    }

    private void showPossibleScores(Player player) {
        int column = playerToColumnIndex(player);
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
        updatePlayerCells(player);

        boolean done = checkIfDone();

        if (!done) {
            resetDice();
            resetRollButton();
            nextPlayer();
        } else {
            System.out.println("complete game");
            completeGame();
        }
    }

    public void completeGame() {
        //todo fix this shit
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("You won!");
        alert.setContentText("Player " + players.get(0).getName() + " won!");

        alert.showAndWait();
        /* sort based on total */
      /*  List<Player> sortedPlayers = new ArrayList<>();
        for (Player player : players) {
            int playerTotal = player.getScores().get(ScoreType.TOTAL);
            int index = 0;
            for (int i = 0; i < sortedPlayers.size(); i++) {
                int sortedTotal = sortedPlayers.get(i).getScores().get(ScoreType.TOTAL);

                if (playerTotal < sortedTotal) {
                    index = i;
                    break;
                }
                index = i + 1;
            }
            sortedPlayers.add(index, player);
        }

        GridPane gridPane = (GridPane) scorePane.getChildren().get(0);
        double width = grid.getColumnConstraints().get(0).getPrefWidth();
        double height = grid.getRowConstraints().get(0).getPrefHeight();

        *//* put in grid *//*
        for (int row = 1; row <= sortedPlayers.size(); row++) {
            for (int col = 0; col < 3; col++) {
                int index = sortedPlayers.size() - row;

                StackPane stackPane = new StackPane();
                gridPane.add(stackPane, col, row);

                Rectangle rectangle = new Rectangle();
                rectangle.setWidth(width);
                rectangle.setHeight(height);
               // rectangle.setFill(Color.web(colors[realPlayerIndex(sortedPlayers.get(index))]));
                stackPane.getChildren().add(rectangle);

                Label label = new Label();
                String text = "";

                switch (col) {
                    case 0:
                        text = String.format("#%d", row);
                        break;
                    case 1:
                        text = sortedPlayers.get(index).getName();
                        break;
                    case 2:
                        text += sortedPlayers.get(index).getScores().get(ScoreType.TOTAL);
                        break;
                    default:
                        break;
                }

                label.setText(text);
                stackPane.getChildren().add(label);
            }
        }

        rollingPane.setVisible(false);
        scorePane.setVisible(true);*/
    }

    /*private int realPlayerIndex(Player player) {
        for (int i = 0; i < players.size(); i++) {
            if (player.equals(players.get(i))) {
                return i;
            }
        }
        return -1;
    }*/

    private void nextScoreType() {
        do {
            currentScoreType = currentScoreType.next();
        }
        while (currentScoreType == ScoreType.SUM || currentScoreType == ScoreType.BONUS || currentScoreType == ScoreType.TOTAL);
    }

    private boolean checkIfDone() {
        int lastPlayerScoreSize = players.get(players.size() - 1).getScores().size();
        return lastPlayerScoreSize == ScoreType.values().length;
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
                break; // should never occur
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

  /*  private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (!(node instanceof Group)) {
                Integer columnIndex = GridPane.getColumnIndex(node);
                Integer rowIndex = GridPane.getRowIndex(node);
                if (columnIndex != null && rowIndex != null && columnIndex.intValue() == col && rowIndex.intValue() == row) {
                    return node;
                }
            }
        }
        return null;
    }*/

    private int playerToColumnIndex(Player player) {
        for (int i = 0; i < players.size(); i++) {
            if (player == players.get(i)) {
                return i + 1;
            }
        }
        throw new IllegalArgumentException("Illegal player argument");
    }

    private void resetDice() {
        /* remove dice images */
        for (ImageView dieImage : diceImage) {
            dieImage.setImage(null);
        }

        /* remove dice selected images*/
        for (ImageView dieSelectedImage : diceSelectedImage) {
            dieSelectedImage.setOpacity(0.0);
        }

        /* set all dice to unselected */
        for (Die die : dice.getDice()) {
            die.setSelected(false);
        }
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /*private void clearBoard() {
        grid.getChildren().clear();
    }*/

}