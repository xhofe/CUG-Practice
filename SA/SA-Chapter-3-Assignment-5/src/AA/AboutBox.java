package AA;

import javax.swing.*;

public class AboutBox extends JFrame {
    public AboutBox(){
        super("about");
        setSize(640,480);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new AboutBox();
    }
}
