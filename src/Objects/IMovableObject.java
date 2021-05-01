package Objects;

import javafx.geometry.Bounds;
import javafx.scene.Group;

public interface IMovableObject {

    /**
     * @return The group of shapes that represent this object
     */
    Group getMainGroup();
    /**
     * Translates the object in the X axis.
     * @param dX X translation increment/decrement.
     */
    void move(double dX);

    void update(long elapsedMils);

    void drawShapes();

    /**
     * The arrays that will contain the shapes for this object's drawing might differ in size, for all objects.
     */
    void setShapeArray();

    /**
     * @return Ratio of this object's width to the playing area's width.
     */
    double getRatio();

    /**
     * For object's not completely canKill (i.e. Snake - where only the head kills the frog).
     * @return The canKill group, or the main group if the object is totally canKill.
     */
    Bounds getCollidingBounds();

    /**
     * @return The last amount of X axis movement this object has moved.
     */
    double getDX();

    boolean canKill();

    boolean canLand();

    double getWidth();
}
