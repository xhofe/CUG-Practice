package MVP;

import java.util.Observable;

class Model extends Observable {
    final int BALL_SIZE = 20;
    int xPosition = 0;
    int yPosition = 0;
    int xLimit, yLimit;
    int xDelta = 6;
    int yDelta = 4;

    void makeOneStep() {
        xPosition += xDelta;
        if (xPosition < 0) {
            xPosition = 0;
            xDelta = -xDelta;
        }
        if (xPosition >= xLimit) {
            xPosition = xLimit;
            xDelta = -xDelta;
        }
        yPosition += yDelta;
        if (yPosition < 0 || yPosition >= yLimit) {
            yDelta = -yDelta;
            yPosition += yDelta;
        }
        setChanged();
        notifyObservers();
    }
}