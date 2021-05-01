package Objects;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

import static Main.Game.playableWidth;

public class Turtles2 extends Turtles3 {

    Turtles2(double x, int lane, double velocity, boolean sinks) {
        super(x, lane, velocity, sinks);
    }

    @Override
    public void drawShapes() {
        double WIDTH = playableWidth / GAME_WIDTH_TO_TURTLE_RATIO;
        double HEIGHT = WIDTH / 2;
        double radiusX = WIDTH / 3.1;
        double radiusY = HEIGHT / 2;

        for (int i = 0; i < shapes.length; i++) {
            switch (i) {
                case 0:
                    Rectangle r = new Rectangle(x - WIDTH, y - HEIGHT / 2, WIDTH * 2, HEIGHT) {{
//                        setStroke(Color.RED);
                        setStroke(Color.TRANSPARENT);
                        setFill(Color.TRANSPARENT);
                    }};
                    shapes[i] = r;
                    shapesPaddling[i] =r;
                    shapesRetracted[i] =r;
                    break;
                case 1:
                    shapes[i] = new Group(
                            drawLegs(x - WIDTH/2, y, radiusX, radiusY, SwimState.EXTENDED),
                            drawLegs(x + WIDTH/2, y, radiusX, radiusY, SwimState.EXTENDED)
                    );
                    shapesPaddling[i] = new Group(
                            drawLegs(x - WIDTH/2, y, radiusX, radiusY, SwimState.PADDLING)
                            , drawLegs(x + WIDTH/2, y, radiusX, radiusY, SwimState.PADDLING)
                    );
                    shapesRetracted[i] = new Group(
                            drawLegs(x - WIDTH/2, y, radiusX, radiusY, SwimState.RETRACTED)
                            , drawLegs(x + WIDTH/2, y, radiusX, radiusY, SwimState.RETRACTED)
                    );
                    break;
                case 2:
                    //body
                    shapes[i] = new Group(
                            new Ellipse(x - WIDTH/2, y, radiusX, radiusY){{setFill(Color.RED);}}
                            ,new Ellipse(x+WIDTH/2, y, radiusX, radiusY){{setFill(Color.RED);}}
                    );
                    shapesPaddling[i] = new Group(
                            new Ellipse(x - WIDTH/2, y, radiusX, radiusY){{setFill(Color.RED);}}
                            ,new Ellipse(x+WIDTH/2, y, radiusX, radiusY){{setFill(Color.RED);}}
                    );
                    shapesRetracted[i] = new Group(
                            new Ellipse(x - WIDTH/2, y, radiusX, radiusY){{setFill(Color.RED);}}
                            ,new Ellipse(x+WIDTH/2, y, radiusX, radiusY){{setFill(Color.RED);}}
                    );
                    break;
                case 3:
                    //head
                    shapes[i] = new Group(
                            drawHead(x - WIDTH/2, y, radiusX, SwimState.EXTENDED)
                            , drawHead(x + WIDTH/2, y, radiusX, SwimState.EXTENDED)
                    );
                    shapesPaddling[i] = new Group(
                            drawHead(x - WIDTH/2, y, radiusX, SwimState.PADDLING)
                            , drawHead(x + WIDTH/2, y, radiusX, SwimState.PADDLING)
                    );
                    shapesRetracted[i] = new Group(
                            drawHead(x - WIDTH/2, y, radiusX, SwimState.RETRACTED)
                            , drawHead(x + WIDTH/2, y, radiusX, SwimState.RETRACTED)
                    );
                    break;
                case 4:
                    //tail
                    shapes[i] = new Group(
                            drawTail(x-WIDTH/2, y, radiusX, SwimState.EXTENDED)
                            ,drawTail(x+WIDTH/2, y, radiusX, SwimState.EXTENDED)
                    );
                    shapesPaddling[i] = new Group(
                            drawTail(x-WIDTH/2, y, radiusX, SwimState.PADDLING)
                            ,drawTail(x+WIDTH/2, y, radiusX, SwimState.PADDLING)
                    );
                    shapesRetracted[i] = new Group(
                            drawTail(x-WIDTH/2, y, radiusX, SwimState.RETRACTED)
                            ,drawTail(x+WIDTH/2, y, radiusX, SwimState.RETRACTED)
                    );
                    break;
            }
        }
    }

    @Override
    void drawSinkingNodes(){
        double WIDTH = playableWidth / GAME_WIDTH_TO_TURTLE_RATIO;
        double HEIGHT = WIDTH / 2;
        double radiusX = WIDTH * 0.28;
        double radiusY = HEIGHT / 2;
        for (int i = 0; i < shapesSinkHalfway.length; i++) {
            switch (i) {
                case 0:
                    Rectangle r = new Rectangle(x - WIDTH, y - HEIGHT / 2, WIDTH * 2, HEIGHT) {{
                        setStroke(Color.TRANSPARENT);
                        setFill(Color.TRANSPARENT);
                    }};
                    shapesSinkStarted[i] =r;
                    shapesSinkHalfway[i] =r;
                    shapeSubmerged = r;//
                    break;
                case 1:
                    shapesSinkStarted[i] = new Group(
                            new Ellipse(x - WIDTH/2, y, radiusX, radiusY){{setFill(Color.LIMEGREEN);}}
                            ,new Ellipse(x + WIDTH/2, y, radiusX, radiusY){{setFill(Color.LIMEGREEN);}}
                    );
                    shapesSinkHalfway[i] = new Group(
                            new Ellipse(x - WIDTH/2, y, radiusX / 2, radiusY / 2){{setFill(Color.LIMEGREEN);}}
                            ,new Ellipse(x + WIDTH/2, y, radiusX / 2, radiusY / 2){{setFill(Color.LIMEGREEN);}}
                    );
                    break;
                case 2:
                    shapesSinkStarted[i] = drawRippleCircles(x, y, WIDTH, radiusX,false,true);
                    shapesSinkHalfway[i] = new Group(
                            //inner ripples
                            drawRippleCircles(x, y, WIDTH, radiusX / 2 * 1.3, false,true)
                            //outer ripples
                            ,drawRippleCircles(x, y, WIDTH, radiusX,true,true));
                    break;
            }
        }
    }
}
