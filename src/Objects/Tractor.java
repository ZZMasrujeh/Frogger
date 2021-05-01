package Objects;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import static Main.Game.playableWidth;
public class Tractor extends RacecarRight {

    private final double TRACTOR_TO_GAME_RATIO = 20;

    Tractor(double x, int lane, double movableV) {
        super(x, lane, movableV);
    }

    @Override
    public void setShapeArray() {
        shapes = new Node[4];
    }

    @Override
    public void drawShapes() {
        double WIDTH = playableWidth / TRACTOR_TO_GAME_RATIO;
        double HEIGHT = WIDTH / 2;
        SVGPath p;
        String svgContent;
        Color green = Color.rgb(0, 255, 0);
        for (int i = 0; i < shapes.length; i++) {
            switch (i) {
                case 0:
                    //frame
                    svgContent = ""
                            + "M" + (x - WIDTH * 0.4) + "," + (y - HEIGHT / 2)
                            + "a 1,2 0 1,0 0," + HEIGHT
                            + "l" + (WIDTH * 0.70) + ",0"
                            + "l 0," + (-HEIGHT)+ "z"
                    ;
                    p = new SVGPath();
                    p.setFill(Color.WHITE);
                    p.setContent(svgContent);
                    shapes[i] = p;
                    break;
                case 1:
                    double strokeWidth = playableWidth *0.005;
                    svgContent = ""
                            //fork
                            + "M" + (x + WIDTH * 0.25) + "," + (y - HEIGHT / 2 - HEIGHT * 0.08)
                            + "l" + (WIDTH * 0.20) + ",0"
                            + "M" + (x + WIDTH * 0.25) + "," + (y + HEIGHT / 2 + HEIGHT * 0.08)
                            + "l" + (WIDTH * 0.20) + ",0"
                            //lights
                            + "M" + (x + WIDTH * 0.25) + "," + (y - HEIGHT * 0.16)
                            + "l 0," + (-playableWidth * 0.001)
                            + "M" + (x + WIDTH * 0.25) + "," + (y + HEIGHT * 0.16)
                            + "l 0," + (playableWidth * 0.001)
                    ;
                    p = new SVGPath();
                    p.setContent(svgContent);
                    p.setStroke(green);
                    p.setFill(green);
                    p.setStrokeWidth(strokeWidth);
                    //bucket
                    String svgBucket = ""
                            + "M" + (x + WIDTH / 2) + "," + (y - HEIGHT)
                            + "l 0," + (HEIGHT * 2)
                            + "l" + (WIDTH * 0.2) + ",0"
                            + "m" + (-WIDTH * 0.2) + "," + (-HEIGHT * 0.33)
                            + "l" + (WIDTH * 0.2) + ",0"
                            + "m" + (-WIDTH * 0.2) + "," + (-HEIGHT * 0.33)
                            + "l" + (WIDTH * 0.2) + ",0"
                            + "m" + (-WIDTH * 0.2) + "," + (-HEIGHT * 0.66)
                            + "l" + (WIDTH * 0.2) + ",0"
                            + "m" + (-WIDTH * 0.2) + "," + (-HEIGHT * 0.33)
                            + "l" + (WIDTH * 0.2) + ",0"
                            + "M" + (x + WIDTH / 2) + "," + (y - HEIGHT)
                            + "l" + (WIDTH * 0.2) + ",0";
                    SVGPath bucket = new SVGPath();
                    bucket.setContent(svgBucket);
                    bucket.setStroke(Color.WHITE);
                    bucket.setFill(Color.TRANSPARENT);
                    bucket.setStrokeWidth(strokeWidth);
                    Line l = new Line(x + WIDTH / 2 + strokeWidth, y - HEIGHT,
                            x + WIDTH / 2 + strokeWidth, y + HEIGHT){{
                        setStrokeWidth(strokeWidth);
                        setStroke(Color.RED);
                    }};
                    shapes[i] = new Group(p, l, bucket);
                    break;
                case 2:
                    //tracks
                    shapes[i] = new Group(
                            new Rectangle(x - WIDTH / 2, y - HEIGHT,
                                    WIDTH * 0.60, HEIGHT * 0.33) {{
                                setFill(Color.RED);
                            }},
                            new Rectangle(x - WIDTH / 2, y + HEIGHT - HEIGHT * 0.33,
                                    WIDTH * 0.60, HEIGHT * 0.33) {{
                                setFill(Color.RED);
                            }},
                            //full lines
                            new Line(x - WIDTH * 0.40, y - HEIGHT,
                                    x - WIDTH * 0.40, y + HEIGHT) {{
                                setStroke(Color.WHITE);
                                setStrokeWidth(playableWidth * 0.002);
                            }},
                            new Line(x, y - HEIGHT, x, y + HEIGHT) {{
                                setStroke(Color.WHITE);
                                setStrokeWidth(playableWidth * 0.002);
                            }},
                            //small lines
                            new Line(x - WIDTH * 0.20, y - HEIGHT,
                                    x - WIDTH * 0.20, y - HEIGHT + HEIGHT * 0.33) {{
                                setStroke(Color.WHITE);
                                setStrokeWidth(playableWidth * 0.002);
                            }},
                            new Line(x - WIDTH * 0.20, y + HEIGHT,
                                    x - WIDTH * 0.20, y + HEIGHT - HEIGHT * 0.33) {{
                                setStroke(Color.WHITE);
                                setStrokeWidth(playableWidth * 0.002);
                            }}
                    );
                    break;
                case 3:
                    //cabin
                    svgContent = ""
                            + "M" + (x - WIDTH * 0.33) + "," + (y - HEIGHT * 0.2)
                            + "l" + (WIDTH * 0.33) + ",0"
                            + "l 0," + (HEIGHT * 0.40)
                            + "l" + (-WIDTH * 0.33) + ",0z"

                            + "M" + x + "," + (y - HEIGHT * 0.33)
                            + "l 0," + (HEIGHT * 0.66)
                            + "l" + (WIDTH * 0.15) + ",0"
                            + "l 0," + (-HEIGHT * 0.66)+"z"
                    ;
                    p = new SVGPath();
                    p.setContent(svgContent);
                    p.setFill(green);
                    p.setStroke(green);
                    Rectangle r = new Rectangle(x-WIDTH*0.04, y - HEIGHT * 0.20, WIDTH*0.08, HEIGHT * 0.40) {{
                        setFill(Color.WHITE);
                    }};
                    shapes[i] = new Group(p, r);
                    break;
            }
        }
    }
}
