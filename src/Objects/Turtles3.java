package Objects;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import static Main.Game.playableWidth;

public class Turtles3 extends RacecarLeft {

    enum SwimState{
        EXTENDED,PADDLING,RETRACTED
    }
    enum SinkState{
        FLOATING,
        STARTED,
        HALFWAY,
        SUBMERGED
    }
    private SwimState swimState = SwimState.EXTENDED;
    private SinkState sinkState = SinkState.FLOATING;
    private SinkState previousSinkState;

    //shapes
    Node[] shapesPaddling;
    Node[] shapesRetracted;
    Node[] shapesSinkStarted;
    Node[] shapesSinkHalfway;
    Node shapeSubmerged;

    final double GAME_WIDTH_TO_TURTLE_RATIO = 18;

    private double swimTimer = 0;
    private double sinkTimer = 0;
    private boolean sinks;

    Turtles3(double x, int lane, double velocity, boolean sinks) {
        super(x, lane, velocity);
        this.sinks = sinks;
        if (sinks) {
            setSinkArrays();
            drawSinkingNodes();
            sinkState = SinkState.FLOATING;
            previousSinkState = SinkState.FLOATING;
        }
    }

    private void setSinkArrays() {
        int n = 3;
        shapesSinkStarted = new Node[n];
        shapesSinkHalfway = new Node[n];
    }

    @Override
    public void setShapeArray() {
        int n = 5;
        shapes = new Node[n];
        shapesRetracted = new Node[n];
        shapesPaddling = new Node[n];
    }

    @Override
    public void update(long elapsedMils) {
        super.update(elapsedMils);
        updateSwim(elapsedMils);
        if (sinks) {
            updateSink(elapsedMils);
        }
        changeShapeArray();
    }
    private void changeShapeArray() {
        g.getChildren().clear();
        if (sinks && sinkState != SinkState.FLOATING) {
            switch (sinkState) {
                case STARTED:
                    g.getChildren().addAll(shapesSinkStarted);
                    break;
                case HALFWAY:
                    g.getChildren().addAll(shapesSinkHalfway);
                    break;
                case SUBMERGED:
                    g.getChildren().add(shapeSubmerged);
                    break;
            }
        }else {
            switch (swimState) {
                case EXTENDED:
                    g.getChildren().addAll(shapes);
                    break;
                case PADDLING:
                    g.getChildren().addAll(shapesPaddling);
                    break;
                case RETRACTED:
                    g.getChildren().addAll(shapesRetracted);
                    break;
            }
        }
    }
    private void changeSinkStateTo(SinkState newSinkState) {
        previousSinkState = this.sinkState;
        this.sinkState = newSinkState;
        sinkTimer = 0;
    }
    private void updateSink(long elapsedMills) {
        sinkTimer += elapsedMills;
        final double sinkInterval = 500;
        if (sinkTimer > sinkInterval) {
            switch (sinkState) {
                case FLOATING:
                    if (previousSinkState == SinkState.STARTED) {
                        changeSinkStateTo(SinkState.FLOATING);
                    } else {
                        changeSinkStateTo(SinkState.STARTED);
                    }
                    break;
                case STARTED:
                    if (previousSinkState == SinkState.FLOATING)
                        changeSinkStateTo(SinkState.HALFWAY);
                    else
                        changeSinkStateTo(SinkState.FLOATING);
                    break;
                case HALFWAY:
                    if (previousSinkState == SinkState.STARTED)
                        changeSinkStateTo(SinkState.SUBMERGED);
                    else
                        changeSinkStateTo(SinkState.STARTED);
                    break;
                case SUBMERGED:
                    changeSinkStateTo(SinkState.HALFWAY);
                    break;
            }
        }
    }
    private void updateSwim(long elapsedMills) {
        swimTimer += elapsedMills;
        if (swimTimer > 500) {
            swimTimer = 0;
            switch (swimState) {
                case EXTENDED:
                    swimState = SwimState.PADDLING;
                    break;
                case PADDLING:
                    swimState = SwimState.RETRACTED;
                    break;
                case RETRACTED:
                    swimState = SwimState.EXTENDED;
                    break;
            }
        }
    }

    @Override
    public void drawShapes() {
        double WIDTH = playableWidth / GAME_WIDTH_TO_TURTLE_RATIO;
        double HEIGHT = WIDTH / 2;
        double radiusX = WIDTH * 0.32;
        double radiusY = HEIGHT / 2;

        for (int i = 0; i < shapes.length; i++) {
            switch (i) {
                case 0:
                    //colliding group
                    Rectangle r = new Rectangle(x - WIDTH - WIDTH / 2, y - HEIGHT / 2, WIDTH * 3, HEIGHT) {{
                        setStroke(Color.TRANSPARENT);
                        setFill(Color.TRANSPARENT);
                    }};
                    shapes[i] =r;
                    shapesPaddling[i] =r;
                    shapesRetracted[i] =r;
                    break;
                case 1:
                    shapes[i] = new Group(
                            drawLegs(x - WIDTH, y, radiusX, radiusY, SwimState.EXTENDED)
                            , drawLegs(x, y, radiusX, radiusY, SwimState.EXTENDED)
                            , drawLegs(x + WIDTH, y, radiusX, radiusY, SwimState.EXTENDED)
                    );
                    shapesPaddling[i] = new Group(
                            drawLegs(x - WIDTH, y, radiusX, radiusY, SwimState.PADDLING)
                            , drawLegs(x, y, radiusX, radiusY, SwimState.PADDLING)
                            , drawLegs(x + WIDTH, y, radiusX, radiusY, SwimState.PADDLING)
                    );
                    shapesRetracted[i] = new Group(
                            drawLegs(x - WIDTH, y, radiusX, radiusY, SwimState.RETRACTED)
                            , drawLegs(x, y, radiusX, radiusY, SwimState.RETRACTED)
                            , drawLegs(x + WIDTH, y, radiusX, radiusY, SwimState.RETRACTED)
                    );
                    break;
                case 2:
                    //body
                    shapes[i] = new Group(
                            new Ellipse(x - WIDTH, y, radiusX, radiusY){{setFill(Color.RED);}}
                            ,new Ellipse(x, y, radiusX, radiusY){{setFill(Color.RED);}}
                            ,new Ellipse(x+WIDTH, y, radiusX, radiusY){{setFill(Color.RED);}}
                    );
                    shapesPaddling[i] = new Group(
                            new Ellipse(x - WIDTH, y, radiusX, radiusY){{setFill(Color.RED);}}
                            ,new Ellipse(x, y, radiusX, radiusY){{setFill(Color.RED);}}
                            ,new Ellipse(x+WIDTH, y, radiusX, radiusY){{setFill(Color.RED);}}
                    );
                    shapesRetracted[i] = new Group(
                            new Ellipse(x - WIDTH, y, radiusX, radiusY){{setFill(Color.RED);}}
                            ,new Ellipse(x, y, radiusX, radiusY){{setFill(Color.RED);}}
                            ,new Ellipse(x+WIDTH, y, radiusX, radiusY){{setFill(Color.RED);}}
                    );
                    break;
                case 3:
                    //head
                    shapes[i] = new Group(
                            drawHead(x - WIDTH, y, radiusX, SwimState.EXTENDED)
                            , drawHead(x, y, radiusX, SwimState.EXTENDED)
                            , drawHead(x + WIDTH, y, radiusX, SwimState.EXTENDED)
                    );
                    shapesPaddling[i] = new Group(
                            drawHead(x - WIDTH, y, radiusX, SwimState.PADDLING)
                            , drawHead(x, y, radiusX, SwimState.PADDLING)
                            , drawHead(x + WIDTH, y, radiusX, SwimState.PADDLING)
                    );
                    shapesRetracted[i] = new Group(
                            drawHead(x - WIDTH, y, radiusX, SwimState.RETRACTED)
                            , drawHead(x, y, radiusX, SwimState.RETRACTED)
                            , drawHead(x + WIDTH, y, radiusX, SwimState.RETRACTED)
                    );
                    break;
                case 4:
                    //tail
                    shapes[i] = new Group(
                            drawTail(x-WIDTH, y, radiusX, SwimState.EXTENDED)
                            ,drawTail(x, y, radiusX, SwimState.EXTENDED)
                            ,drawTail(x+WIDTH, y, radiusX, SwimState.EXTENDED)
                    );
                    shapesPaddling[i] = new Group(
                            drawTail(x-WIDTH, y, radiusX, SwimState.PADDLING)
                            ,drawTail(x, y, radiusX, SwimState.PADDLING)
                            ,drawTail(x+WIDTH, y, radiusX, SwimState.PADDLING)
                    );
                    shapesRetracted[i] = new Group(
                            drawTail(x-WIDTH, y, radiusX, SwimState.RETRACTED)
                            ,drawTail(x, y, radiusX, SwimState.RETRACTED)
                            ,drawTail(x+WIDTH, y, radiusX, SwimState.RETRACTED)
                    );
                    break;
            }
        }
    }

    @Override
    public boolean canLand() {
        return sinkState != SinkState.SUBMERGED;
    }

    @Override
    public boolean canKill() {
        return false;
    }

    void drawSinkingNodes(){
        double WIDTH = playableWidth / GAME_WIDTH_TO_TURTLE_RATIO;
        double HEIGHT = WIDTH / 2;
        double radiusX = WIDTH * 0.28;
        double radiusY = HEIGHT / 2;
        for (int i = 0; i < shapesSinkHalfway.length; i++) {
            switch (i) {
                case 0:
                    Rectangle r = new Rectangle(x - WIDTH - WIDTH / 2, y - HEIGHT / 2, WIDTH * 3, HEIGHT) {{
                        setStroke(Color.TRANSPARENT);
                        setFill(Color.TRANSPARENT);
                    }};
                    shapesSinkStarted[i] = r;
                    shapesSinkHalfway[i] = r;
                    shapeSubmerged =r;
                    break;
                case 1:
                    shapesSinkStarted[i] = new Group(
                            new Ellipse(x - WIDTH, y, radiusX, radiusY){{setFill(Color.LIMEGREEN);}}
                            ,new Ellipse(x, y, radiusX, radiusY){{setFill(Color.LIMEGREEN);}}
                            ,new Ellipse(x + WIDTH, y, radiusX, radiusY){{setFill(Color.LIMEGREEN);}}
                    );
                    shapesSinkHalfway[i] = new Group(
                            new Ellipse(x - WIDTH, y, radiusX / 2, radiusY / 2){{setFill(Color.LIMEGREEN);}}
                            ,new Ellipse(x, y, radiusX / 2, radiusY / 2){{setFill(Color.LIMEGREEN);}}
                            ,new Ellipse(x + WIDTH, y, radiusX / 2, radiusY / 2){{setFill(Color.LIMEGREEN);}}
                    );
                    break;
                case 2:
                    shapesSinkStarted[i] = drawRippleCircles(x, y, WIDTH, radiusX,false,false);
                    shapesSinkHalfway[i] = new Group(
                            //inner ripples
                            drawRippleCircles(x, y, WIDTH, radiusX / 2 * 1.3, false,false)
                            //outer ripples
                            ,drawRippleCircles(x, y, WIDTH, radiusX,true,false));
                    break;
            }
        }
    }

    Group drawRippleCircles(double x, double y, double WIDTH, double radiusX, boolean inverted, boolean twoTurtles) {
        Circle[] circles;
        if (twoTurtles) {
            circles = new Circle[]{
                    new Circle(x - WIDTH/2, y, radiusX * 1.3)
                    , new Circle(x + WIDTH/2, y, radiusX * 1.3)
            };
        } else {
            circles = new Circle[]{
                    new Circle(x - WIDTH, y, radiusX * 1.3)
                    , new Circle(x, y, radiusX * 1.3)
                    , new Circle(x + WIDTH, y, radiusX * 1.3)
            };
        }
        for (Circle c: circles) {
            double circumference = 2 * Math.PI * c.getRadius();
            if (inverted)
                c.setStrokeDashOffset(circumference * 0.06);
            else
                c.setStrokeDashOffset(-circumference * 0.06);
            c.getStrokeDashArray().addAll(circumference * 0.125, circumference * 0.125);
            c.setFill(Color.TRANSPARENT);
            c.setStroke(Color.WHITE);
        }
        return new Group(circles);
    }

    Group drawLegs(double x, double y, double xRadius, double yRadius, SwimState swimState) {
        double curveStrokeWidth = playableWidth/240;
        double radius = playableWidth/480;
        QuadCurve[] qc;
        Circle[] circles;

        if (swimState == SwimState.EXTENDED) {
            double legLength = playableWidth * 0.005;
            qc = new QuadCurve[]{
                    new QuadCurve(x - xRadius * 0.75, y - yRadius * 0.75, x - xRadius * 0.8, y - yRadius
                            , x - xRadius * 0.75 - legLength, y - yRadius * 0.75 - legLength)
                    , new QuadCurve(x - xRadius * 0.75, y + yRadius * 0.75, x - xRadius * 0.75, y + yRadius
                    , x - xRadius * 0.75 - legLength, y + yRadius * 0.75 + legLength)
                    , new QuadCurve(x + xRadius * 0.75, y - yRadius * 0.75, x + xRadius * 0.75, y - yRadius
                    , x + xRadius * 0.75 + legLength, y - yRadius * 0.75 - legLength)
                    , new QuadCurve(x + xRadius * 0.75, y + yRadius * 0.75, x + xRadius * 0.75, y + yRadius
                    , x + xRadius * 0.75 + legLength, y + yRadius * 0.75 + legLength)
            };
            circles = new Circle[]{
                    new Circle(x - xRadius * 0.75 - legLength
                            , y - yRadius * 0.75 - legLength - curveStrokeWidth / 2, radius, Color.LIMEGREEN)
                    , new Circle(x - xRadius * 0.75 - legLength - curveStrokeWidth / 2
                    , y - yRadius * 0.75 - legLength, radius, Color.LIMEGREEN)
                    , new Circle(x - xRadius * 0.75 - legLength
                    , y + yRadius * 0.75 + legLength + curveStrokeWidth / 2, radius, Color.LIMEGREEN)
                    , new Circle(x - xRadius * 0.75 - legLength - curveStrokeWidth / 2
                    , y + yRadius * 0.75 + legLength, radius, Color.LIMEGREEN)

                    , new Circle(x + xRadius * 0.75 + legLength
                    , y - yRadius * 0.75 - legLength - curveStrokeWidth / 2, radius, Color.LIMEGREEN)
                    , new Circle(x + xRadius * 0.75 + legLength + curveStrokeWidth / 2
                    , y - yRadius * 0.75 - legLength, radius, Color.LIMEGREEN)
                    , new Circle(x + xRadius * 0.75 + legLength
                    , y + yRadius * 0.75 + legLength + curveStrokeWidth / 2, radius, Color.LIMEGREEN)
                    , new Circle(x + xRadius * 0.75 + legLength + curveStrokeWidth / 2
                    , y + yRadius * 0.75 + legLength, radius, Color.LIMEGREEN)
            };
        } else if (swimState == SwimState.PADDLING) {
            double legLength = playableWidth * 0.005;
            qc = new QuadCurve[]{
                    new QuadCurve(x - xRadius * 0.75, y - yRadius * 0.75, x - xRadius * 0.75, y - yRadius
                            , x - xRadius * 0.75 + legLength, y - yRadius * 0.75 - legLength)
                    , new QuadCurve(x - xRadius * 0.75, y + yRadius * 0.75, x - xRadius * 0.75, y + yRadius
                    , x - xRadius * 0.75 + legLength, y + yRadius * 0.75 + legLength)
                    , new QuadCurve(x + xRadius * 0.75, y - yRadius * 0.75, x + xRadius * 0.75, y - yRadius
                    , x + xRadius * 0.75 + legLength, y - yRadius * 0.75 - legLength)
                    , new QuadCurve(x + xRadius * 0.75, y + yRadius * 0.75, x + xRadius * 0.75, y + yRadius
                    , x + xRadius * 0.75 + legLength, y + yRadius * 0.75 + legLength)
            };

            circles = new Circle[]{
                    new Circle(x - xRadius * 0.75 + legLength
                            , y - yRadius * 0.75 - legLength - curveStrokeWidth / 2, radius, Color.WHITE)
                    , new Circle(x - xRadius * 0.75 + legLength + curveStrokeWidth / 2
                    , y - yRadius * 0.75 - legLength, radius, Color.WHITE)

                    , new Circle(x - xRadius * 0.75 + legLength
                    , y + yRadius * 0.75 + legLength + curveStrokeWidth / 2, radius, Color.WHITE)
                    , new Circle(x - xRadius * 0.75 + legLength + curveStrokeWidth / 2
                    , y + yRadius * 0.75 + legLength, radius, Color.WHITE)

                    , new Circle(x + xRadius * 0.75 + legLength
                    , y - yRadius * 0.75 - legLength - curveStrokeWidth / 2, radius, Color.WHITE)
                    , new Circle(x + xRadius * 0.75 + legLength + curveStrokeWidth / 2
                    , y - yRadius * 0.75 - legLength, radius, Color.WHITE)

                    , new Circle(x + xRadius * 0.75 + legLength
                    , y + yRadius * 0.75 + legLength + curveStrokeWidth / 2, radius, Color.WHITE)
                    , new Circle(x + xRadius * 0.75 + legLength + curveStrokeWidth / 2
                    , y + yRadius * 0.75 + legLength, radius, Color.WHITE)
            };
        }else {
            double legLength = playableWidth * 0.001;
            qc = new QuadCurve[]{
                    new QuadCurve(x - xRadius * 0.75, y - yRadius * 0.75, x - xRadius * 0.8, y - yRadius
                            , x - xRadius * 0.75 - legLength, y - yRadius * 0.75 - legLength)
                    , new QuadCurve(x - xRadius * 0.75, y + yRadius * 0.75, x - xRadius * 0.75, y + yRadius
                    , x - xRadius * 0.75 - legLength, y + yRadius * 0.75 + legLength)
                    , new QuadCurve(x + xRadius * 0.75, y - yRadius * 0.75, x + xRadius * 0.75, y - yRadius
                    , x + xRadius * 0.75 + legLength, y - yRadius * 0.75 - legLength)
                    , new QuadCurve(x + xRadius * 0.75, y + yRadius * 0.75, x + xRadius * 0.75, y + yRadius
                    , x + xRadius * 0.75 + legLength, y + yRadius * 0.75 + legLength)
            };
            circles = new Circle[]{
                    new Circle(x - xRadius * 0.75 - legLength
                            , y - yRadius * 0.75 - legLength - curveStrokeWidth / 2, radius, Color.LIMEGREEN)
                    , new Circle(x - xRadius * 0.75 - legLength - curveStrokeWidth / 2
                    , y - yRadius * 0.75 - legLength, radius, Color.LIMEGREEN)
                    , new Circle(x - xRadius * 0.75 - legLength
                    , y + yRadius * 0.75 + legLength + curveStrokeWidth / 2, radius, Color.LIMEGREEN)
                    , new Circle(x - xRadius * 0.75 - legLength - curveStrokeWidth / 2
                    , y + yRadius * 0.75 + legLength, radius, Color.LIMEGREEN)

                    , new Circle(x + xRadius * 0.75 + legLength
                    , y - yRadius * 0.75 - legLength - curveStrokeWidth / 2, radius, Color.LIMEGREEN)
                    , new Circle(x + xRadius * 0.75 + legLength + curveStrokeWidth / 2
                    , y - yRadius * 0.75 - legLength, radius, Color.LIMEGREEN)
                    , new Circle(x + xRadius * 0.75 + legLength
                    , y + yRadius * 0.75 + legLength + curveStrokeWidth / 2, radius, Color.LIMEGREEN)
                    , new Circle(x + xRadius * 0.75 + legLength + curveStrokeWidth / 2
                    , y + yRadius * 0.75 + legLength, radius, Color.LIMEGREEN)
            };
        }

        for (QuadCurve c : qc) {
            c.setStroke(Color.LIMEGREEN);
            c.setFill(Color.LIMEGREEN);
            c.setStrokeWidth(curveStrokeWidth);
        }
        Group legs = new Group();
        legs.getChildren().addAll(qc);
        legs.getChildren().addAll(circles);
        return legs;
    }

    Group drawHead(double x, double y, double radiusX, SwimState swimState) {
        double lineLength;
        if (swimState == SwimState.RETRACTED)
            lineLength = playableWidth / 96;
        else
            lineLength = playableWidth / 64;

        CubicCurve c = new CubicCurve(
                x - radiusX
                , y
                , x - radiusX - lineLength / 2
                , y - lineLength / 2
                , x - radiusX - lineLength / 2
                , y + lineLength / 2
                , x - radiusX
                , y
        );
        c.setStrokeWidth(playableWidth / 320);
        c.setStroke(Color.LIMEGREEN);
        c.setFill(Color.LIMEGREEN);

        Circle c1 = new Circle(x - radiusX - lineLength * 0.33, y - lineLength * 0.16, playableWidth * 0.002, Color.WHITE);
        Circle c2 = new Circle(x - radiusX - lineLength * 0.33, y + lineLength * 0.16, playableWidth* 0.002, Color.WHITE);
        Circle c3 = new Circle(x - radiusX - lineLength / 2, y, playableWidth* 0.002, Color.LIMEGREEN);
        return new Group(c, c1, c2, c3);
    }

    Group drawTail(double x, double y, double radiusX, SwimState swimState) {
        double lineLength = playableWidth * 0.002;
        Line l1 = new Line(x + radiusX, y, x + radiusX + lineLength, y);
        l1.setStroke(Color.LIMEGREEN);
        if (swimState == SwimState.EXTENDED) {
            Line l2 = new Line(x + radiusX + lineLength, y, x + radiusX + lineLength + lineLength, y - lineLength);
            l2.setStroke(Color.LIMEGREEN);
            return new Group(l1, l2);
        } else if (swimState == SwimState.PADDLING) {
            Line l2 = new Line(x + radiusX + lineLength, y, x + radiusX + lineLength + lineLength, y + lineLength);
            l2.setStroke(Color.LIMEGREEN);
            return new Group(l1, l2);
        } else
            return new Group(l1);
    }

    @Override
    public Bounds getCollidingBounds() {
        return g.localToParent( g.getChildren().get(0).getBoundsInLocal());
    }
}
