package AA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import plugins.IPlayerPlugin;

public class AAPFrame extends JFrame {
    private JButton choseFileToPlayButton;
    private JTextField music;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JPanel panel1;
    private JTextArea textArea1;
    private JButton choseExtFile;
    private JButton ok;
    private JTextField ext;
    private JButton loadButton;
    private File musicFile;
    private JFileChooser fileChooser=new JFileChooser(System.getProperty("user.dir"));
    private List<File> extFileList =new ArrayList<>();
    private List<String> extList=new ArrayList<>();
    private Map<String,Class<?>> classes=new HashMap<>();
    private File extFile;
    private IPlayerPlugin player=null;
    boolean change=true;

    public AAPFrame(){
        super("音乐播放器");
        setContentPane(panel1);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUI();
        setVisible(true);
        addListeners();
    }
    private void setUI(){
        setSize(640,480);
        textArea1.setFont(new Font("宋体",Font.PLAIN,16));
        setLocationRelativeTo(null);
        this.setVisible(true);
    }
    private void addListeners(){
        choseFileToPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = fileChooser.showDialog(null,null);
                if (option==JFileChooser.APPROVE_OPTION){
                    musicFile=new File(fileChooser.getSelectedFile().getPath());
                    music.setText(musicFile.getPath());
                    change=true;
                }
            }
        });
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!change){
                    addLog("已经加载:"+musicFile.getName());
                }else {
                    String type=musicFile.getName().substring(musicFile.getName().length()-3,musicFile.getName().length());
                    if (!classes.containsKey(type.toLowerCase())){
                        addLog("不支持文件类型:"+type);
                    }else {
                        try {
                            player=(IPlayerPlugin)classes.get(type.toLowerCase()).newInstance();
                            if(player.loadFile(musicFile.getPath())){
                                addLog("加载文件:"+musicFile.getName());
                                change=false;
                            }else {
                                addLog("加载失败:"+musicFile.getName());
                            }
                        } catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (player!=null){
                    if (player.play()){
                        addLog("播放:"+musicFile.getName());
                    }else {
                        addLog("播放失败"+musicFile.getName());
                    }
                }else {
                    addLog("请先加载文件");
                }
            }
        });
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (player!=null){
                    if (player.pause()){
                        addLog("暂停:"+musicFile.getName());
                    }else {
                        addLog("暂停失败:"+musicFile.getName());
                    }
                }else {
                    addLog("请先加载文件");
                }
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (player!=null){
                    if (player.stop()){
                        addLog("停止:"+musicFile.getName());
                    }else {
                        addLog("停止失败:"+musicFile.getName());
                    }
                }else {
                    addLog("请先加载文件");
                }
            }
        });
        //选择插件
        choseExtFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = fileChooser.showDialog(null,null);
                if (option==JFileChooser.APPROVE_OPTION){
                    extFile=new File(fileChooser.getSelectedFile().getPath());
                    extFileList.add(extFile);
                    ext.setText(extFile.getPath());
                }
            }
        });
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    URL url=new URL("file:"+extFile.getPath());
                    URLClassLoader classLoader=new URLClassLoader(new URL[]{url},Thread.currentThread().getContextClassLoader());
                    Class<?> cla=classLoader.loadClass("plugins."+extFile.getName().substring(0,extFile.getName().length()-4));
                    classes.put(extFile.getName().substring(0,3).toLowerCase(),cla);
                    addLog("已加载插件:"+extFile.getName()+" type:"+extFile.getName().substring(0,3).toLowerCase());
                } catch (Exception ex) {
                    addLog("加载失败:"+extFile.getName());
                    ex.printStackTrace();
                }
            }
        });
    }
    private void addLog(String s){
        if (textArea1.getText().split("\n").length>15){
            textArea1.setText("");
        }
        textArea1.append(s+'\n');
    }
}
