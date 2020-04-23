package lab5;

/**
 * Хранит кординаты X и Y для билета
 */
class Coordinates {
    Coordinates(float x, long y) {
        this.x = x;
        this.y = y;
    }

    void setX(float x) {
        this.x = x;
    }

    void setY(long y) {
        this.y = y;
    }

    float getX() {
        return x;
    }

    long getY() {
        return y;
    }

    private float x;
    private long y;
}
