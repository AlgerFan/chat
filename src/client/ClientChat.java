package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.Properties;

/**
 * @author 阿杰
 * Create 2018-08-24 16:32
 * Description:
 */
public class ClientChat extends JFrame implements ActionListener, KeyListener {

    public static void main(String[] args) {
        new ClientChat();
    }
    //文本域
    private JTextArea jta;
    //滚动条
    private JScrollPane jsp;
    //面板
    private JPanel jp;
    //文本框
    private JTextField jtf;
    //按钮
    private JButton jb;
    //输出流
    private BufferedWriter bufferedWriter = null;
    //客户端IP地址
    private static String clientIp;
    //客户端端口号
    private static int clientPort;
    static {
        Properties properties = new Properties();
        try {
            //加载配置文件
            properties.load(new FileReader("chat.properties"));
            //赋值
            clientPort = Integer.parseInt(properties.getProperty("clientPort"));
            clientIp = properties.getProperty("clientIp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //构造方法
    private ClientChat(){
        //初始化组件
        jta = new JTextArea();
        //**设置文本域不可编辑
        jta.setEditable(false);
        //**将文本域添加到滚动条中，实现滚动
        jsp = new JScrollPane(jta);
        //面板
        jp = new JPanel();
        jtf = new JTextField(10);
        jb = new JButton("发送");
        //**将文本框和按钮添加到面板中
        jp.add(jtf);
        jp.add(jb);
        jta.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 18));
        jsp.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 18));
        jp.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 18));
        jtf.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 18));
        jb.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 18));

        //**需要将滚动条和面板都添加到窗体中
        this.add(jsp,BorderLayout.CENTER);
        this.add(jp,BorderLayout.SOUTH);

        //**需要设置标题、大小、位置、关闭、是否可见
        this.setTitle("悄悄话");
        this.setSize(400,400);
        this.setLocation(100,650);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        /**********************TCP客户端******************************/
        jb.addActionListener(this);
        jtf.addKeyListener(this);
        try {
            //1、创建一个服务端的套接字
            Socket socket = new Socket(clientIp,clientPort);
            //2、获取socket通道的输入流
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //3、获取socket通道输出流
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //循环读取数据，并拼接到文本域中
            String line;
            while ((line = bufferedReader.readLine())!=null){
                //将读取的数据拼接到文本域中显示
                jta.append(line + System.lineSeparator());
            }
            //5、关闭serverSocket通道
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sendDataToSocket();
    }

    private void sendDataToSocket(){
        //1、获取文本框发送的内容
        String text = jtf.getText();
        //2、拼接需要发送的数据内容
        text = "小幸运：" + text;
        //3、自己也需要显示
        jta.append(text + System.lineSeparator());
        try {
            //4、发送
            System.out.println("客户端发送");
            bufferedWriter.write(text);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            //5、清空文本框内容
            jtf.setText("");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //回车键
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            sendDataToSocket();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
