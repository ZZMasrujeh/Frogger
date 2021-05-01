package Objects;

public class LogSmall extends LogLarge {

    private final double GAME_WIDTH_TO_LOG_RATIO = 5;
    LogSmall(double x, int lane, double velocity) {
        super(x, lane, velocity);
    }

    @Override
    public double getRatio() {
        return GAME_WIDTH_TO_LOG_RATIO;
    }
}
