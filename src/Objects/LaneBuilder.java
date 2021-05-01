package Objects;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Hashtable;

import static Main.Game.playableWidth;

public class LaneBuilder {
    public static final int TOTAL_LANES = 16;

    //SCORE_LANE = 0;
    //GOAL_LANE_FIRST_HALF =1;
    public static final int GOAL_LANE = 2;

    public static final int FIRST_WATER_LANE = 3;
    public static final int SECOND_WATER_LANE = 4;
    public static final int THIRD_WATER_LANE = 5;
    public static final int FOURTH_WATER_LANE = 6;
    public static final int FIFTH_WATER_LANE = 7;

    public static final int TOP_PAVEMENT_LANE = 8;

    public static final int FIRST_ROAD_LANE = 9;
    public static final int SECOND_ROAD_LANE = 10;
    public static final int THIRD_ROAD_LANE = 11;
    public static final int FOURTH_ROAD_LANE = 12;
    public static final int FIFTH_ROAD_LANE = 13;

    public static final int BOTTOM_PAVEMENT_LANE = 14;
    public static final int TIME_LANE = 15;

    private static Hashtable<Integer, ArrayList<IMovableObject>> objects;
    private static double movableV;
    private static Pane root;
    private static int level;

    public static Hashtable<Integer, ArrayList<IMovableObject>> build(int l, double v, Pane pane) {
        objects = new Hashtable<>();
        movableV = v;
        root = pane;
        level = l;
        for (int i = FIRST_WATER_LANE; i <= FIFTH_ROAD_LANE; i++) {
            ArrayList<IMovableObject> list = new ArrayList<>();
            switch (i) {
                case TOP_PAVEMENT_LANE:
                    setTopPavementLane(i, list);
                    break;
                case FIRST_ROAD_LANE:
                    setFirstRoadLane(i,list);
                    break;
                case SECOND_ROAD_LANE:
                    setSecondRoadLane(i,list);
                    break;
                case THIRD_ROAD_LANE:
                    setThirdRoadLane(i,list);
                    break;
                case FOURTH_ROAD_LANE:
                    setFourthRoadLane(i, list);
                    break;
                case FIFTH_ROAD_LANE:
                    setFifthRoadLane(i, list);
                    break;
                case FIRST_WATER_LANE:
                    setFirstWaterLane(i,list);
                    break;
                case SECOND_WATER_LANE:
                   setSecondWaterLane(i,list);
                    break;
                case THIRD_WATER_LANE:
                    setThirdWaterLane(i, list);
                    break;
                case FOURTH_WATER_LANE:
                    setFourthWaterLane(i, list);
                    break;
                case FIFTH_WATER_LANE:
                    setFifthWaterLane(i, list);
                    break;
            }
            objects.put(i, list);
        }
        return objects;
    }

    private static void setFirstWaterLane(int lane, ArrayList<IMovableObject> list) {
        //velocity doesn't scale
        double v = playableWidth / 9.6;
        int nObjects;
        if (level % 3 == 0) nObjects = 3;
        else nObjects = 4;
        IMovableObject croc = new Crocodile(0, lane, v);
        double objectWidth = croc.getWidth();
        double spacing = (playableWidth + objectWidth - objectWidth * nObjects) / nObjects;
        switch (level % 3) {
            case 1:
                //level 1, 4 logs
                for (int i = 0; i < nObjects; i++) {
                    list.add(new LogMedium(i * spacing + i * objectWidth, lane, v));
                }
                break;
            case 2: case 0:
                //level 2, 1 crocodile 3 logs
                //level 3, 1 crocodile, 2 logs
                list.add(croc);
                for (int i = 1; i < nObjects; i++) {
                    list.add(new LogMedium(i * spacing + i * objectWidth, lane, v));
                }
                break;
        }
        int index = nObjects - 1;
        LogMedium mount = (LogMedium) list.get(index);
        UncontrollableFrog uFrog = new UncontrollableFrog(
                index * spacing + index * objectWidth, lane, mount.getMainGroup(), mount);
        mount.setMountedObject(uFrog);
        list.add(uFrog);
    }

    private static void setSecondWaterLane(int lane, ArrayList<IMovableObject> list) {
        //velocity doesn't scale
        double v = playableWidth / 9.6;
        int nObjects;
        if (level % 3 == 1) nObjects = 5;
        else nObjects = 4;
        IMovableObject temp = new Turtles2(1, lane, v, true);
        double objectWidth = temp.getWidth();
        double spacing = (playableWidth + objectWidth - objectWidth * nObjects) / nObjects;
        list.add(temp);
        for (int i = 1; i < nObjects; i++) {
            list.add(new Turtles2(i * spacing + i * objectWidth, lane, v, false));
        }
    }

    private static void setThirdWaterLane(int lane, ArrayList<IMovableObject> list) {
        double v = playableWidth / 9.6;
        int nObjects;
        switch (level % 3) {
            case 1: nObjects=3; break;
            case 2: nObjects=2; break;
            default: nObjects=1; break;
        }
        IMovableObject temp = new LogLarge(0, lane, v);
        double objectWidth = temp.getWidth();
        double spacing = (playableWidth + objectWidth - objectWidth * nObjects) / nObjects;
        list.add(temp);
        for (int i = 1; i < nObjects; i++) {
            list.add(new LogLarge(i * spacing + i * objectWidth, lane, v));
        }
        if (level %3 != 1) {
            LogLarge l = (LogLarge) list.get(0);
            Snake s = new Snake(0, lane, v * 0.7, l.getMainGroup(), l);
            l.setMountedObject(s);
            list.add(s);
        }
    }

    private static void setFourthWaterLane(int lane, ArrayList<IMovableObject> list) {
        double v = playableWidth / 19.2;
        int nObjects;
        if (level % 3 == 1) nObjects = 4;
        else nObjects = 3;
        IMovableObject temp = new LogSmall(1, lane, v);
        double objectWidth = temp.getWidth();
        double spacing = (playableWidth + objectWidth - objectWidth * nObjects) / nObjects;
        list.add(temp);
        for (int i = 1; i < nObjects; i++) {
            list.add(new LogSmall(i * spacing + i * objectWidth, lane, v));
        }
    }


    private static void setTopPavementLane(int lane, ArrayList<IMovableObject> list){
        double v = movableV * level;
        switch (level % 3) {
            case 2: case 0:
                list.add(new Snake(playableWidth / 1.92, lane, v, root, null));
                objects.put(lane, list);
                break;
        }
    }

    private static void setFirstRoadLane(int lane, ArrayList<IMovableObject> list) {
        double v = movableV * level;
        int nObjects;
        if (level % 3 == 1)
            nObjects = 2;
        else
            nObjects = 3;
        IMovableObject temp = new Truck(0, lane, v);
        double objectWidth = temp.getWidth();
        double spacing = (playableWidth - objectWidth * nObjects) / nObjects;
        list.add(temp);
        for (int i = 1; i < nObjects; i++) {
            list.add(new Truck(i * spacing + i * objectWidth, lane, v));
        }
    }

    private static void setSecondRoadLane(int lane, ArrayList<IMovableObject> list){
        double v = movableV * level + (movableV * 8);
        int nObjects;
        if (level % 3 == 1) nObjects = 1;
        else nObjects = 2;
        IMovableObject temp = new RacecarRight(playableWidth, lane, v);
        double objectWidth = temp.getWidth();
        double spacing = (playableWidth - objectWidth * nObjects) / nObjects;
        if (spacing > objectWidth) {
            spacing = objectWidth;
        }
        list.add(temp);
        for (int i = 1; i < nObjects; i++) {
            list.add(new RacecarRight(i * spacing + i * objectWidth, lane, v));
        }
    }

    private static void setThirdRoadLane(int lane, ArrayList<IMovableObject> list){
        double v = movableV * level * 1.5;
        int nObjects;
        if (level % 3 == 1) nObjects = 3;
        else nObjects = 4;
        IMovableObject temp = new Car(0, lane, v);
        double objectWidth = temp.getWidth();
        double spacing = (playableWidth - objectWidth * nObjects) / nObjects;
        list.add(temp);
        for (int i = 1; i < nObjects; i++) {
            list.add(new Car(i * spacing + i * objectWidth, lane, v));
        }
    }

    private static void setFourthRoadLane(int lane, ArrayList<IMovableObject> list) {
        double v = movableV * level;
        int nObjects;
        if (level % 3 == 1) nObjects = 3;
        else nObjects = 4;

        IMovableObject temp = new Tractor(playableWidth, lane, v);
        double objectWidth = temp.getWidth();
        double spacing = (playableWidth - objectWidth * nObjects) / nObjects;
        list.add(temp);
        for (int i = 1; i < nObjects; i++) {
            list.add(new Tractor(i * spacing + i * objectWidth, lane, v));
        }
    }

    private static void setFifthRoadLane(int lane, ArrayList<IMovableObject> list){
        double v = movableV * level;
        int nObjects;
        if (level % 3 == 1) nObjects = 3;
        else nObjects = 4;
        IMovableObject temp = new RacecarLeft(0, lane, v);
        double objectWidth = temp.getWidth();
        double spacing = (playableWidth - objectWidth * nObjects) / nObjects;
        list.add(temp);
        for (int i = 1; i < nObjects; i++) {
            list.add(new RacecarLeft(i * spacing + i * objectWidth, lane, v));
        }
    }

    private static void setFifthWaterLane(int lane, ArrayList<IMovableObject> list) {
        double v = playableWidth / 6.4;
        int nObjects;
        if (level % 3 == 0) nObjects = 3;
        else nObjects = 4;
        IMovableObject temp = new Turtles3(1, lane, v, true);
        double objectWidth = temp.getWidth();
        double spacing = (playableWidth +objectWidth - objectWidth * nObjects) / nObjects;
        list.add(temp);
        for (int i = 1; i < nObjects; i++) {
            list.add(new Turtles3(i * spacing + i * objectWidth, lane, v, false));
        }
    }
}
