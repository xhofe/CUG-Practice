package MVP;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

class View extends Canvas implements Observer {
    Presenter presenter;
    int stepNumber = 0;

    View(Presenter presenter) {
        this.presenter = presenter;
    }

    public void paint(Graphics g) {
        g.setColor(Color.red);
        g.fillOval(presenter.model.xPosition, presenter.model.yPosition, presenter.model.BALL_SIZE, presenter.model.BALL_SIZE);
        presenter.showStatus("Step " + (stepNumber++) + ", x = " + presenter.model.xPosition + ", y = " + presenter.model.yPosition);
    }

    public void update(Observable obs, Object arg) {
        repaint();
    }
}