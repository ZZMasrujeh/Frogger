package Objects;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import static Main.Game.laneHeight;
import static Main.Game.playableWidth;
public class Truck extends RacecarLeft {

    private final double WIDTH_TO_TRUCK_RATIO = 6.5;
    Truck(double x, int lane, double velocity) {
        super(x, lane, velocity);
    }

    @Override
    public void setShapeArray() {
        shapes = new Node[4];
    }

    @Override
    public void drawShapes() {
        double WIDTH = playableWidth / WIDTH_TO_TRUCK_RATIO;
        double HEIGHT = laneHeight * 0.7;
        SVGPath p;
        String svgContent;
        for (int i = 0; i < shapes.length; i++) {
            switch (i) {
                case 0:
                    svgContent = ""
                            //container Front Tires
                            + "M" + (x - WIDTH * 0.10) + "," + (y - HEIGHT / 2)
                            + "l" + (WIDTH * 0.06) + ",0"
                            + "M" + (x - WIDTH * 0.10) + "," + (y + HEIGHT / 2)
                            + "l" + (WIDTH * 0.06) + ",0"
                            //container Back Tires
                            + "M" + (x + WIDTH / 2 - WIDTH * 0.15) + "," + (y - HEIGHT / 2)
                            + "l" + (WIDTH * 0.06) + ",0"
                            + "M" + (x + WIDTH / 2 - WIDTH * 0.15) + "," + (y + HEIGHT / 2)
                            + "l" + (WIDTH * 0.06) + ",0"
                            //cabin tires
                            + "M" + (x - WIDTH * 0.30) + "," + (y - HEIGHT / 2)
                            + "l" + (WIDTH * 0.05) + ",0"
                            + "M" + (x - WIDTH * 0.30) + "," + (y + HEIGHT / 2)
                            + "l" + (WIDTH * 0.05) + ",0"
                    ;
                    p = new SVGPath();
                    p.setContent(svgContent);
                    p.setStrokeWidth(playableWidth * 0.01);
                    p.setFill(Color.TRANSPARENT);
                    p.setStroke(Color.rgb(0, 255, 0));
                    shapes[i] = p;
                    break;
                case 1:
                    svgContent = ""
                            //bumper
                            + "M" + (x - WIDTH/2 +WIDTH* 0.05) + "," + (y - HEIGHT * 0.35)
                            + "a 1,2 0 1,0 0," + (HEIGHT * 0.70);
                    p = new SVGPath();
                    p.setContent(svgContent);
                    p.setStroke(Color.RED);
                    p.setFill(Color.TRANSPARENT);
                    p.setStrokeWidth(playableWidth * 0.004);
                    shapes[i] = p;
                    break;
                case 2:
                    svgContent = ""
                            //container
                            + "M" + (x - WIDTH * 0.15) + "," + (y - HEIGHT / 2)
                            + "l" + (WIDTH * 0.65) + ",0"
                            + "l 0," + (HEIGHT)
                            + "l" + (-WIDTH * 0.65) + ",0z"
                            //cabin
                            + "M" + (x - WIDTH * 0.20) + "," + (y - HEIGHT / 2)
                            + "l" + (-WIDTH * 0.14)+",0"
                            + "a 1,1 0 1,0 0," + (HEIGHT)
                            + "l" + (WIDTH * 0.14)+",0z"
                    ;
                    p = new SVGPath();
                    p.setContent(svgContent);
                    p.setFill(Color.WHITE);
                    shapes[i] = p;
                    break;
                case 3:
                    svgContent = ""
                            //container link
                            + "M" + (x - WIDTH * 0.17) + "," + (y - HEIGHT * 0.20)
                            + "l 0," + (HEIGHT * 0.40);
                    p = new SVGPath();
                    p.setContent(svgContent);
                    p.setStroke(Color.RED);
                    p.setStrokeWidth(playableWidth * 0.006);
                    shapes[i] = p;
                    break;
            }
        }
    }
}
