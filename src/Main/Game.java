package Main;

import Objects.*;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Random;

import static Main.Game.SoundType.*;
import static Main.GameState.*;
import static Objects.FrogDirection.*;
import static Objects.GoalState.*;
import static Objects.LaneBuilder.*;

public class Game {
    //Game BOUNDS
    private GameState gameState;
    private Pane gameRoot = new Pane();
    private Pane root = new Pane();
    public static double playableWidth;
    public static double playableHeight;
    public static double laneHeight;
    public static double goalWidth;
    /**active/inactive continue countdown. Becomes false only when the player doesn't continue in time.**/
    private boolean continuable = true;
    private boolean highscoreChecked = false;
    private int highscoreIndex = Integer.MAX_VALUE;
    private int initialLives = 7;
    private SimpleIntegerProperty levelProperty = new SimpleIntegerProperty();
    private boolean gameOver = false;
    private boolean paused = false;
    private int remainingGoals = 5;
    private AnimationTimer at;
    private long continueTimer = 10000;
    private SimpleStringProperty continueProperty = new SimpleStringProperty("CONTINUE ? 9");
    //TIME and MESSAGES
    private long frameBefore;
    private Rectangle timeBar = new Rectangle();
    private double maxTimeRemaining = 50;
    private double currentTimeRemaing = maxTimeRemaining;
    private double maxTextWidth;
    private double minTextHeight;
    private Text messageText;
    private SimpleStringProperty spScore = new SimpleStringProperty("00000");
    private static Highscores highscores = new Highscores();
    private StackPane highscoresRoot;
    private long messageTimer = 0;
    private long demoTimer = 0;
    private final int messageDuration = 3000;
    private StackPane continueContainer;
    private double velocity;
    //CONTAINERS
    private Hashtable<Integer, ArrayList<IMovableObject>> allObjects = new Hashtable<>();
    private Hashtable<Integer, Boolean> visitedLanes;
    private Goal[] goals;
    private ArrayList<ImageView> lifeIndicators = new ArrayList<>();
    private StackPane messageContainer;
    //FROG
    private ControllableFrog controllableFrog;
    private FrogDirection direction = null;
    private boolean frogAlive = true;
    private boolean flyAppeared = false;
    private boolean crocodileAppeared = false;
    public static int flyDuration = 10000;
    private SimpleIntegerProperty livesProperty = new SimpleIntegerProperty();
    private IMovableObject controllableFrogsMount = null;
    private boolean updateFrogsX = false;
    private int demoFrameCounter = 0;
    //MEDIA
    private static Image levelIndicatorImage = new Image(Objects.requireNonNull(Game.class.getClassLoader()
            .getResource("Images/level_indicator.png")).toString());
    private static Image lifeIndicatorImage = new Image(Objects.requireNonNull(Game.class.getClassLoader()
            .getResource("Images/life_indicator.png")).toString());
    private AudioClip stageStart = new AudioClip(Objects.requireNonNull(getClass().getClassLoader()
            .getResource("Sounds/stage_start.mp3")).toString());
    private AudioClip mainTheme = new AudioClip(Objects.requireNonNull(getClass().getClassLoader()
            .getResource("Sounds/main_theme.mp3")).toString());
    private AudioClip hopClip = new AudioClip(Objects.requireNonNull(getClass().getClassLoader()
            .getResource("Sounds/hop.wav")).toString());
    private AudioClip drownClip = new AudioClip(Objects.requireNonNull(getClass().getClassLoader()
            .getResource("Sounds/drowned.wav")).toString());
    private AudioClip collidedClip = new AudioClip(Objects.requireNonNull(getClass().getClassLoader()
            .getResource("Sounds/collided.wav")).toString());
    private AudioClip timeClip = new AudioClip(Objects.requireNonNull(getClass().getClassLoader()
            .getResource("Sounds/time.wav")).toString());
    private AudioClip nowPlaying;

    enum SoundType{
        COLLIDED,DROWNED,HOP,TIME
    }

    Game(int level, GameState state) {
        this.gameState = state;
        root.getChildren().add(gameRoot);
        playableWidth = Main.playingWidth;
        playableHeight = Main.playingHeight;
        laneHeight = playableHeight / TOTAL_LANES;
        goalWidth = playableWidth / 12.4;
        velocity = playableWidth / 24; //40

        gameRoot.setMinWidth(playableWidth);
        gameRoot.setMaxWidth(playableWidth);
        gameRoot.setStyle("-fx-background-color: linear-gradient" +
                "(from 0% 0% to 0% 100%, #000042 0%, #000042 50%, black 50%, black 100%);");

        addLevelIndicators(level);
        livesProperty.addListener(livesChangeListener);
        levelProperty.addListener(levelChangeListener);
        livesProperty.set(initialLives);

        addPavements();
        addGoals();
        addScoreAndHighscore();
        addVisitedLanes();
        addTimeBarAndText();
        addMessageTexts();
        addMovableObjects();

        controllableFrog = new ControllableFrog(playableWidth / 2, BOTTOM_PAVEMENT_LANE);
        gameRoot.getChildren().add(controllableFrog.getMainGroup());
        gameRoot.setOnKeyPressed(keyEventEventHandler);
        controllableFrog.getHopAnimation().setOnFinished(hopFinished);

        mainTheme.setVolume(0.3);   //its too loud!

        //start 'demos'
        if (state != PLAYING )
            start();
    }

    Pane getRoot() { return root; }
    Pane getGameRoot() { return gameRoot; }

    private void addLevelIndicators(int level){
        levelProperty.set(level);
        for (int i = 1; i <=level ; i++) {
            ImageView iv = new ImageView(levelIndicatorImage);
            double w = playableWidth / 48;
            iv.setFitHeight(laneHeight / 2.5);
            iv.setY(laneHeight * TIME_LANE);
            iv.setFitWidth(w);
            iv.setX(playableWidth - 2 * w - i * 2 * w);
            gameRoot.getChildren().add(iv);
        }
    }

    private final ChangeListener levelChangeListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            ImageView iv = new ImageView(levelIndicatorImage);
            double w = playableWidth / 48;
            iv.setFitWidth(w);
            iv.setY(laneHeight * TIME_LANE);
            iv.setFitHeight(laneHeight / 2.5);
            iv.setX(playableWidth - 2 * w - ((int) newValue) * 2 * w);
            gameRoot.getChildren().add(iv);
        }
    };

    private final ChangeListener livesChangeListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            if (lifeIndicators.size() > 0) {
                ImageView iv = lifeIndicators.get(lifeIndicators.size() - 1);
                gameRoot.getChildren().remove(iv);
                lifeIndicators.remove(iv);
            } else {
                for (int i = 0; i < livesProperty.getValue() - 1; i++) {
                    ImageView iv = new ImageView(lifeIndicatorImage);
                    double w = playableWidth / 48;
                    iv.setFitWidth(w);
                    iv.setY(laneHeight * TIME_LANE);
                    iv.setFitHeight(laneHeight / 2.5);
                    iv.setX(w * lifeIndicators.size());
                    lifeIndicators.add(iv);
                    gameRoot.getChildren().add(iv);
                }
            }
        }
    };

    private void addScoreAndHighscore() {
        double fontSize = playableWidth / 24;
        Text pointsText = new Text("1-UP");
        Text highscoreText = new Text("HI-SCORE");
        Text[] texts = {pointsText, highscoreText};
        for (Text t : texts) {
            t.setFont(Font.font("", FontWeight.EXTRA_BOLD, fontSize));
            t.setTextOrigin(VPos.TOP);
            t.setTranslateY(-fontSize / 4);
            t.setFill(Color.WHITE);
        }
        pointsText.setX(playableWidth / 5 - pointsText.getBoundsInParent().getWidth() / 2);
        highscoreText.setX(playableWidth / 2 - highscoreText.getBoundsInParent().getWidth() / 2);
        gameRoot.getChildren().addAll(texts);

        Text pointsNText = new Text();
        Text highscoreNText = new Text();
        Text[] nTexts = {pointsNText, highscoreNText};
        pointsNText.textProperty().bind(spScore);
        highscoreNText.textProperty().bind(highscores.topScore);

        for (Text t : nTexts) {
            t.setFont(Font.font("", FontWeight.EXTRA_BOLD, fontSize));
            t.setTextOrigin(VPos.TOP);
            t.setY(pointsText.getLayoutBounds().getHeight());
            t.setTranslateY(-fontSize / 1.5);
            t.setFill(Color.RED);
        }
        pointsNText.setX(pointsText.getBoundsInParent().getMaxX() - pointsNText.getBoundsInParent().getWidth());
        highscoreNText.setX(highscoreText.getBoundsInParent().getHeight() / 2 + highscoreText.getBoundsInParent().getMinX());
        gameRoot.getChildren().addAll(nTexts);
    }

    /**
     * Updates the points the coinMediaPlayer has scored.
     * @param points Amount of points to add.
     */
    private void addToScore(int points) {
        int score = Integer.parseInt(spScore.getValue());
        score += points;
        if (score > Integer.parseInt(highscores.topScore.getValue())) {
            highscores.topScore.setValue(String.format("%05d", score));
        }
        spScore.setValue(String.format("%05d", score));
    }

    /**Frog controls*/
    private EventHandler<KeyEvent> keyEventEventHandler = event -> {
        if (!frogAlive && !gameOver )
            return;
        if (gameState == PLAYING) {
            if (event.getCode() == KeyCode.UP) {
                direction = UP;
                return;
            }
            if (event.getCode() == KeyCode.DOWN) {
                direction = DOWN;
                return;
            }
            if (event.getCode() == KeyCode.LEFT) {
                direction = LEFT;
                return;
            }
            if (event.getCode() == KeyCode.RIGHT) {
                direction = RIGHT;
                return;
            }
            if (event.getCode() == KeyCode.P) {
                togglePause();
            }
        }
    };

    /**
     * Adds the image where the frog's goals are.
     */
    private void addGoals() {
        ImageView allGoalsImageView = new ImageView(Objects.requireNonNull(getClass().getClassLoader().getResource(
                "Images/goals.png")).toString());
        allGoalsImageView.setFitWidth(playableWidth);
        allGoalsImageView.setFitHeight(laneHeight * 1.5);
        allGoalsImageView.setY(laneHeight * GOAL_LANE - laneHeight / 2);
        gameRoot.getChildren().add(allGoalsImageView);

        goals = new Goal[remainingGoals];
        for (int i = 0; i < goals.length; i++) {
            switch (i) {
                case 0:
                    goals[i] = new Goal(playableWidth / 37);
                    break;
                case 1:
                    goals[i] = new Goal(playableWidth / 4.1);
                    break;
                case 2:
                    goals[i] = new Goal(playableWidth / 2.18);
                    break;
                case 3:
                    goals[i] = new Goal(playableWidth / 1.47);
                    break;
                case 4:
                    goals[i] = new Goal(playableWidth / 1.12);
                    break;
            }
            gameRoot.getChildren().add(goals[i].getImageView());
        }
    }

    private void addPavements() {
        ImageView[] imageViews = new ImageView[2];
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView(Objects.requireNonNull(getClass().getClassLoader().getResource(
                    "Images/pavement.png")).toString());
            imageViews[i].setFitWidth(playableWidth);
            imageViews[i].setFitHeight(laneHeight);
            if (i == 0) {
                imageViews[i].setY(imageViews[i].getFitHeight() * BOTTOM_PAVEMENT_LANE);
            } else {
                imageViews[i].setY(imageViews[i].getFitHeight() * TOP_PAVEMENT_LANE);
            }
        }
        gameRoot.getChildren().addAll(imageViews);
    }

    /**
     * Adds the container of the messages that appear on the center of the screen.
     */
    private void addMessageTexts() {
        double messageFont = playableWidth / 32;
        //add text for layout measurements
        messageText = new Text("Insert Coin (Press 1)");
        messageText.setTextOrigin(VPos.TOP);
        messageText.setId("messageLabel");
        messageText.setFont(Font.font("", FontWeight.EXTRA_BOLD, messageFont));
        messageText.setFill(Color.RED);
        messageContainer = new StackPane(messageText);
        messageContainer.setStyle("-fx-background-color:black;");
        messageContainer.setVisible(false);
        gameRoot.getChildren().add(messageContainer);
    }

    private void addTimeBarAndText() {
        double timeFontSize = playableWidth / 24;
        Text timeText = new Text("TIME");
        timeText.setFont(Font.font("Tahoma", FontWeight.BOLD, timeFontSize));
        timeText.setFill(Color.YELLOW);
        timeText.setBoundsType(TextBoundsType.VISUAL);
        maxTextWidth = timeText.maxWidth(laneHeight);
        timeText.setX(playableWidth - maxTextWidth);
        timeText.setTextOrigin(VPos.TOP);
        timeText.setY(laneHeight * TIME_LANE + laneHeight / 2);
        minTextHeight = timeText.minHeight(maxTextWidth);
        timeBar.setHeight(minTextHeight);
        updateTimeBar(0);
        gameRoot.getChildren().addAll(timeText, timeBar);
    }

    private void addVisitedLanes() {
        visitedLanes = new Hashtable<>();
        visitedLanes.put(FIRST_WATER_LANE, false);
        visitedLanes.put(SECOND_WATER_LANE, false);
        visitedLanes.put(THIRD_WATER_LANE, false);
        visitedLanes.put(FOURTH_WATER_LANE, false);
        visitedLanes.put(FIFTH_WATER_LANE, false);
        visitedLanes.put(TOP_PAVEMENT_LANE, false);
        visitedLanes.put(FIRST_ROAD_LANE, false);
        visitedLanes.put(SECOND_ROAD_LANE, false);
        visitedLanes.put(THIRD_ROAD_LANE, false);
        visitedLanes.put(FOURTH_ROAD_LANE, false);
        visitedLanes.put(FIFTH_ROAD_LANE, false);
    }

    private void addMovableObjects() {
        allObjects = build(levelProperty.getValue(), velocity, gameRoot);
        allObjects.forEach((integer,list)-> {
            for (IMovableObject obj : list) {
                gameRoot.getChildren().add(obj.getMainGroup());
            }
        });
    }

    /**
     * Adds points for newly visited lanes, when the controllable frog stops jumping.
     */
    private EventHandler<ActionEvent> hopFinished = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            int frogLane = controllableFrog.getLane();
            if (frogLane != BOTTOM_PAVEMENT_LANE
                    && frogLane != GOAL_LANE
                    && !visitedLanes.get(frogLane)) {

                visitedLanes.replace(frogLane, true);
                addToScore(10);
            }
        }
    };

    /**
     * Game loop.
     */
    void start() {
        frameBefore = 0;
        at = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (frameBefore == 0) {
                    frameBefore = now;
                    return;
                }
                //timer
                long elapsedMils = (now - frameBefore) / 1_000_000;
                frameBefore = now;
                if (!paused) {
                    update(elapsedMils);
                    collisions();
                    respondToInput();
                }
            }
        };
        at.start();
    }

    private void update(long millis) {
        switch (gameState) {
            case PLAYING:
                if (!gameOver) {
                    updateThemeSong();
                    updateTimeBar(millis);
                    updateGoals(millis);
                }else {
                    if (continuable) {
                        addHighscore();
                        addContinueMessage();
                        updateContinue(millis);
                    }else {
                        gameState = HIGHSCORES;
                    }
                }
                break;
            case DEMO:
                demoTimer += millis;
                if (gameOver) {
                    continueGame();
                } else {
                    showMessage("Insert Coin (Press 1)");
                    updateDemo();
                }
                if (demoTimer > 5000) {
                    gameState = HIGHSCORES;
                    demoTimer = 0;
                }
                break;
            case HIGHSCORES:
                if (showHighscores()) {
                    demoTimer += millis;
                    if (demoTimer > 10000) {
                        demoTimer = 0;
                        gameState = DEMO;
                        removeHighScores();
                    }
                } else gameState = DEMO;
                break;
        }
        updateMovableObjects(millis);
        updateMessages(millis);
        updateRunRestart();
    }

    private boolean isHighScore(){
        if (highscoreChecked) return false;
        highscoreChecked = true;
        ArrayList<ScoreModel> highscoresList = highscores.getHighscoresList();
        if (highscoresList.isEmpty()) {
            highscoreIndex = 0;
            return true;
        }
        for (int i = 0; i < highscoresList.size(); i++) {
            ScoreModel sm = highscoresList.get(i);
            if (sm.getScore() < Integer.parseInt(spScore.getValue())) {
                highscoreIndex = i;
                return true;
            }
        }
        if (highscoresList.size() < 10) {
            highscoreIndex = highscoresList.size();
            return true;
        }
        return false;
    }

    private void addHighscore(){
        if (isHighScore()) {
            togglePause();
            Stage addStage = new Stage(StageStyle.UNDECORATED);
            addStage.initModality(Modality.APPLICATION_MODAL);
            int fontRatio = 30;
            Label l = new Label("Save Hi-Score !") {{
                setFont(Font.font(playableWidth / fontRatio * 2));
                setTextFill(Color.WHITE);
            }};
            Button y = new Button("Add") {{
                setFont(Font.font(playableWidth / fontRatio));
            }};
            Button n = new Button("Cancel") {{
                setFont(Font.font(playableWidth / fontRatio));
            }};
            TextField textField = new TextField(System.getProperty("user.name")){{
                setFont(Font.font(playableWidth / fontRatio));
            }};
            VBox v = new VBox(l, textField, y, n) {{
                setPadding(new Insets(20, 20, 20, 20));
                setSpacing(10);
                setAlignment(Pos.CENTER);
                setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
            }};
            Scene s = new Scene(v);
            addStage.setScene(s);

            y.setOnAction(event1 -> {
                String name = textField.getText();
                int score = Integer.parseInt(spScore.getValue());
                highscores.addHighscoreToDatabase(name, score, highscoreIndex);
                addStage.close();
                togglePause();
            });
            n.setOnAction(event1 -> {
                addStage.close();
                togglePause();
            });
            addStage.show();
            textField.requestFocus();
        }
    }

    /**
     * Continue countdown.
     */
    private void updateContinue(long millis){
        continueTimer -= millis;
        continueProperty.setValue("CONTINUE ? " + (continueTimer / 1000));
        if (continueTimer < 1) {
            continuable = false;
            spScore.setValue("00000");
            gameRoot.getChildren().remove(continueContainer);
        }
    }

    void continueGame(){
        if (gameState == PLAYING) {
            spScore.setValue("00000");
            gameRoot.getChildren().remove(continueContainer);
            continueTimer = 10000;
            highscoreChecked = false;
        }
        controllableFrog.revive(false, true);
        gameOver = false;
        restartRun();
        livesProperty.set(initialLives);
    }

    private void updateThemeSong(){
        if (nowPlaying == null) {
            nowPlaying = stageStart;
            nowPlaying.play();
        } else {
            if (!nowPlaying.isPlaying()) {
                if (nowPlaying == stageStart) {
                    nowPlaying = mainTheme;
                } else {
                    nowPlaying = stageStart;
                }
                nowPlaying.play();
            }
        }
    }

    /**
     * Respawns the controllableFrog when the death animation stops, when there are more lives or goals.
     */
    private void updateRunRestart() {
        if (gameOver) {
            //no more lives
            showMessage("GAME OVER");
        } else {
            if (remainingGoals == 0) {
                startNewLevel();
            }
            //waiting for death animation to stop
            if (!frogAlive) {
                restartRun();
            }
        }
    }

    private void addContinueMessage(){
        if (gameRoot.getChildren().contains(continueContainer))
            return;
        Text continueText = new Text();
        continueText.textProperty().bind(continueProperty);
        continueText.setTextOrigin(VPos.TOP);
        continueText.setFont(Font.font("", FontWeight.EXTRA_BOLD, playableWidth / 16));
        continueText.setFill(Color.RED);
        continueContainer = new StackPane(continueText);
        continueContainer.setTranslateX(playableWidth / 2 - continueText.getBoundsInParent().getWidth() / 2);
        continueContainer.setTranslateY(SECOND_WATER_LANE * laneHeight);
        continueContainer.setStyle("-fx-background-color:black;");
        gameRoot.getChildren().add(continueContainer);
    }

    private void startNewLevel(){
        levelProperty.set(levelProperty.getValue() + 1);
        for (Goal goal : goals) {
            goal.changeStateTo(EMPTY);
        }
        allObjects.forEach(((integer, list) -> {
            for (IMovableObject obj : list) {
                gameRoot.getChildren().remove(obj.getMainGroup());
            }
        }));
        addMovableObjects();
        int i = gameRoot.getChildren().indexOf(controllableFrog.getMainGroup());
        gameRoot.getChildren().get(i).toFront();
        remainingGoals = goals.length;
        maxTimeRemaining = 50;
    }

    /**
     * Hides the message in the center of the screen, once enough time has passed.
     * @param millis Amount of milliseconds passed from the previous frame.
     */
    private void updateMessages(long millis) {
        if (messageText.isVisible()) {
            messageTimer += millis;
            if (messageTimer > messageDuration) {
                messageContainer.setVisible(false);
                messageTimer = 0;
            }
        }
    }

    /**
     * Updates the goals, to add/remove flies and ?crocodiles?
     * Adds the fly to the furthest available goal.
     * @param millis Amount of milliseconds passed from the previous frame.
     */
    private void updateGoals(long millis) {
        setGoalToFly();
        setGoalToCrocodile();
        for (Goal g : goals) {
            g.update(millis);
        }
    }

    private boolean showHighscores(){
        highscoresRoot = highscores.getRoot();
        boolean exists = highscoresRoot != null;
        if (exists) {
            if (!root.getChildren().contains(highscoresRoot)) {
                root.getChildren().clear();
                root.getChildren().add(highscoresRoot);
            }
        }
        return exists;
    }

    private void removeHighScores(){
        if (highscoresRoot != null) {
            root.getChildren().clear();
            root.getChildren().add(gameRoot);
        }
    }

    /**
     * A minimal attempt to make the controllable frog move, without user input, during demo.
     */
    private void updateDemo(){
        demoFrameCounter++;
        if (demoFrameCounter < 20) {
            return;
        }
        demoFrameCounter = 0;
        ArrayList<FrogDirection> possibleMoves = new ArrayList<>();
        Random r = new Random();

        Bounds fBounds = controllableFrog.getMainGroup().getBoundsInParent();
        moveDemo(fBounds, possibleMoves);

        if (possibleMoves.size() > 0) {
            if (possibleMoves.contains(UP)) controllableFrog.move(UP);
            else controllableFrog.move(possibleMoves.get(r.nextInt(possibleMoves.size())));
        }
    }

    /**
     * A minimal attempt to make the controllable frog move, without user input, during demo.
     */
    private void moveDemo(Bounds fBounds, ArrayList<FrogDirection> possibleMoves){
        int froglane = controllableFrog.getLane();
        if (froglane == FIRST_WATER_LANE) {
            possibleMoves.add(UP);
            return;
        }
        //up
        if (froglane > TOP_PAVEMENT_LANE) {
            double diagonal = laneHeight / 2;
            try {
                boolean add = true;
                ArrayList<IMovableObject> list = allObjects.get(froglane - 1);
                for (IMovableObject obj : list) {
                    if (obj.canKill() &&
                            (pointCollision(fBounds.getMinX(), fBounds.getMaxX(), obj)
                                    || pointCollision(fBounds.getMinX() - diagonal, fBounds.getMaxX() - diagonal, obj)
                                    || pointCollision(fBounds.getMinX() + diagonal, fBounds.getMaxX() + diagonal, obj)
                            )
                    ) {
                        add = false;
                        break;
                    }
                }
                if (add) possibleMoves.add(UP);
            } catch (NullPointerException ignored) {}
            //down
            try {
                boolean add = true;
                ArrayList<IMovableObject> list = allObjects.get(froglane + 1);
                for (IMovableObject obj : list) {
                    if (obj.canKill() && (
                            pointCollision(fBounds.getMinX(), fBounds.getMaxX(), obj)
                                    || pointCollision(fBounds.getMinX() - diagonal, fBounds.getMaxX() - diagonal, obj)
                                    || pointCollision(fBounds.getMinX() + diagonal, fBounds.getMaxX() + diagonal, obj)
                    )) {
                        add = false;
                        break;
                    }
                }
                if (add) possibleMoves.add(DOWN);
            } catch (NullPointerException ignored) {
                possibleMoves.add(DOWN);
            }
        }
        if (froglane > FIFTH_WATER_LANE) {
            //left
            try {
                boolean add = true;
                ArrayList<IMovableObject> list = allObjects.get(froglane);
                for (IMovableObject obj : list) {
                    if (obj.canKill() && pointCollision(fBounds.getMinX()-laneHeight, fBounds.getMaxX()-laneHeight, obj)) {
                        add = false;
                        break;
                    }
                }
                if (add) possibleMoves.add(LEFT);
            }catch (NullPointerException ignored){}
            //right
            try {
                boolean add = true;
                ArrayList<IMovableObject> list = allObjects.get(froglane);
                for (IMovableObject obj : list) {
                    if (obj.canKill() && pointCollision(fBounds.getMinX()+laneHeight, fBounds.getMaxX()+laneHeight, obj)) {
                        add = false;
                        break;
                    }
                }
                if (add) possibleMoves.add(RIGHT);
            }catch (NullPointerException ignored){}
        }

        if (froglane <= TOP_PAVEMENT_LANE) {
            //water up
            try {
                ArrayList<IMovableObject> list = allObjects.get(froglane - 1);
                for (IMovableObject obj : list) {
                    if (obj.canLand() && fullContactCollision(fBounds.getMinX(), fBounds.getMaxX(), obj)) {
                        possibleMoves.add(UP);
                        break;
                    }
                }
            } catch (NullPointerException ignored) {}
        }
    }

    /**
     * Sets the state of the furthest available goal, to contain a fly.
     */
    private void setGoalToFly() {
        if (!flyAppeared && controllableFrog.getLane()==THIRD_WATER_LANE) {
            flyAppeared = true;
            double goalDX = 0;
            int goalI = -1;
            for (int i = 0; i < goals.length; i++) {
                Goal g = goals[i];
                GoalState state = g.getState();
                if (state == COMPLETE || state == CROCODILE_HALF
                        || state == CROCODILE)
                    continue;
                double dX = Math.abs(g.getX() - controllableFrog.getX());
                if (dX > goalDX) {
                    goalDX = dX;
                    goalI = i;
                }
            }
            if (goalI >= 0) {
                goals[goalI].changeStateTo(FLY);
            }
        }
    }

    /**
     * Sets the closest available goal to contain a crocodile.
     */
    private void setGoalToCrocodile(){
        if (crocodileAppeared) return;
        if (levelProperty.getValue() > 1 && !crocodileAppeared && controllableFrog.getLane() == SECOND_WATER_LANE) {
            crocodileAppeared = true;
            double goalDX = Double.MAX_VALUE;
            int goalI = -1;
            for (int i = 0; i < goals.length; i++) {
                Goal g = goals[i];
                if (g.getState() == COMPLETE || g.getState() == FLY)
                    continue;
                double dX = Math.abs(g.getX() - controllableFrog.getX());
                if (dX < goalDX) {
                    goalDX = dX;
                    goalI = i;
                }
            }
            if (goalI >= 0) {
                goals[goalI].changeStateTo(CROCODILE_HALF);
            }
        }
    }

    /**
     * Updates the rectangle that represents the remaining time.
     * @param millis Amount of milliseconds passed from the previous frame.
     */
    private void updateTimeBar(long millis) {
        currentTimeRemaing -= millis / 1000.0;
        final double SPACING = playableWidth / 85.0;
        double maxTimebarWidth = playableWidth / 1.5;
        timeBar.setWidth(currentTimeRemaing * maxTimebarWidth / maxTimeRemaining - SPACING);
        timeBar.setX(playableWidth - timeBar.getWidth() - maxTextWidth - SPACING);
        timeBar.setY(playableHeight - minTextHeight);

        //add changes to fill when time is running out
        if (currentTimeRemaing > 5) {
            timeBar.setFill(Color.LIME);
        } else {
            if (!timeClip.isPlaying()) playSound(TIME);
            timeBar.setFill(Color.RED);
        }

        if (currentTimeRemaing <= 0) {
            //time out
            killFrog(true);
        }
    }

    private void updateMovableObjects(long elapsedMils) {
        allObjects.forEach(((integer, list) -> {
            for (IMovableObject obj : list) {
                obj.update(elapsedMils);
            }
        }));
        //call for update when the controllable frog mounts a water object,
        //to update its x along with the water object
        if (updateFrogsX && controllableFrogsMount != null) {
            updateFrogsX = false;
            controllableFrog.updateX(controllableFrogsMount.getDX());
            controllableFrogsMount = null;
        }
    }

    private void collisions() {
        //no checks while the controllable frog is moving
        if (controllableFrog.getHopAnimation().getStatus() == Animation.Status.RUNNING)
            return;
        int frogLane = controllableFrog.getLane();
        if (frogAlive && frogLane != BOTTOM_PAVEMENT_LANE) {
            Bounds fBounds = controllableFrog.getMainGroup().getBoundsInParent();
            double fMinX = fBounds.getMinX();
            double fMaxX = fBounds.getMaxX();

            if (frogLane == GOAL_LANE) {
                for (Goal g : goals) {
                    if (pointCollision(fMinX, fMaxX, g)) {
                        landedInGoal(g);
                        return;
                    }
                }
                //crashed in goal section
                killFrog(true);
            } else {
                boolean frogDrowned = true;
                ArrayList<IMovableObject> list = allObjects.get(frogLane);
                for (IMovableObject obj : list) {
                    if (frogLane <= FIFTH_WATER_LANE) {
                        if (obj.canLand()) {
                            if (fullContactCollision(fMinX, fMaxX, obj)) {
                                updateFrogsX = true;
                                controllableFrogsMount = obj;
                                frogDrowned = false;
                            }
                        }
                    }
                    if (obj.canKill()) {
                        if (pointCollision(fMinX, fMaxX, obj)) {
                            killFrog(true);
                            return;
                        }
                    }
                    if (obj instanceof Frog) {
                        UncontrollableFrog uf = (UncontrollableFrog) obj;
                        if (!uf.isBound() && pointCollision(fMinX, fMaxX, obj)) {
                            if (uf.canBind()) {
                                uf.setBindWith(controllableFrog);
                            }
                        }
                    }
                }
                if (frogLane <= FIFTH_WATER_LANE && frogDrowned)
                    killFrog(false);
            }
        }
    }

    private void landedInGoal(Goal g) {
        GoalState state = g.goalReached();
        if (state != CROCODILE && state != COMPLETE) {
            int newScore = 50;
            if (state == FLY) {
                //add fly extra points
                newScore += 200;
            }
            if (controllableFrog.isBound()) {
                newScore += 200;
            }
            newScore += 10 * (int) currentTimeRemaing;
            showMessage("TIME " + (int) currentTimeRemaing);
            addToScore(newScore);
            remainingGoals--;
            if (remainingGoals > 2) maxTimeRemaining = 50;
            else maxTimeRemaining = 40;
            controllableFrog.setToInitialPosition();
            restartRun();
        }else
            killFrog(true);
    }

    /**
     * @return True if frog 'touches' another object.
     */
    private boolean pointCollision(double frogMinX, double frogMaxX, IMovableObject obj) {
        Bounds oBounds = obj.getCollidingBounds();
        double oMinX = oBounds.getMinX();
        double oMaxX = oBounds.getMaxX();
        return ((frogMinX >= oMinX && frogMinX <= oMaxX)
                || (frogMaxX >= oMinX && frogMaxX <= oMaxX));
    }

    /**
     * @return True if frog 'touches' a goal.
     */
    private boolean pointCollision(double frogMinX, double frogMaxX, Goal obj) {
        Bounds oBounds = obj.getImageView().getBoundsInParent();
        double oMinX = oBounds.getMinX();
        double oMaxX = oBounds.getMaxX();
        return ((frogMinX >= oMinX && frogMinX <= oMaxX)
                || (frogMaxX >= oMinX && frogMaxX <= oMaxX));
    }

    /**
     * @return True if frog is 'immersed' in another object.
     */
    private boolean fullContactCollision(double frogMinX, double frogMaxX, IMovableObject obj) {
        Bounds oBounds = obj.getCollidingBounds();
        double oMinX = oBounds.getMinX();
        double oMaxX = oBounds.getMaxX();
        return frogMinX >= oMinX && frogMaxX <= oMaxX;
    }

    private void killFrog(boolean collided) {
        //if statement to prevent multiple calls by updateTimebar() when the time runs out
        if (frogAlive) {
            frogAlive = false;
            if (livesProperty.getValue() == 1) {
                gameOver = true;
                if (collided) {
                    controllableFrog.animateDeath(false);
                    playSound(DROWNED);
                }
                else {
                    controllableFrog.animateDrowning(false);
                    playSound(COLLIDED);
                }
            } else {
                if (collided) {
                    controllableFrog.animateDeath(true);
                    playSound(COLLIDED);
                }
                else {
                    controllableFrog.animateDrowning(true);
                    playSound(DROWNED);
                }
            }
        }
    }

    private void respondToInput() {
        if (frogAlive && direction != null) {
            if (controllableFrog.move((direction))){
                playSound(HOP);
            }
            direction = null;
        }
    }

    /**
     * Updates that happen when the controllableFrog respawns after death, or successful landing.
     */
    private void restartRun() {
        if (!controllableFrog.isDead()) {   //true when the death animation stops
            if (!frogAlive) {   //prevents reducing one life if the method is called when reaching a goal
                livesProperty.set(livesProperty.getValue() - 1);
            }
            frogAlive = true;
            visitedLanes.forEach((integer, visited)
                    -> visitedLanes.replace(integer, false));
            currentTimeRemaing = maxTimeRemaining;
            flyAppeared = false;
            crocodileAppeared = false;
            direction = null;
            for (Goal g : goals) {
                if (g.getState() == CROCODILE_HALF || g.getState() == CROCODILE || g.getState() == FLY) {
                    g.changeStateTo(EMPTY);
                }
            }
        }
    }

    private void showMessage(String message) {
        if (messageContainer.isVisible()) return;
        messageText.setText(message);
        messageContainer.setTranslateX(playableWidth / 2 - messageText.getBoundsInParent().getWidth() / 2);
        messageContainer.setTranslateY(TOP_PAVEMENT_LANE * laneHeight + laneHeight - messageText.getBoundsInParent().getHeight());
        messageContainer.setVisible(true);
    }

    boolean isPlayable() {
        return gameState == PLAYING;
    }

    boolean isGameOver() {
        return gameOver;
    }

    boolean isContinuable() {
        return continuable;
    }

    void togglePause(){
        paused = !paused;
        if (nowPlaying != null && paused) {
//            if (paused) nowPlaying.stop();
            nowPlaying.stop();
        }
    }

    /**
     * Stops the animation timer.
     */
    void stop(){
        if (nowPlaying!=null)
            nowPlaying.stop();
        if (at !=null)
            at.stop();
    }

    private void playSound(SoundType type) {
        if (gameState !=PLAYING) return;
        //No good sounds were found for reaching the goal succesfully
        switch (type) {
            case COLLIDED:
                collidedClip.play();
                break;
            case DROWNED:
                drownClip.play();
                break;
            case HOP:
                hopClip.play();
                break;
            case TIME:
                timeClip.play();
                break;
        }
    }
}
