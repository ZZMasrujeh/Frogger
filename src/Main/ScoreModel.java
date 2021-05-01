package Main;

class ScoreModel {
    private String name;
    private int points;
    private static final int maxChars = 9;

    ScoreModel(String name, int points) {
        if (name.length() > maxChars) {
            this.name = name.substring(0, maxChars);
        } else {
            this.name = name;
        }
        this.points = points;
    }

    String getName() {
        return name;
    }

    int getScore() {
        return points;
    }

    @Override
    public String toString() {
        return ""+points;
    }
}
