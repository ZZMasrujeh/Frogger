package Objects;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;

import static Main.Game.laneHeight;
import static Main.Game.playableWidth;

public class MovableObject_OneWay implements IMovableObject {
    double x;
    double y;
    int lane;
    Node[] shapes;
    Group g;
    ObjectState state = ObjectState.MOVING;

    private double velocity;
    private double dX;
    private double totalWidth;

    MovableObject_OneWay(double x, int lane, double velocity) {
        this.lane = lane;
        this.x = x;
        this.y = laneHeight * lane + laneHeight / 2;
        this.velocity = velocity;
        g = new Group();
        setShapeArray();
        drawShapes();
        g.getChildren().addAll(shapes);
        totalWidth = g.getBoundsInParent().getWidth();
    }

    @Override
    public double getWidth() {
        return totalWidth;
    }

    @Override
    public Group getMainGroup() {
        return g;
    }

    @Override
    public void move(double dX) {
        this.dX = dX;
        g.setTranslateX(g.getTranslateX() + dX);
    }

    @Override
    public void update(long elapsedMils) {
        Bounds b = g.getBoundsInParent();
        switch (state) {
            case MOVING:
                move(velocity * elapsedMils / 1000.0);
                if (!(b.getMaxX() > 0) || !(b.getMinX() < playableWidth)) {
                    state = ObjectState.RESETTING;
                }
                break;
            case RESETTING:
                state = ObjectState.MOVING;
                if (dX > 0) {
                    //To Right
                    g.setTranslateX(-playableWidth - totalWidth + g.getTranslateX());
                } else if (dX < 0) {
                    //To Left
                    g.setTranslateX(playableWidth + totalWidth + g.getTranslateX());
                }
                move(2*velocity * elapsedMils / 1000.0);
                break;
        }
    }

    @Override
    public void drawShapes() {
        //each subclass will draw its own
    }

    @Override
    public void setShapeArray() {
        //each subclass will set its own
    }

    @Override
    public double getRatio() {
        return 1;
    }

    @Override
    public double getDX() {return dX;}

    @Override
    public boolean canKill() {
        return false;
    }

    @Override
    public boolean canLand() {
        return false;
    }

    @Override
    public Bounds getCollidingBounds() {
        return g.getBoundsInParent();
    }
}
