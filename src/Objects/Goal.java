package Objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

import static Main.Game.*;
import static Objects.LaneBuilder.*;

public class Goal {
    private static Image successfulGoalImage = new Image(Objects.requireNonNull(Goal.class.getClassLoader().getResource(
            "Images/goals_image.png")).toString());
    private static Image flyImage = new Image(Objects.requireNonNull(Goal.class.getClassLoader().getResource(
            "Images/fly.png")).toString());
    private static Image crocHalfImage = new Image(Objects.requireNonNull(Goal.class.getClassLoader().getResource(
            "Images/croc_goal_half.png")).toString());
    private static Image crocFullImage = new Image(Objects.requireNonNull(Goal.class.getClassLoader().getResource(
            "Images/croc_goal_full.png")).toString());
    private ImageView iv = new ImageView();

    private GoalState state = GoalState.EMPTY;
    private long timer = 0;
    private double x;

    public Goal(double x) {
        this.x = x;
        iv.setX(x);
        iv.setY(laneHeight * GOAL_LANE);
        iv.setFitWidth(goalWidth);
        iv.setFitHeight(laneHeight);
    }

    public ImageView getImageView() {
        return iv;
    }

    public GoalState goalReached(){
        GoalState currentState = state;
        if (state == GoalState.EMPTY || state == GoalState.FLY || state == GoalState.CROCODILE_HALF) {
            iv.setImage(successfulGoalImage);
            changeStateTo(GoalState.COMPLETE);
        }
        return currentState;
    }

    public void changeStateTo(GoalState newState) {
        switch (newState) {
            case EMPTY:
                iv.setImage(null);
                break;
            case FLY:
                iv.setImage(flyImage);
                break;
            case CROCODILE_HALF:
                iv.setImage(crocHalfImage);
                break;
            case CROCODILE:
                iv.setImage(crocFullImage);
                break;
            //complete is handled by goalReached()
        }
        state = newState;
        timer = 0;
    }

    public void update(long millis){
        timer += millis;
        switch (state) {
            case EMPTY:
                break;
            case FLY:
                if (timer > flyDuration) {
                    iv.setImage(null);
                    changeStateTo(GoalState.EMPTY);
                }
                break;
            case CROCODILE_HALF:
                if (timer > 1000) {
                    changeStateTo(GoalState.CROCODILE);
                }
                break;
            case CROCODILE:
                if (timer > 2000) {
                    changeStateTo(GoalState.EMPTY);
                }
                break;
        }
    }

    public double getX() {
        return x;
    }

    public GoalState getState() {
        return state;
    }
}
