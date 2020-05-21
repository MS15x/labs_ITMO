package lab_6.server;

/**
 * Хранит кординаты X и Y для билета
 */
public class Coordinates {
    public Coordinates(float x, long y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(long y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    private float x;
    private long y;
}
