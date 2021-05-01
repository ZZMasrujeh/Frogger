package Objects;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

import static Main.Game.*;

public class Crocodile extends LogMedium {
    private enum CrocodileState {
        MOUTH_OPEN,MOUTH_CLOSED
    }
    private ImageView iv;
    private static Image mouthClosedImage = new Image(Objects.requireNonNull(Goal.class.getClassLoader().getResource(
            "Images/croc_swim_closed.png")).toString());
    private static Image mouthOpenimage = new Image(Objects.requireNonNull(Goal.class.getClassLoader().getResource(
            "Images/croc_swim_open.png")).toString());

    private Group collidingGroup = new Group();
    private CrocodileState mouthState = CrocodileState.MOUTH_OPEN;
    private boolean landingRequested = false;
    Crocodile(double x, int lane, double movableV) {
        super(x, lane, movableV);
    }

    private Group head;
    @Override
    public void setShapeArray() {
        shapes = new Node[2];
    }

    @Override
    public void drawShapes() {
        iv = new ImageView();
        double WIDTH = playableWidth / getRatio();
        double HEIGHT = laneHeight;
        iv.setFitHeight(HEIGHT);
        iv.setFitWidth(WIDTH);
        iv.setX(x - WIDTH / 2);
        iv.setY(laneHeight * lane);
        iv.setImage(mouthOpenimage);
        shapes[0] = iv;
        Rectangle r = new Rectangle(x + WIDTH * 0.25, laneHeight*lane, WIDTH * 0.25, HEIGHT);
        r.setFill(Color.TRANSPARENT);
        head = new Group(r);
        shapes[1] = head;
    }

    private long timer = 0;

    @Override
    public void update(long elapsedMils) {
        super.update(elapsedMils);
        collidingGroup.setTranslateX(g.getTranslateX());
        timer += elapsedMils;
        if (timer > 2000) {
            timer = 0;
            switch (mouthState) {
                case MOUTH_OPEN:
                    mouthState = CrocodileState.MOUTH_CLOSED;
                    iv.setImage(mouthClosedImage);
                    break;
                case MOUTH_CLOSED:
                    mouthState = CrocodileState.MOUTH_OPEN;
                    iv.setImage(mouthOpenimage);
                    break;
            }
        }
    }

    @Override
    public boolean canKill() {
        return mouthState == CrocodileState.MOUTH_OPEN;
    }

    @Override
    public boolean canLand() {
        landingRequested = true;
        return true;
    }

    /**
     * Called twice per update, if the game checks for killable collisions, the head group is returned,
     * otherwise the full group.
     */
    @Override
    public Bounds getCollidingBounds() {
        if (landingRequested){
            landingRequested = false;
            return g.getBoundsInParent();
        }
        else
            return g.localToParent(head.getBoundsInLocal());
    }
}
