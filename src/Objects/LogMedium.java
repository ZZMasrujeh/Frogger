package Objects;

public class LogMedium extends LogLarge {
    private final double GAME_WIDTH_TO_LOG_RATIO = 3.75;

    LogMedium(double x, int lane, double velocity) {
        super(x, lane, velocity);
    }

    @Override
    public double getRatio() {
        return GAME_WIDTH_TO_LOG_RATIO;
    }
}
