package MVP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Presenter extends JApplet {
    JPanel buttonPanel = new JPanel();
    JButton stepButton = new JButton("Step");

    Model model = new Model();
    View view = new View(this);

    public void init() {
        setLayout(new BorderLayout());
        buttonPanel.add(stepButton);
        this.add(BorderLayout.SOUTH, buttonPanel);
        this.add(BorderLayout.CENTER, view);
        stepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                model.makeOneStep();
            }
        });
        model.addObserver(view);
        view.presenter = this;

    }

    public void start() {
        model.xLimit = view.getSize().width - model.BALL_SIZE;
        model.yLimit = view.getSize().height - model.BALL_SIZE;
        repaint();
    }
}