package cn.com.dmg.myspringboot.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
/**
 * @ClassName MyImageUtil
 * @Description
 * @author zhum
 * @date 2024/1/6 17:24
 */
public class MyImageUtil {
    public static void main(String[] args) {
        //addWord2Cover("什么是泛型？有什么好处？你好我是朱猛1");
        addWord2Content("ThreadLocal是java.lang下面的一个类，是用来解决java多线程程序中并发问题的一种途径；通过为每一个线程创建一份共享变量的副本来保证各个线程之间的变量的访问和修改互相不影响；\n" +
                "ThreadLocal存放的值是线程内共享的，线程间互斥的，主要用于线程内共享一些数据，避免通过参数来传递，这样处理后，能够优雅的解决一些实际问题。111");

    }

    public static void addWord2Content(String word) {
        String templateImgPath = "C:\\Users\\13117\\Desktop\\content_template.png";
        String outPath = "C:\\Users\\13117\\Desktop\\3.png";
        Integer fontSize = 45;
        Integer step = 20;
        Integer initX = 220;
        Integer initY = 530;
        Integer offsetY = 70;

        addWord2Image(templateImgPath, outPath, word, fontSize, step, initX, initY, offsetY);

    }

    /**
     * 封面加文字
     * @author zhum
     * @date 2024/1/6 17:13
     * @param
     * @return void
     */
    public static void addWord2Cover(String word){
        String templateImgPath = "C:\\Users\\13117\\Desktop\\cover_template.png";
        String outPath = "C:\\Users\\13117\\Desktop\\2.png";
        Integer fontSize = 90;
        Integer step = 10;
        Integer initX = 150;
        Integer initY = 850;
        Integer offsetY = 120;

        addWord2Image(templateImgPath, outPath, word, fontSize, step, initX, initY, offsetY);
    }


    /**
     *
     * @author zhum
     * @date 2024/1/6 16:58
     * @param word 需要添加的文字
     * @param step 步长（多少个文字之后换行）
     * @param offsetY 换行之后 Y 轴的偏移量
     * @return void
     */
    public static void addWord2Image(String templateImgPath, String outPath,
                                     String word, Integer fontSize,
                                     Integer step,Integer initX, Integer initY, Integer offsetY){
        try {
            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(new File(templateImgPath));

            // 创建Graphics2D对象来绘制文字
            Graphics2D g2d = originalImage.createGraphics();

            // 设置字体、颜色和大小
            Font font = new Font("黑体", Font.BOLD, fontSize);
            g2d.setFont(font);
            g2d.setColor(Color.BLACK); // 设置颜色为白色

            //段落
            String[] sections = word.split("\n");

            for (int i = 0; i < sections.length; i++) {
                String section = sections[i];
                Integer[] integers = addWord2G2d(g2d, section, step, initX, initY, offsetY);
                initX = integers[0];
                //换行加两倍
                initY = integers[1] + 2 * offsetY;
            }

            // 释放图形上下文使用的系统资源
            g2d.dispose();

            // 将结果写入新的文件
            ImageIO.write(originalImage, "png", new File(outPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int[] getXY(Integer x, Integer y, int offsetY){
        //x不变 纵轴增加
        return new int[]{x, y + offsetY};
    }

    private static Integer[] addWord2G2d (Graphics2D g2d, String word, Integer step,Integer initX, Integer initY, Integer offsetY) {
        //一共多少行
        Integer rows = word.length() / step + 1;

        //像图片上添加文字
        for (Integer i = 0; i < rows; i++) {
            //需要添加的文字
            int start = i * step;
            int end = Math.min(start + step, word.length());
            String substring = word.substring(start, end);

            // 将文字添加到图片上
            g2d.drawString(substring, initX, initY);

            int[] xy = getXY(initX, initY, offsetY);
            initX = xy[0];
            initY = xy[1];
            //高度不能超
            if (initY > 1400) {
                throw new RuntimeException("该页文字过长");
            }
        }

        return new Integer[]{initX, initY};
    }
}
