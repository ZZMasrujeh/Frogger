package Objects;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static Main.Game.*;

public class UncontrollableFrog extends Frog implements IMovableObject {

    private Node logGroup;
    private LogLarge log;
    private long hopTimer = 0;
    private double totalWidth;

    UncontrollableFrog(double x, int lane, Node logGroup, LogLarge log) {
        super(x, lane, Color.rgb(0, 0, 255), Color.WHITE);
        this.logGroup = logGroup;
        this.log = log;

        direction = FrogDirection.RIGHT;
        g.setRotate(90);
        setHopKeyFrame();
        totalWidth = g.getBoundsInParent().getWidth();
    }

    @Override
    public double getWidth() {
        return totalWidth;
    }

    public void setBindWith(ControllableFrog controllableFrog) {
        bound = true;
        hop.stop();
        log.dismount();
        controllableFrog.bindWith(this);
        g.setVisible(false);
    }

    void unBind(){
        bound = false;
        log.mount(this);
    }

    public boolean canBind(){ return g.isVisible(); }

    private void setHopKeyFrame() {
        hopKeyFrame = new KeyFrame(Duration.millis(totalHopDuration / totalHopFrames),event -> {
            currentFrame++;
            legs.setScaleY(1.5);
            legs.setScaleX(1.2);
            switch (direction) {
                case LEFT:
                    g.setRotate(-90);
                    g.setTranslateX(g.getTranslateX() + log.getDX() - hopDistance / totalHopFrames);
                    break;
                case RIGHT:
                    g.setRotate(90);
                    g.setTranslateX(g.getTranslateX() + log.getDX() + hopDistance / totalHopFrames);
                    break;
            }
            if (currentFrame == totalHopFrames) {
                legs.setScaleY(1);
                legs.setScaleX(1);
                if (logGroup.getBoundsInParent().getMaxX() <= g.getBoundsInParent().getMaxX() + hopDistance) {
                    direction = FrogDirection.LEFT;
                } else if (logGroup.getBoundsInParent().getMinX() >= g.getBoundsInParent().getMinX() - hopDistance) {
                    direction = FrogDirection.RIGHT;
                }
                hop.stop();
            }
        });
    }

    @Override
    public void move(double dX) {
        g.setTranslateX(g.getTranslateX() + dX);
    }

    @Override
    public void update(long elapsedMils) {
        if (!bound) {
            hopTimer += elapsedMils;
            if (hopTimer > 1500) {
                hopTimer = 0;
                if (hop.getStatus() != Animation.Status.RUNNING) {
                    hopDistance = laneHeight;
                    hopAnimation();
                }
            } else {
                move(log.getDX());
            }
        }
    }

    @Override
    public void setShapeArray() {
        //set by the super constructor
    }

    @Override
    public double getRatio() {
        return 0;
    }


    @Override
    public double getDX() {
        return 0;
    }

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
