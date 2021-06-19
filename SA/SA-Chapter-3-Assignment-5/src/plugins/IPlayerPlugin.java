package plugins;

public interface IPlayerPlugin {
    public boolean loadFile(String filename);
    public boolean play();
    public boolean stop();
    public boolean pause();
}
