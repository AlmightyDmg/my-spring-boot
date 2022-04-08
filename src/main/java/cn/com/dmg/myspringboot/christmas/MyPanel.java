package cn.com.dmg.myspringboot.christmas;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * @author JiaMing
 * @since 2021/12/17/0017 下午 13:47
 **/

public class MyPanel extends JPanel implements ActionListener {

    /**
     * 坐标
     */
    public int x, y;

    /**
     * 开关按钮
     */
    public JButton onOff;

    /**
     * 触发时间事件
     */
    public Timer time;

    /**
     * 判断开关
     */
    public boolean flag;

    /**
     * 控制动画
     */
    public boolean color;
    //获取本地音乐
    File file = new File("E:\\christmasTree\\music.wav");
    URL url = null;
    URI uri = null;
    AudioClip clip;

    MyPanel() {
        setLayout(null);
        //获取本地关闭按钮
        ImageIcon icon = new ImageIcon("E:\\christmasTree\\OFF.png");
        icon.setImage(icon.getImage().getScaledInstance(50, 50, 0));
        onOff = new JButton();
        onOff.addActionListener(this);
        //添加按钮图片
        onOff.setIcon(icon);
        //设置 取消边框
        onOff.setBorder(null);
        //设置 取消默认背景颜色
        onOff.setContentAreaFilled(false);
        onOff.setBounds(0, 0, 50, 50);
        add(onOff);
        flag = true;
        color = true;
        time = new Timer(300, this);
        time.stop();
        try {
            uri = file.toURI();
            url = uri.toURL();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        clip = Applet.newAudioClip(url);
    }

    public void paintComponent(Graphics g) {
        x = 380;
        y = 100;
        if (color) {
            ImageIcon image1 = new ImageIcon("E:\\christmasTree\\2.png");
            g.drawImage(image1.getImage(), x - 3, y - 25, 25, 25, null);
        } else {
            ImageIcon image1 = new ImageIcon("E:\\christmasTree\\1.png");
            g.drawImage(image1.getImage(), x - 2, y - 24, 26, 26, null);
        }
        Color red = new Color(255, 0, 0);
        Color yellow = new Color(255, 241, 0);
        //画第一个三角形（第一层圣诞树）
        drawTree(1, 4, g);
        if (color) {
            //画第一个三角形的黄色装饰
            drawDecoration(x + 22, y - 44, 6, yellow, g);
            //画第一个三角形的红色装饰
            drawDecoration(x, y - 22, 8, red, g);
        } else {
            //画第一个三角形的红色装饰
            drawDecoration(x + 22, y - 44, 6, red, g);
            //画第一个三角形的黄色装饰
            drawDecoration(x, y - 22, 8, yellow, g);
        }
        x = 380 - 2 * 22;
        //画第二个三角形（中间层）
        drawTree(3, 6, g);
        if (color) {
            //画第二个三角形的黄色装饰
            drawDecoration(x + 22, y - 44, 10, yellow, g);
            //画第二个三角形的红色装饰
            drawDecoration(x, y - 22, 12, red, g);
        } else {
            //画第二个三角形的红色装饰
            drawDecoration(x + 22, y - 44, 10, red, g);
            //画第二个三角形的黄色装饰
            drawDecoration(x, y - 22, 12, yellow, g);
        }
        x = 380 - 4 * 22;
        //画第三个三角形（最底层）
        drawTree(5, 8, g);
        if (color) {
            //画第三个三角形的黄色装饰
            drawDecoration(x + 22, y - 44, 14, yellow, g);
            //画第三个三角形的红色装饰
            drawDecoration(x, y - 22, 16, red, g);
        } else {
            //画第三个三角形的红色装饰
            drawDecoration(x + 22, y - 44, 14, red, g);
            //画第三个三角形的黄色装饰
            drawDecoration(x, y - 22, 16, yellow, g);
        }
        x = 380 - 22;
        //画出树根
        drawRoot(g);
    }

    //画树
    void drawTree(int from, int to, Graphics g) {
       //设置树的颜色
        g.setColor(new Color(9, 124, 37));
        for (int i = from; i <= to; i++) {
            for (int j = 0; j < (i * 2 - 1); j++) {
                g.fillRect(x, y, 20, 20);
                x += 22;
            }
            x = 380 - i * 22;
            y += 22;
        }
    }

    //画装饰
    void drawDecoration(int tx, int ty, int num, Color c, Graphics g) {
        g.setColor(c);
        g.fillRoundRect(tx, ty, 18, 18, 18, 18);
        g.fillRoundRect(tx + num * 22, ty, 18, 18, 18, 18);
    }

    //画树根
    void drawRoot(Graphics g) {
        //设置树根颜色
        g.setColor(new Color(131, 78, 0));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                g.fillRect(x, y, 20, 20);
                x += 22;
            }
            x = 380 - 22;
            y += 22;
        }
    }

    public void actionPerformed(ActionEvent e) {
        //按钮事件
        if (e.getSource() == onOff) {
            //开关打开
            if (flag) {
                ImageIcon icon = new ImageIcon("E:\\christmasTree\\ON.png");
                icon.setImage(icon.getImage().getScaledInstance(50, 50, 0));
                onOff.setIcon(icon);
                flag = false;
                clip.loop();
                time.restart();
            } else {
                //开关关闭
                ImageIcon icon = new ImageIcon("E:\\christmasTree\\OFF.png");
                icon.setImage(icon.getImage().getScaledInstance(50, 50, 0));
                onOff.setIcon(icon);
                flag = true;
                time.stop();
                clip.stop();
            }
        } else if (e.getSource() == time) {
            repaint();
            color = !color;
        }
    }

}
