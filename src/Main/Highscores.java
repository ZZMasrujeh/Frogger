package Main;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

class Highscores {
    private StackPane highscoresPane;
    private static ArrayList<ScoreModel> highscoresList = new ArrayList<>();
    private static final int maxScore = 99999;
    static final int maxRecords = 10;
    private static final Insets gridPaneInsets = new Insets(30, 20, 20, 20);
    SimpleStringProperty topScore = new SimpleStringProperty("00000");

    private HTTP_Database request;

    Highscores() {
        request = new HTTP_Database();

        //Retieves the hisghscores from the database and updates the gridpane if there a successful result.
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<ScoreModel> result = request.getHighscores();
                    if (result != null) {
                        highscoresList = result;
                        topScore.setValue(String.format("%05d",
                                + highscoresList.get(0).getScore() > maxScore ? maxScore : highscoresList.get(0).getScore()
                        ));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void draw() {
        highscoresPane = new StackPane();
        highscoresPane.setMinWidth(Main.playingWidth);
        highscoresPane.setMaxWidth(Main.playingWidth);
        highscoresPane.setMinHeight(Main.playingHeight);
        highscoresPane.setMaxHeight(Main.playingHeight);

        highscoresPane.setStyle("-fx-background-color:#000042;");
        GridPane g = new GridPane();
        g.setPadding(gridPaneInsets);
        g.setVgap(Main.playingWidth / 96);
        g.setHgap(Main.playingWidth / 48);
        Text hText = new Text("HIGHSCORES") {{
            setFont(Font.font(Main.playingWidth / 12));
            setFill(Color.WHITE);
        }};
        GridPane.setColumnSpan(hText, 2);
        GridPane.setRowIndex(hText, 0);
        g.getChildren().add(hText);
        g.setAlignment(Pos.TOP_CENTER);
        double fontSize = Main.playingWidth / 19.2;
        int row = 1;
        for (int i = 0; i < highscoresList.size(); i++) {
            ScoreModel sm = highscoresList.get(i);
            Text rankName = new Text(i + 1 + " " + sm.getName() + " ") {{
                setFill(Color.WHITE);
                setFont(Font.font(fontSize));
            }};
            Text score = new Text(String.format("%05d",
                    sm.getScore() > maxScore ? maxScore : sm.getScore())) {{
                setFill(Color.WHITE);
                setFont(Font.font(fontSize));
            }};
            GridPane.setColumnIndex(rankName, 0);
            GridPane.setColumnIndex(score, 1);
            GridPane.setRowIndex(rankName, row);
            GridPane.setRowIndex(score, row);
            g.getChildren().addAll(rankName, score);
            row++;

            if (i == maxRecords - 1) break;
        }
        highscoresPane.getChildren().add(g);
    }

    StackPane getRoot() {
        if (highscoresList.isEmpty())
            return null;
        draw();
        return highscoresPane;
    }

    ArrayList<ScoreModel> getHighscoresList() {
        return highscoresList;
    }

    void addHighscoreToDatabase(String name, int score, int highscoreIndex) {
        request.post(name, score);
        highscoresList.add(highscoreIndex, new ScoreModel(name, score));
        changeTopScore(score);
    }

    /**
     * Changes the highscore if it is not the highest anymore.
     * @param score The new score.
     */
    private void changeTopScore(int score) {
        if (score > Integer.parseInt(topScore.getValue())) {
            topScore.set("" + score);
        }
    }
}
