package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientUI extends JFrame{
    private JRadioButton radioSOAP;
    private JRadioButton radioRest;
    private JTextField urlsField;
    private JTextArea text;
    private JButton validateButton;
    private JButton sendButton;
    private JPanel jpanel;
    private JTextField resField;
    private SOAPClient soapClient;
    private RestClient restClient;
    public ClientUI(){
        super("阿里云邮件发送");
        setContentPane(jpanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        addListens();
        setVisible(true);
        soapClient=new SOAPClient();
        restClient=new RestClient();
    }
    private void initUI(){
        setSize(640,480);
        setLocationRelativeTo(null);
        ButtonGroup group=new ButtonGroup();
        group.add(radioSOAP);
        group.add(radioRest);
        Font font=new Font("楷体",Font.PLAIN,20);
        urlsField.setFont(font);
        text.setFont(font);
        resField.setFont(font);
//        radioSOAP.setSelected(true);
        text.setLineWrap(true);
//        jpanel.add(new JScrollPane(text));
    }
    private void addListens(){
        validateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url=urlsField.getText();
                if (!radioSOAP.isSelected()&&!radioRest.isSelected()){
                    resField.setText("请选择一种方式");
                }
                else if (radioSOAP.isSelected()){
                    String res=soapClient.validateEmailAddressSOAP(url);
                    if (res==null){
                        resField.setText("SOAP:Error");
                        return;
                    }
                    if (res.equals("Y")){
                        resField.setText("SOAP:此邮箱合法");
                    }else if (res.equals("N")){
                        resField.setText("SOAP:此邮箱不合法");
                    }else {
                        resField.setText("SOAP:Error");
                    }
                }
                else {
                    String res=restClient.validateEmailAddressRest(url);
                    if (res==null){
                        resField.setText("Rest:Error");
                        return;
                    }
                    if (res.equals("Y")){
                        resField.setText("Rest:此邮箱合法");
                    }else if (res.equals("N")){
                        resField.setText("Rest:此邮箱不合法");
                    }else {
                        resField.setText("Rest:Error");
                    }
                }
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url=urlsField.getText();
                String content=text.getText();
                if (!radioSOAP.isSelected()&&!radioRest.isSelected()){
                    resField.setText("请选择一种方式");
                }
                else if (radioSOAP.isSelected()){
                    String res=soapClient.sendEmailSOAP(url,content);
                    if (res==null){
                        resField.setText("SOAP:Error");
                        return;
                    }
                    if (res.equals("Y")){
                        resField.setText("SOAP:发送成功");
                    }else if (res.equals("N")){
                        resField.setText("SOAP:发送失败");
                    }else {
                        resField.setText("SOAP:Error");
                    }
                }
                else {
                    String res=restClient.sendEmailRest(url,content);
                    if (res==null){
                        resField.setText("Rest:Error");
                        return;
                    }
                    if (res.equals("Y")){
                        resField.setText("Rest:发送成功");
                    }else if (res.equals("N")){
                        resField.setText("Rest:发送失败");
                    }else {
                        resField.setText("Rest:Error");
                    }
                }
            }
        });
    }
    public static void main(String[] args) {
        new ClientUI();
    }
}
