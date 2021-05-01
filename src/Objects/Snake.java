package Objects;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import static Main.Game.laneHeight;
import static Main.Game.playableWidth;

public class Snake implements IMovableObject {
    private final int GAME_TO_SNAKE_RATIO = 10;

    private long upDownTimer = 0;
    private double velocity;
    private boolean right = true;
    private Node parent;
    private IMovableObject objectMounting;
    private Group g;
    private Node[] shapes;
    private double x;
    private double y;
    private Group head;
    private double totalWidth;

    Snake(double x, int lane, double velocity, Node parent, IMovableObject objectMounting) {
        this.x = x;
        this.y = laneHeight * lane + laneHeight / 2;
        this.parent = parent;
        this.velocity = velocity;
        this.objectMounting = objectMounting;
        setShapeArray();
        drawShapes();
        g = new Group(shapes);
        totalWidth = g.getBoundsInParent().getWidth();
    }

    @Override
    public double getWidth() {
        return totalWidth;
    }

    @Override
    public Bounds getCollidingBounds() {
        return g.localToParent(head.getBoundsInLocal());
    }

    @Override
    public Group getMainGroup() { return g; }

    @Override
    public void move(double dX) { g.setTranslateX(g.getTranslateX() + dX); }

    @Override
    public void update(long elapsedMils) {
        if (objectMounting == null) {
            moveOnRoad(elapsedMils);
        }else {
            moveWhileMounting(elapsedMils);
        }
        upDownTimer += elapsedMils;
        if (upDownTimer > 500) {
            upDownTimer = 0;
            if (g.getScaleY() == -1) {
                g.setScaleY(1);
            } else
                g.setScaleY(-1);
        }
    }

    /**
     * For snakes on top of logs.
     * @param elapsedMils
     */
    private void moveWhileMounting(long elapsedMils){
        if (right) {
            if (parent.getBoundsInParent().getMaxX() <= g.getBoundsInParent().getMaxX()) {
                //reverse
                g.setScaleX(-1);
                right = false;
            } else {
                move(objectMounting.getDX()+ velocity * elapsedMils / 1000);
            }
        } else {
            if (parent.getBoundsInParent().getMinX() >= g.getBoundsInParent().getMinX()) {
                g.setScaleX(1);
                right = true;
            } else {
                move(-velocity * elapsedMils / 1000);
            }
        }
    }

    private void moveOnRoad(long elapsedMils){
        Bounds pBounds = parent.getLayoutBounds();
        Bounds b = g.getBoundsInParent();
        if (right) {
            move(velocity * elapsedMils / 1000);
            if (pBounds.getMaxX() <= b.getMaxX()) {
                //reverse
                g.setScaleX(-1);
                right = false;
            }
        } else {
            move(-velocity * elapsedMils / 1000);
            if (pBounds.getMinX() >=b.getMinX()) {
                g.setScaleX(1);
                right = true;
            }
        }
    }

    @Override
    public void drawShapes() {
        double WIDTH = playableWidth / GAME_TO_SNAKE_RATIO;
        double HEIGHT = WIDTH * 0.20;
        for (int i = 0; i < shapes.length; i++) {
            double strokeWidth = playableWidth * 0.007;
            switch (i) {
                case 0:
                    //body
                    shapes[i] = new Group(
                            new CubicCurve(
                                    x - WIDTH * 0.33,
                                    y - HEIGHT / 2,

                                    x - WIDTH * 0.33 + WIDTH * 0.16,
                                    y + HEIGHT * 2,
                                    x,
                                    y - HEIGHT * 2,

                                    x + WIDTH * 0.16,
                                    y

                            ) {{
                                setStroke(Color.LIMEGREEN);
                                setStrokeWidth(strokeWidth);
                                setFill(Color.TRANSPARENT);
                                setStrokeLineCap(StrokeLineCap.ROUND);
                            }}
                            ,
                            new QuadCurve(
                                    x + WIDTH * 0.16,
                                    y,
                                    x + WIDTH * 0.33,
                                    y + HEIGHT,
                                    x + WIDTH * 0.33 + WIDTH * 0.16,
                                    y
                            ) {{
                                setStroke(Color.LIMEGREEN);
                                setStrokeWidth(strokeWidth*0.9);
                                setFill(Color.TRANSPARENT);
                                setStrokeLineCap(StrokeLineCap.ROUND);
                            }}
                    );
                    break;
                case 1:
                    double radiusX = playableWidth * 0.010;
                    head = new Group(
                            new Line(x + WIDTH * 0.50, y, x + WIDTH * 0.80, y - HEIGHT * 0.30) {{
                                setStroke(Color.RED);
                                setStrokeWidth(playableWidth * 0.003);
                            }},
                            new Ellipse(x + WIDTH * 0.33 + WIDTH * 0.16 + radiusX, y, radiusX, playableWidth*0.006) {{
                                setStroke(Color.LIMEGREEN);
                                setStrokeWidth(strokeWidth);
                                setFill(Color.LIMEGREEN);
                                setStrokeLineCap(StrokeLineCap.ROUND);
                            }}
                    );
                    shapes[i] = head;
                    break;
            }
        }
    }

    @Override
    public void setShapeArray() {
        shapes = new Node[2];
    }

    @Override
    public boolean canKill() {
        return true;
    }

    @Override
    public boolean canLand() {
        return false;
    }

    //NOT CALLED FOR SNAKES
    @Override
    public double getRatio() {
        return 1;
    }

    @Override
    public double getDX() {
        return 0;
    }
}
