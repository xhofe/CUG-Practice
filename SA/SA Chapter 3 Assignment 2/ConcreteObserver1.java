package Sa3_2;

public class ConcreteObserver1 implements Observer{
    @Override
    public void uodate() {
        System.out.println("I am observer 1. I got you"+
                "message and I am going to"+
                "do something");
    }
}
