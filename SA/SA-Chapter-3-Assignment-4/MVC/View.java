package MVC;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

class View extends Canvas implements Observer {
    Controller controller;
    Model model;
    int stepNumber = 0;

    View(Model model) {
        this.model = model;
    }

    public void paint(Graphics g) {
        g.setColor(Color.red);
        g.fillOval(model.xPosition, model.yPosition, model.BALL_SIZE, model.BALL_SIZE);
        controller.showStatus("Step " + (stepNumber++) + ", x = " + model.xPosition + ", y = " + model.yPosition);
    }

    public void update(Observable obs, Object arg) {
        repaint();
    }
}