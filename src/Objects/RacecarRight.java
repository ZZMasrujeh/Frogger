package Objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import static Main.Game.*;

public class RacecarRight extends MovableObject_OneWay {
    private final double GAME_WIDTH_TO_CAR_RATIO = 20;
    RacecarRight(double x, int lane, double movableV) {
        super(x, lane, movableV);
    }

    @Override
    public void move(double dX) {
        super.move(dX);
    }

    @Override
    public void setShapeArray() {
        shapes = new SVGPath[6];
    }

    @Override
    public boolean canKill() {
        return true;
    }

    public void drawShapes() {
        double WIDTH = playableWidth / GAME_WIDTH_TO_CAR_RATIO;
        double HEIGHT = WIDTH / 2;
        String svgContent = "";
        for (int i = 0; i < shapes.length; i++) {
            SVGPath p = new SVGPath();
            switch (i) {
                //frame
                case 0:
                    svgContent = "" +
                            "M" + (x + (WIDTH * 0.40)) + "," + (y - (HEIGHT / 2))
                            + "l" + (-WIDTH) + ",0"
                            + "l" + "0," + HEIGHT
                            + "l" + (WIDTH) + ",0"
                            + "q" + (WIDTH / 2) + "," + (-HEIGHT / 2) + " 0," + (-HEIGHT) + "z"
                    ;
                    p.setFill(Color.WHITE);
                    break;
//
                //front wheels
                case 1:
                    svgContent = "" +
                            "M" + (x + (WIDTH * 0.08)) + "," + (y - HEIGHT + (HEIGHT * 0.16))
                            + " l" + (WIDTH * 0.33) + ",0"
                            + " l 0," + (HEIGHT * 0.25)
                            + " l" + (-WIDTH * 0.33) + ",0"
                            + " l 0," + (-HEIGHT * 0.25) + "z"

                            + " m" + (WIDTH * 0.16) + "," + (HEIGHT * 0.25)
                            + " l 0," + (HEIGHT * 0.10) + "z"

                            + "M" + (x + (WIDTH * 0.08)) + "," + (y + HEIGHT - (HEIGHT * 0.16))
                            + " l" + (WIDTH * 0.33) + ",0"
                            + " l 0," + (-HEIGHT * 0.25)
                            + " l" + (-WIDTH * 0.33) + ",0"
                            + " l 0," + (HEIGHT * 0.25) + "z"

                            + " m" + (WIDTH * 0.16) + "," + (-HEIGHT * 0.25)
                            + " l 0," + (-HEIGHT * 0.10) + "z"
                    ;
                    p.setStroke(Color.RED);
                    p.setStrokeWidth(playableWidth *0.002);
                    p.setFill(Color.RED);
                    break;
                //rear wheels
                case 2:
                    svgContent = ""
                            + "M" + (x - (WIDTH * 0.57)) + "," + (y - HEIGHT + (HEIGHT * 0.08))
                            + " l" + (WIDTH * 0.33) + ",0"
                            + " l 0," + (HEIGHT * 0.33)
                            + " l" + (-WIDTH * 0.33) + ",0"
                            + " l 0," + (-HEIGHT * 0.33) + "z"

                            + " m" + (WIDTH * 0.16) + "," + (HEIGHT * 0.33)
                            + " l 0," + (HEIGHT * 0.10) + "z"

                            + "M" + (x - (WIDTH * 0.57)) + "," + (y + HEIGHT - (HEIGHT * 0.08))
                            + " l" + (WIDTH * 0.33) + ",0"
                            + " l 0," + (-HEIGHT * 0.33)
                            + " l" + (-WIDTH * 0.33) + ",0"
                            + " l 0," + (HEIGHT * 0.33) + "z"

                            + " m" + (WIDTH * 0.16) + "," + (-HEIGHT * 0.33)
                            + " l 0," + (-HEIGHT * 0.10) + "z"
                    ;
                    
                    p.setStroke(Color.RED);
                    p.setStrokeWidth(playableWidth *0.002);
                    p.setFill(Color.RED);
                    break;

                //windshield
                case 3:
                    svgContent = "M" + x + "," + (y - (HEIGHT * 0.25))
                            + " l" + (WIDTH * 0.125) + ",0"
                            + " q" + (WIDTH * 0.33) + "," + (HEIGHT * 0.25) + " " + "0," + (HEIGHT / 2)
                            + " l" + (-WIDTH * 0.125) + ",0"
                            + "a 1,1 0 0,1 0," + (-HEIGHT * 0.06)
                            + "a 1,1 0 1,0 0," + (-HEIGHT * 0.37)
                            + "a 1,1 0 0,1 0," + (-HEIGHT * 0.06) + "z"
                    ;
                    p.setFill(Color.GREEN);
                    break;

                //spoiler
                case 4:
                    svgContent = "M" + (x - (WIDTH * 0.62)) + "," + (y - (HEIGHT * 0.25))
                            + " l" + (WIDTH * 0.33) + ",0"
                            + " q" + (WIDTH * 0.33) + "," + (HEIGHT * 0.25) + " " + "0," + (HEIGHT / 2)
                            + " l" + (-WIDTH * 0.33) + ",0"
                            + "a 1,1 0 0,1 0," + (-HEIGHT * 0.125)
                            + "a 1,1 0 1,0 0," + (-HEIGHT * 0.25)
                            + "a 1,1 0 0,1 0," + (-HEIGHT * 0.125) + "z"
                    ;
                    p.setFill(Color.BLACK);
                    p.setStroke(Color.GREEN);
                    break;
                //spoiler ellipsis
                case 5:
                    svgContent = "" +
                            "M" + (x - (WIDTH * 0.44)) + "," + (y - (HEIGHT * 0.125))
                            + "a 1,1 0 1,0 0," + (HEIGHT * 0.25)
                            + "a 1,1 0 1,0 0," + (-HEIGHT * 0.25) + "z"

                            + "m" + (WIDTH * 0.16) + ",0"
                            + "a 1,1 0 1,0 0," + (HEIGHT * 0.25)
                            + "a 1,1 0 1,0 0," + (-HEIGHT * 0.25) + "z"
                    ;
                    p.setFill(Color.RED);
                    break;
            }
            p.setContent(svgContent);
            shapes[i] = p;
        }
    }
}
