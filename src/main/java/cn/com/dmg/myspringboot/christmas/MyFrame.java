package cn.com.dmg.myspringboot.christmas;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaMing
 * @since 2021/12/17/0017 下午 13:43
 **/

public class MyFrame extends JFrame {

    MyPanel p;
    MyFrame() {
        p = new MyPanel();
        add(p);
        //设置布局
        setBounds(400, 200, 800, 800);
        setVisible(true);
        validate();

        setDefaultCloseOperation(MyFrame.EXIT_ON_CLOSE);
        //添加文字标签 可以添加你想发给的人的名字哦
        JLabel label1 = new JLabel("merry christmas!");
        //设置字体，格式（斜体，粗细），尺寸
        label1.setFont(new Font("宋体", Font.BOLD, 30));
        //设置标签颜色
        label1.setForeground(Color.red);
        label1.setBounds(250, 300, 500, 400);
        p.add(label1);
        p.setVisible(true);
    }

}
