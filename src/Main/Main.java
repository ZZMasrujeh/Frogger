package Main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

import static Main.GameState.DEMO;
import static Main.GameState.PLAYING;

/**
 * The primary stage of the game.
 * <br><br>
 * This is the arcade room view. Allows the user to exit the game completely (Escape)
 * or switch to the cabinet view by clicking the mini screen.
 * <br><br>
 * Handles input that is not related to playing.
 */

public class Main extends Application {
    private static double miniScreenWidthRatio = 14;
    private static double miniScreeHeightRatio = 6.5;
    private static double screenWidth = Screen.getPrimary().getBounds().getWidth();
    private static double screenHeight = Screen.getPrimary().getBounds().getHeight();
    static double playingWidth = screenWidth / miniScreenWidthRatio;
    static double playingHeight = screenHeight / miniScreeHeightRatio;

    private static Image arcadeRoomImage = new Image(Objects.requireNonNull(Main.class.getClassLoader().
            getResource("Images/Picture2.png")).toString());
    private static ImageView arcadeRoomBackground = new ImageView(arcadeRoomImage);
    private static Media coinM = new Media(Objects.requireNonNull(Main.class
            .getClassLoader().getResource("Sounds/coin_in.wav")).toString());

    private static Game fullScreenGame;
    private static Scene scene;
    private static StackPane sp = new StackPane();
    private static SubScene demoSubScene;
    private static SubScene gameSubScene;
    private static Stage stage;
    private static Bounds demoSubSceneBounds;
    private static Game demoGame;
    private static StackPane name = new StackPane(
            new Label("by Zoher Zacharias Masrujeh\nzzmasrujeh@uclan.ac.uk") {{
                setFont(Font.font(screenWidth / 76.8));
                setTextFill(Color.WHITE);
            }}
    );


    @Override
    public void start(Stage primaryStage) throws Exception {
        demoGame = new Game(2, DEMO);
        demoSubScene = new SubScene(demoGame.getRoot(), playingWidth, playingHeight);
        sp.setBackground(new Background(new BackgroundFill(Color.rgb(0, 20, 20), CornerRadii.EMPTY, Insets.EMPTY)));
        arcadeRoomBackground.setFitWidth(screenWidth);
        arcadeRoomBackground.setFitHeight(screenHeight);
        sp.getChildren().addAll(arcadeRoomBackground, demoSubScene, name);

        double xTranslateRatio = 3.081861958266453;
        demoSubScene.setTranslateX(screenWidth / xTranslateRatio);
        demoSubScene.setTranslateY(screenHeight / -7.2);
        name.setTranslateX(screenWidth / xTranslateRatio);
        name.setTranslateY(screenHeight / 4);

        scene = new Scene(sp);
        scene.setOnMouseClicked(mouseClick);
        scene.setOnKeyPressed(key);

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setMaximized(true);
        primaryStage.show();
        stage = primaryStage;

        demoSubSceneBounds = demoSubScene.getBoundsInParent();
        scene.setOnMouseMoved(mouseMove);
    }

    /**
     * Changes to the cabinet view.
     */
    private static void changeToGame() {
        demoGame.stop();
        playingWidth = screenWidth / 2.0;
        playingHeight = screenHeight;
        fullScreenGame = new Game(2, DEMO);
        sp.getChildren().clear();
        gameSubScene = new SubScene(fullScreenGame.getRoot(), playingWidth, playingHeight);
        sp.getChildren().add(gameSubScene);
        fullScreenGame.getRoot().requestFocus();
    }

    /**
     * Changes to the arcade room view.
     */
    private static void changeToArcadeRoom() {
        fullScreenGame.stop();
        playingWidth = screenWidth / miniScreenWidthRatio;
        playingHeight = screenHeight / miniScreeHeightRatio;
        sp.getChildren().clear();
        demoGame = new Game(2, DEMO);
        demoSubScene.setRoot(demoGame.getRoot());
        sp.getChildren().addAll(arcadeRoomBackground, demoSubScene, name);
        fullScreenGame = null;
        scene.setOnKeyPressed(key);
    }

    private static EventHandler<KeyEvent> key = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            //exit the game
            if (event.getCode() == KeyCode.ESCAPE) {
                if (fullScreenGame == null) {
                    stage.close();
                } else {
                    showConfirmationWindow();
                }
                return;
            }
            //waiting for 'coin to be inserted'
            if (event.getCode() == KeyCode.DIGIT1) {
                if (fullScreenGame != null && !fullScreenGame.isPlayable()) {
                    //start a playable game at level 1
                    fullScreenGame.stop();
                    fullScreenGame = new Game(1, PLAYING);
                    gameSubScene.setRoot(Main.fullScreenGame.getRoot());
                    fullScreenGame.getGameRoot().requestFocus();
                    MediaPlayer coinMediaPlayer = new MediaPlayer(coinM);
                    coinMediaPlayer.setOnEndOfMedia(fullScreenGame::start);
                    coinMediaPlayer.play();
                } else if (fullScreenGame != null && fullScreenGame.isGameOver() && fullScreenGame.isContinuable()) {
                    //during continue countdown
                    fullScreenGame.continueGame();
                }
            }
        }
    };

    /**
     * Whilst in the arcade room, checks whether the user clicked the cabinet's screen.
     */
    private EventHandler<MouseEvent> mouseClick = event -> {
        if (fullScreenGame == null) {
            double mouseX = event.getX();
            double mouseY = event.getY();
            double demoMinX = demoSubSceneBounds.getMinX();
            double demoMaxX = demoSubSceneBounds.getMaxX();
            double demoMinY = demoSubSceneBounds.getMinY();
            double demoMaxY = demoSubSceneBounds.getMaxY();
            if (demoMinX <= mouseX && mouseX <= demoMaxX && demoMinY <= mouseY && mouseY <= demoMaxY)
                changeToGame();
        }
    };

    /**
     * Toggles the mouse cursor between hand and pointer, depending on being on top of the mini screen or not.
     */
    private EventHandler<MouseEvent> mouseMove = event -> {
        if (demoSubSceneBounds.getMinX() <= event.getX() && event.getX() <= demoSubSceneBounds.getMaxX()
                && demoSubSceneBounds.getMinY() <= event.getY() && event.getY() <= demoSubSceneBounds.getMaxY()) {
            scene.setCursor(Cursor.HAND);
        } else {
            scene.setCursor(Cursor.DEFAULT);
        }
    };

    /**
     * Confirmation window to ask the user whether to exit the cabinet view.
     */
    private static void showConfirmationWindow() {
        int fontRatio = 30;
        fullScreenGame.togglePause();
        Stage confirmStage = new Stage(StageStyle.UNDECORATED);
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        Label l = new Label("Back to the arcade room ?") {{
            setFont(Font.font(playingWidth / fontRatio * 2));
            setTextFill(Color.WHITE);
        }};
        Button y = new Button("Yes") {{
            setFont(Font.font(playingWidth / fontRatio));
        }};
        Button n = new Button("No") {{
            setFont(Font.font(playingWidth / fontRatio));
        }};
        VBox v = new VBox(l, y, n) {{
            setPadding(new Insets(20, 20, 20, 20));
            setSpacing(10);
            setAlignment(Pos.CENTER);
            setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        }};
        Scene s = new Scene(v);
        confirmStage.setScene(s);
        confirmStage.setOnCloseRequest(event -> {
            //for Alt+F4 to handle like yes
            changeToArcadeRoom();
        });
        y.setOnAction(event1 -> {
            changeToArcadeRoom();
            confirmStage.close();
        });
        n.setOnAction(event1 -> {
            fullScreenGame.togglePause();
            confirmStage.close();
        });
        confirmStage.show();
        n.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
