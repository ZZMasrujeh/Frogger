package Objects;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import static Main.Game.*;

public class LogLarge extends RacecarRight{
    private final double GAME_WIDTH_TO_LOG_RATIO = 2.5;
    private IMovableObject mountedObject = null;
    /**Will be true only when the uncontrollable frog is picked, and is available to be placed back to the log.**/
    private boolean resetMountedFrog = false;

    LogLarge(double x, int lane, double velocity) {
        super(x, lane, velocity);
    }

    @Override
    public void update(long elapsedMils) {
        if (mountedObject == null) {
            super.update(elapsedMils);
        }
        else {
            //resets the mounted object along with this object
            ObjectState previousState = state;
            super.update(elapsedMils);
            if (previousState == ObjectState.RESETTING && state == ObjectState.MOVING) {
                if (resetMountedFrog) {
                    Group ufg = mountedObject.getMainGroup();
                    ufg.setVisible(true);
                    resetMountedFrog = false;
                }
                mountedObject.getMainGroup().setTranslateX(g.getTranslateX());
            }
        }
    }

    @Override
    public boolean canLand() {
        return true;
    }

    @Override
    public boolean canKill() {
        return false;
    }

    void setMountedObject(IMovableObject mObject) {
        mountedObject = mObject;
    }

    @Override
    public double getRatio() {
        return GAME_WIDTH_TO_LOG_RATIO;
    }

    @Override
    public void setShapeArray() {
        shapes = new Shape[7];
    }

    @Override
    public void drawShapes() {
        double WIDTH = playableWidth / getRatio();
        double HEIGHT = laneHeight / 1.5;
        double outerEllipseRadiusX = playableWidth * 0.015;
        double arcWidthHeight = playableWidth * 0.04;
        for (int i = 0; i < shapes.length; i++) {
            switch (i) {
                case 0:
                    shapes[i] = new Rectangle(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT) {{
                        setFill(Color.rgb(226, 109, 75));
                        setArcWidth(arcWidthHeight);
                        setArcHeight(arcWidthHeight);
                    }};
                    break;
                case 1:
                    //outer right edge
                    Ellipse e = new Ellipse(x + WIDTH / 2 - outerEllipseRadiusX, y, outerEllipseRadiusX, HEIGHT / 2);
                    e.setFill(Color.WHITE);
                    shapes[i] = e;
                    break;
                case 2:
                    drawWater(i,WIDTH,HEIGHT);
                    break;
                case 3:
                    //middle right edge
                    Ellipse ellipse = new Ellipse(x + WIDTH / 2 - playableWidth / 72, y, playableWidth / 96, HEIGHT / 3);
                    ellipse.setFill(Color.rgb(226,109,75));
                    shapes[i] = ellipse;
                    break;
                case 4:
                    //inner right edge
                    Circle c = new Circle(x + WIDTH / 2 - playableWidth / 72, y, playableWidth / 240, Color.WHITE);
                    shapes[i] = c;
                    break;
                case 5:
                    //blackLines on top and bottom
                    drawBlackLines(i, WIDTH,HEIGHT, outerEllipseRadiusX, arcWidthHeight);
                    break;
                case 6:
                    //white lines and spots
                    drawWhiteLines(i, WIDTH, HEIGHT, outerEllipseRadiusX, arcWidthHeight);
                    break;
            }
        }
    }

    private void drawWater(int i, double WIDTH, double HEIGHT) {
        double position = x - (WIDTH / 2);
        double dPosition = playableWidth / 10;
        Polygon polygon = new Polygon(
                position, y + (HEIGHT / 2)
        );
        while (position < x + (WIDTH / 2)) {
            if (position + dPosition > x + WIDTH / 2) {
                polygon.getPoints().addAll(x + WIDTH / 2 - dPosition / 2, y + HEIGHT / 4);
                break;
            }
            polygon.getPoints().addAll(
                    position + dPosition / 4, y + (HEIGHT / 4)//right
                    , position + dPosition / 4, y + (HEIGHT / 3)//down
                    , position + dPosition / 2, y + (HEIGHT / 3) //right
                    , position + dPosition / 2, y + (HEIGHT / 4) //up
            );
            position += dPosition;
        }
        polygon.setFill(Color.rgb(150, 103, 72));
        polygon.getPoints().addAll(x + WIDTH / 2, y + HEIGHT / 2);
        shapes[i] = polygon;
    }

    private void drawBlackLines(int i, double WIDTH, double HEIGHT, double ellipseRadiusX, double arcWidth){
        double dY = playableWidth / 320;    //vertical length of lines
        double startingX = x + WIDTH / 2 - ellipseRadiusX * 2;
        double position = startingX;
        double minX = x - WIDTH / 2 + arcWidth / 2;
        double dPosition = playableWidth * 0.04;  //distance from each set of two lines
        StringBuilder svgContent = new StringBuilder("M " + position + "," + (y - (HEIGHT / 2)));
        while (position > minX) {
            svgContent.append("" + "l 0,").append(dY).append("m").append(-dPosition).append(",").append(-dY)
            ;
            position -= dPosition;
        }
        position = startingX;
        dPosition = playableWidth * 0.06;
        svgContent.append("M ").append(position).append(",").append(y + (HEIGHT / 2));
        while (position > minX) {
            svgContent.append("" + "l 0,").append(-dY).append("m").append(-dPosition).append(",").append(dY)
            ;
            position -= dPosition;
        }
        SVGPath blackLines = new SVGPath();
        blackLines.setContent(svgContent.toString());
        blackLines.setStroke(Color.rgb(0,0,42));
        blackLines.setFill(Color.TRANSPARENT);
        blackLines.setStrokeWidth(playableWidth / 320);
        shapes[i] = blackLines;
    }

    void dismount(){
        mountedObject = null;
    }

    void mount(IMovableObject mountedObject) {
        this.mountedObject = mountedObject;
        resetMountedFrog = true;
    }

    private void drawWhiteLines(int i, double WIDTH, double HEIGHT, double ellipseRadiusX, double arcWidth) {
        double dY = playableWidth / 320;    //vertical length of lines
        double dPosition = playableWidth * 0.05;
        double startingPosition = x - WIDTH / 2 + arcWidth / 2;
        double maxX = x + WIDTH / 2 - ellipseRadiusX * 2;
        final String line = "l" + (dY * 3) + ",0" + "m" + (dPosition - dY * 3) + ",0";

        //top lines
        double position = startingPosition + dPosition * 0.3;
        StringBuilder svgContent = new StringBuilder("M " + position + "," + (y - HEIGHT / 2 + dY * 2));
        while (position < maxX) {
            svgContent.append(line);
            position += dPosition;
        }
        //middle lines
        position = startingPosition;
        svgContent.append("M ").append(position).append(",").append(y);
        while (position < maxX) {
            svgContent.append(line);
            position += dPosition;
        }
        //bottom lines
        position = startingPosition + dPosition * 0.6;
        svgContent.append("M ").append(position).append(",").append(y + (HEIGHT / 2) - dY * 2);
        while (position < maxX) {
            svgContent.append(line);
            position += dPosition;
        }

        String spot = "l" + (dY * 0.33) + " ,0" + "m" + (dPosition - dY * 0.33) + ",0";
        //topmost spots
        position = startingPosition + dPosition * 0.8;
        svgContent.append("M ").append(position).append(",").append(y - HEIGHT / 2 + (HEIGHT * 0.25));
        while (position<maxX){
            svgContent.append(spot);
            position += dPosition;
        }
        //second row spots
        position = startingPosition + dPosition * 0.6;
        svgContent.append("M ").append(position).append(",").append(y - (HEIGHT * 0.12));
        while (position<maxX){
            svgContent.append(spot);
            position += dPosition;
        }
        //third row spots
        position = startingPosition + dPosition * 0.3;
        svgContent.append("M ").append(position).append(",").append(y + (HEIGHT * 0.12));
        while (position<maxX){
            svgContent.append(spot);
            position += dPosition;
        }
        //fourth row spots
        position = startingPosition + dPosition * 0.1;
        svgContent.append("M ").append(position).append(",").append(y + HEIGHT / 2 - (HEIGHT * 0.25));
        while (position<maxX){
            svgContent.append(spot);
            position += dPosition;
        }
        SVGPath whiteLines = new SVGPath();
        whiteLines.setContent(svgContent.toString());
        whiteLines.setStroke(Color.WHITE);
        whiteLines.setFill(Color.TRANSPARENT);
        whiteLines.setStrokeWidth(playableWidth / 320);
        shapes[i] = whiteLines;
    }
}
