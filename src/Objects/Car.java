package Objects;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import static Main.Game.playableWidth;
public class Car extends RacecarLeft {

    private final double WIDTH_TO_CAR_RATIO = 20;

    Car(double x, int lane, double velocity) {
        super(x, lane, velocity);
    }

    @Override
    public void setShapeArray() {
        shapes = new Node[4];
    }

    @Override
    public void drawShapes() {
        double WIDTH = playableWidth / WIDTH_TO_CAR_RATIO;
        double HEIGHT = WIDTH / 2;
        SVGPath p;
        String svgcontent;
        for (int i = 0; i < shapes.length; i++) {
            switch (i) {
                case 0:
                    //frame
                    p = new SVGPath();
                    svgcontent = ""
                            + "M" + (x) + "," + (y - HEIGHT / 2)
                            + "a 2,1 0 1,0 " + (-WIDTH / 2) + ",0"
                            + "a 1,2 0 1,0 0," + (HEIGHT)
                            + "a 2,1 0 1,0 " + (WIDTH / 2) + ",0"
                            + "a 2,1 0 1,0 " + (WIDTH * 0.40) + ",0"
                            + "l  0," + (-HEIGHT)
                            + "a 2,1 0 1,0 " + (-WIDTH * 0.40) + ",0";
                    p.setContent(svgcontent);
                    p.setFill(Color.rgb(249,68,250));
                    p.setStroke(Color.rgb(249,68,250));
                    shapes[i] = p;
                    break;
                case 1:
                    //tires
                    p = new SVGPath();
                    svgcontent = ""
                            //front tires
                            + "M" + (x - WIDTH * 0.33) + "," + (y - HEIGHT * 0.75)
                            + "l" + (playableWidth * 0.005) + ",0"
                            + "M" + (x - WIDTH * 0.33) + "," + (y + HEIGHT * 0.75)
                            + "l" + (playableWidth * 0.005) + ",0"
                            + "M" + (x + WIDTH * 0.125) + "," + (y - HEIGHT * 0.75)
                            + "l" + (playableWidth * 0.008) + ",0"
                            + "M" + (x + WIDTH * 0.125) + "," + (y + HEIGHT * 0.75)
                            + "l" + (playableWidth * 0.008) + ",0";
                    p.setContent(svgcontent);
                    p.setStroke(Color.YELLOW);
                    p.setFill(Color.TRANSPARENT);
                    p.setStrokeWidth(playableWidth * 0.003);
                    shapes[i] = p;
                    break;
                case 2:
                    p = new SVGPath();
                    svgcontent = ""
                            //bumper
                            + "M" + (x - WIDTH / 2) + "," + (y - HEIGHT /2)
                            + "a 1,2 0 1,0 0," + (HEIGHT) + ""
                            //top spots
                            + "M" + (x - WIDTH /2) + "," + (y - HEIGHT * 0.25)
                            + "l" + (WIDTH * 0.002) + ",0"
                            + "M" + (x) + "," + (y - HEIGHT /2)
                            + "l" + (WIDTH * 0.002) + ",0"
                            + "M" + (x + WIDTH * 0.40) + "," + (y - HEIGHT * 0.25)
                            + "l" + (WIDTH * 0.002) + ",0"
                            //bottom spots
                            + "M" + (x - WIDTH /2) + "," + (y + HEIGHT * 0.25)
                            + "l" + (WIDTH * 0.002) + ",0"
                            + "M" + (x) + "," + (y + HEIGHT /2)
                            + "l" + (WIDTH * 0.002) + ",0"
                            + "M" + (x + WIDTH * 0.40) + "," + (y + HEIGHT * 0.25)
                            + "l" + (WIDTH * 0.002) + ",0";
                    p.setContent(svgcontent);
                    p.setStrokeWidth(playableWidth*0.002);
                    p.setStroke(Color.rgb(12,233,247));
                    p.setFill(Color.TRANSPARENT);
                    shapes[i] = p;
                    break;
                case 3:
                    p = new SVGPath();
                    svgcontent = ""
                            //windshield
                            + "M" + (x - WIDTH * 0.20) + "," + (y - HEIGHT / 2)
                            + "a 1,2 0 1,0" + (WIDTH * 0.10) + "," + (HEIGHT)
                            + "a 1,1 0 1,0 " + (-WIDTH * 0.05) + "," + (-HEIGHT * 0.05)
                            + "l 0," + (-HEIGHT + HEIGHT * 0.10)
                            + "a 1,1 0 1,0 " + (WIDTH * 0.05) + "," + (-HEIGHT * 0.05) + "z"
                            //cabin
                            + "M" + (x + WIDTH * 0.10) + "," + (y - HEIGHT * 0.40)
                            + "l 0," + (HEIGHT * 0.80)
                            + "l" + (HEIGHT * 0.40) + "," + 0
                            + "l 0," + (-HEIGHT * 0.80)
                            + "l" + (-HEIGHT * 0.40) + ",0z";
                    p.setContent(svgcontent);
                    p.setStrokeWidth(playableWidth * 0.002);
                    p.setStroke(Color.rgb(12,233,247));
                    p.setFill(Color.rgb(12,233,247));
                    shapes[i] = p;
                    break;
            }
        }
    }
}
