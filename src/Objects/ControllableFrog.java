package Objects;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;

import static Main.Game.laneHeight;
import static Main.Game.playableWidth;
import static Objects.LaneBuilder.BOTTOM_PAVEMENT_LANE;
import static Objects.LaneBuilder.GOAL_LANE;

public class ControllableFrog extends Frog {
    private Timeline deathAnimation;
    private Timeline drowningAnimation;
    private Group deathGroup = new Group();
    private boolean dead = false;
    private UncontrollableFrog uf;
    private Group ufGroup;

    public ControllableFrog(double x, int lane) {
        super(x, lane, Color.rgb(0, 255, 0),Color.BLACK);
        setHopKeyFrame();
        direction = FrogDirection.UP;
    }

    public Timeline getHopAnimation(){
        return this.hop;
    }

    private void setHopKeyFrame() {
        hopKeyFrame = new KeyFrame(Duration.millis(totalHopDuration / totalHopFrames), event -> {
            currentFrame++;
            legs.setScaleY(1.5);
            legs.setScaleX(1.2);
            double dHopDistance = hopDistance / totalHopFrames;
            switch (direction) {
                case UP:
                    g.setTranslateY(g.getTranslateY() - dHopDistance);
                    g.setRotate(0);
                    y -= dHopDistance;
                    break;
                case LEFT:
                    g.setTranslateX(g.getTranslateX() - dHopDistance);
                    g.setRotate(-90);
                    x -= dHopDistance;
                    break;
                case RIGHT:
                    g.setTranslateX(g.getTranslateX() + dHopDistance);
                    g.setRotate(90);
                    x += dHopDistance;
                    break;
                case DOWN:
                    g.setTranslateY(g.getTranslateY() + dHopDistance);
                    g.setRotate(180);
                    y += dHopDistance;
                    break;
            }
            if (currentFrame == totalHopFrames) {
                //animation ended
                legs.setScaleY(1);
                legs.setScaleX(1);
                switch (direction) {
                    case UP:
                        lane--;
                        break;
                    case DOWN:
                        lane++;
                        break;
                }
            }
        });
    }

    public void updateX(double dX){
        this.x += dX;
        g.setTranslateX(g.getTranslateX() + dX);
    }

    public boolean isDead() {
        return dead;
    }

    public int getLane() {
        return lane;
    }

    public boolean isBound(){return bound;}

    public boolean move(FrogDirection direction) {
        //prevents hop animation when the frog is dead
        if (direction == null || dead)
            return false;
        //prevents hop animation when it is already in progress
        if (hop.getStatus() == Animation.Status.RUNNING) {
            return false;
        }
        this.direction = direction;
        hopDistance = laneHeight;

        if (lane == GOAL_LANE
                || (x < 0 && direction == FrogDirection.LEFT)
                || (x > playableWidth && direction == FrogDirection.RIGHT)
                || (lane == BOTTOM_PAVEMENT_LANE && direction == FrogDirection.DOWN)) {

            return false;
        }
        hopAnimation();
        return true;
    }

    /**
     * Sets a group that will replace the frog and be used to animate death.
     * @return A sub-group that represents the body of the group.
     */
    private Group setDeathGroup() {
        deathGroup.setTranslateX(-g.getTranslateX());
        deathGroup.setTranslateY(-g.getTranslateY());
        Ellipse e = new Ellipse(x, y, WIDTH/3.84, WIDTH/3.84);
        e.setFill(Color.PURPLE);
        e.setScaleY(2);
        e.setScaleX(1.5);

        Eye leftEye = new Eye(eyeRotation);
        Eye rightEye = new Eye(-eyeRotation);
        Group bodyGroup = new Group(e,leftEye.getEyeGroup(),rightEye.getEyeGroup());

        int boneRotation = 55;
        Bone bone1 = new Bone(-boneRotation);
        Bone bone2 = new Bone(boneRotation);
        deathGroup.getChildren().addAll(bone1.getBoneGroup(),bone2.getBoneGroup(),bodyGroup);

        g.getChildren().removeAll(shapes);
        g.getChildren().add(deathGroup);

        orientation = 0;
        g.setRotate(orientation);

        return bodyGroup;
    }

    /**
     * Sets a group that will replace the frog and be used to animate drowning.
     * @return A sub-group that represents the body of the group.
     */
    private Group setDrowningGroup(){
        deathGroup.setTranslateX(-g.getTranslateX());
        deathGroup.setTranslateY(-g.getTranslateY());
        Ellipse e = new Ellipse(x, y, WIDTH/3.84, WIDTH/2.56);
        e.setFill(Color.PURPLE);

        Group bodyGroup = new Group(e);

        Circle c1 = new Circle(x, y, WIDTH / 1.92, Color.TRANSPARENT);
        c1.setStroke(Color.WHITE);
        c1.getStrokeDashArray().addAll(2d, 5d, 10d);
        Circle c2 = new Circle(x, y, WIDTH / 1.28, Color.TRANSPARENT);
        c2.setStroke(Color.WHITE);
        c2.getStrokeDashArray().addAll(2d, 5d, 10d);

        deathGroup.getChildren().addAll(bodyGroup,c1,c2);

        g.getChildren().removeAll(shapes);
        g.getChildren().add(deathGroup);

        orientation = 0;
        g.setRotate(orientation);

        return bodyGroup;
    }

    public void animateDrowning(boolean respawn){
        if (drowningAnimation != null)return;
        hop.stop();
        g.setRotate(0);

        drowningAnimation = new Timeline();
        dead = true;
        Group bodyGroup = setDrowningGroup();
        final int totalFrames = 10;
        final int[] currentFrame = {0};
        double scaleD = 0.05;
        KeyFrame keyFrame = new KeyFrame(Duration.millis(100), event -> {
            currentFrame[0]++;
            if (currentFrame[0] == totalFrames - 1) {
                deathGroup.getChildren().remove(bodyGroup);
            }
            deathGroup.setScaleX(deathGroup.getScaleX()+scaleD);
            deathGroup.setScaleY(deathGroup.getScaleY()+scaleD);
            bodyGroup.setScaleX(bodyGroup.getScaleX()-2*scaleD);
            bodyGroup.setScaleY(bodyGroup.getScaleY()-2*scaleD);

            if (currentFrame[0] == totalFrames) {
                revive(false,respawn);
            }
        });

        drowningAnimation.getKeyFrames().add(keyFrame);
        drowningAnimation.setCycleCount(totalFrames);
        drowningAnimation.play();
    }

    public void animateDeath(boolean respawn){
        if (deathAnimation != null) return;
        hop.stop();
        g.setRotate(0);

        deathAnimation = new Timeline();
        dead = true;

        Group bodyGroup = setDeathGroup();

        double scaleD = 0.05;
        final int totalFrames = 10;
        final int[] currentFrame = {0};
        KeyFrame keyFrame = new KeyFrame(Duration.millis(100), event -> {
            currentFrame[0]++;
            if (currentFrame[0] == totalFrames-2) {
                deathGroup.getChildren().remove(bodyGroup);
            }
            deathGroup.setScaleX(deathGroup.getScaleX() + scaleD);
            deathGroup.setScaleY(deathGroup.getScaleY() + scaleD);
            if (currentFrame[0] == totalFrames) {
                revive(true,respawn);
            }
        });

        deathAnimation.getKeyFrames().add(keyFrame);
        deathAnimation.setCycleCount(totalFrames);
        deathAnimation.play();
    }

    /**
     * Stops animations and resets the frog's drawing.
     * @param deathAnimation whether called by deathAnimation().
     */
    public void revive(boolean deathAnimation, boolean respawn) {
        deathGroup.getChildren().clear();
        deathGroup.setScaleX(1);
        deathGroup.setScaleY(1);
        if (respawn) {
            g.getChildren().clear();
            setToInitialPosition();
            g.getChildren().addAll(shapes);
            dead = false;
        }
        if (deathAnimation) {
            this.deathAnimation = null;
        } else {
            drowningAnimation = null;
        }
    }

    /**
     * Resets the frog to its original position, after reaching a goal or death.
     */
    public void setToInitialPosition(){
        g.setTranslateX(0);
        g.setTranslateY(0);
        g.setRotate(0);
        x = initialX;
        lane = BOTTOM_PAVEMENT_LANE;
        y = laneHeight * lane + laneHeight / 2;

        if (bound) {
            bound = false;
            uf.unBind();
            uf.g.getChildren().addAll(ufGroup.getChildren());
            g.getChildren().remove(ufGroup);
            ufGroup = null;
            uf = null;
        }
    }

    void bindWith(UncontrollableFrog uf) {
        bound = true;

        ufGroup = new Group();
        ufGroup.getChildren().addAll(uf.getMainGroup().getChildren());
        uf.g.getChildren().clear();
        ufGroup.setTranslateY(-g.getTranslateY());
        ufGroup.setTranslateX(-uf.initialX + this.initialX);
        ufGroup.setScaleX(0.75);
        ufGroup.setScaleY(0.75);

        g.getChildren().add(ufGroup);
        this.uf = uf;
    }
}
