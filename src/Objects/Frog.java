package Objects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;

import static Main.Game.playableHeight;
import static Main.Game.playableWidth;
import static Objects.LaneBuilder.TOTAL_LANES;

public class Frog {
    double x;
    double y;

    int orientation = 0;
    int lane;
    double initialX;
    double hopDistance;
    int totalHopFrames = 2;
    double totalHopDuration = 100;
    int currentFrame = 0;
    FrogDirection direction;
    KeyFrame hopKeyFrame;
    Node[] shapes;
    Group g;
    Timeline hop = new Timeline();

    int eyeRotation = 25;

    boolean bound = false;
    SVGPath legs;

    private Color color;
    private Color lineColor;
    private final double GAME_WIDTH_TO_FROG_RATIO = 25;
    final double WIDTH = playableWidth / GAME_WIDTH_TO_FROG_RATIO;
    private final double HEIGHT = WIDTH / 2;

    Frog(double x, int lane, Color color, Color lineColor) {
        this.x = x;
        initialX = x;
        this.y = playableHeight / TOTAL_LANES * lane + playableHeight / TOTAL_LANES / 2;
        this.lane = lane;
        this.color = color;
        this.lineColor = lineColor;
        shapes = new Node[4];
        g = new Group();
        drawShapes();
        g.getChildren().addAll(shapes);
    }

    public boolean isBound() {
        return bound;
    }

    public Group getMainGroup() {
        return g;
    }

    public void drawShapes(){
        String svgContent;
        SVGPath p;
        for (int i = 0; i < shapes.length; i++) {
            switch (i) {
                //body
                case 0:
                    Ellipse e = new Ellipse(x, y, WIDTH * 0.26, WIDTH * 0.26);
                    e.setFill(color);
                    e.setScaleY(2);
                    e.setScaleX(1.5);
                    shapes[i] = e;
                    break;
                //legs
                case 1:
                    svgContent = ""
                            //top left leg
                            + "M" + (x - (WIDTH * 0.33)) + "," + (y - (HEIGHT * 0.125))
                            + "q" + (-WIDTH * 0.25) + "," + (-HEIGHT * 0.125) + " " + (-WIDTH * 0.125) + "," + (-HEIGHT / 2)
                            + "a 1,1 0 0,1" + (-WIDTH * 0.04) + "," + (-HEIGHT * 0.125)
                            + "l" + (WIDTH * 0.08) + "," + (-HEIGHT * 0.25)
                            + "a 1,1 0 0,1" + (WIDTH * 0.04) + ",0"
                            + "l 0," + (HEIGHT / 2) + "z"

                            //bottom left leg
                            + "M" + (x - (WIDTH * 0.33)) + "," + (y + (HEIGHT * 0.125))
                            + "q" + (-WIDTH * 0.25) + "," + (HEIGHT * 0.125) + " " + (-WIDTH * 0.125) + "," + (HEIGHT / 2)
                            + "a 1,1 0 1,0" + (-WIDTH * 0.04) + "," + (HEIGHT * 0.125)
                            + "l" + (WIDTH * 0.08) + "," + (HEIGHT * 0.25)
                            + "a 1,1 0 1,0" + (WIDTH * 0.04) + ",0"
                            + "l 0," + (-HEIGHT / 2) + "z"

                            //top right leg
                            + "M" + (x + (WIDTH * 0.33)) + "," + (y - (HEIGHT * 0.125))
                            + "q" + (WIDTH * 0.25) + "," + (-HEIGHT * 0.125) + " " + (WIDTH * 0.125) + "," + (-HEIGHT / 2)
                            + "a 1,1 0 1,0" + (WIDTH * 0.04) + "," + (-HEIGHT * 0.125)
                            + "l" + (-WIDTH * 0.08) + "," + (-HEIGHT * 0.25)
                            + "a 1,1 0 1,0" + (-WIDTH * 0.04) + ",0"
                            + "l 0," + (HEIGHT / 2) + "z"

                            //bottom right leg
                            + "M" + (x + (WIDTH * 0.33)) + "," + (y + (HEIGHT * 0.125))
                            + "q" + (WIDTH * 0.25) + "," + (HEIGHT * 0.125) + " " + (WIDTH * 0.125) + "," + (HEIGHT / 2)
                            + "a 1,1 0 0,1" + (WIDTH * 0.04) + "," + (HEIGHT * 0.125)
                            + "l" + (-WIDTH * 0.08) + "," + (HEIGHT * 0.25)
                            + "a 1,1 0 0,1" + (-WIDTH * 0.04) + ",0"
                            + "l 0," + (-HEIGHT / 2) + "z"
                    ;
                    p = new SVGPath();
                    p.setContent(svgContent);
                    p.setFill(color);
                    legs = p;
                    shapes[i] = p;
                    break;

                //eyes
                case 2:
                    Eye l = new Eye(eyeRotation);
                    Eye r = new Eye(-eyeRotation);
                    shapes[i] = new Group(l.getEyeGroup(), r.getEyeGroup());
                    break;

                case 3:
                    //lines on body
                    svgContent = ""
                            + "M" + (x - (WIDTH * 0.25)) + "," + (y + (HEIGHT * 0.25))
                            + "q " + (-WIDTH * 0.05) + "," + (HEIGHT * 0.25) + " " + (WIDTH * 0.125) + "," + (HEIGHT * 0.57)

                            + "M" + (x + (WIDTH * 0.25)) + "," + (y + (HEIGHT * 0.25))
                            + "q " + (+WIDTH * 0.05) + "," + (HEIGHT * 0.25) + " " + (-WIDTH * 0.125) + "," + (HEIGHT * 0.57)
                    ;
                    p = new SVGPath();
                    p.setContent(svgContent);
                    p.setStroke(lineColor);
                    p.setFill(color);
                    shapes[i] = p;
                    break;
            }
        }
    }

    public double getX() {
        return x;
    }

    class Bone{
        Group getBoneGroup() {
            return boneGroup;
        }
        Group boneGroup = new Group();
        Bone(int rotation){
            Line line = new Line(x - WIDTH / 2, y, x + WIDTH / 2, y);
            line.setStroke(Color.YELLOW);
            line.setStrokeWidth(WIDTH * 0.10);

            Circle[] boneRings = {
                    new Circle((x + WIDTH / 2), y - (HEIGHT * 0.125), WIDTH * 0.10, Color.YELLOW)
                    , new Circle((x + WIDTH / 2), y + (HEIGHT * 0.125), WIDTH * 0.10, Color.YELLOW)
                    , new Circle((x - WIDTH / 2), y + (HEIGHT * 0.125), WIDTH * 0.10, Color.YELLOW)
                    , new Circle((x - WIDTH / 2), y - (HEIGHT * 0.125), WIDTH * 0.10, Color.YELLOW)
            };
            boneGroup.getChildren().addAll(boneRings);
            boneGroup.getChildren().add(line);
            boneGroup.setRotate(rotation);
        }
    }

    class Eye{
        Group getEyeGroup() {
            return eyeGroup;
        }
        Group eyeGroup = new Group();
        Eye(int rotation){
            Ellipse e = new Ellipse();
            Circle c = new Circle();
            if (rotation >= 0) {
                e.setCenterX(x - (WIDTH * 0.16));
                c.setCenterX(x - (WIDTH * 0.16));
            } else {
                e.setCenterX(x + (WIDTH * 0.16));
                c.setCenterX(x + (WIDTH * 0.16));
            }
            e.setCenterY(y - (HEIGHT / 2));
            e.setRadiusX(WIDTH * 0.12);
            e.setRadiusY(WIDTH * 0.20);
            e.setSmooth(false);
            e.setFill(Color.YELLOW);
            e.setRotate(rotation);

            c.setCenterY(y - (HEIGHT / 2));
            c.setRadius(WIDTH * 0.10);
            c.setFill(Color.RED);
            eyeGroup.getChildren().addAll(e, c);
        }
    }

    void hopAnimation() {
        currentFrame = 0;
        hop.getKeyFrames().clear();
        hop.getKeyFrames().add(hopKeyFrame);
        hop.setCycleCount(totalHopFrames);
        hop.play();
    }
}
