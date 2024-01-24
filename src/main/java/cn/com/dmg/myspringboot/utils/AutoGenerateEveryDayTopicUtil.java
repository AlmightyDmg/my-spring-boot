package cn.com.dmg.myspringboot.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import javax.imageio.ImageIO;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;

/**
 * @ClassName AutoGenerateEveryDayTopicUtil
 * @Description 自动生成每日一题
 * @author zhum
 * @date 2024/1/15 9:21
 */
public class AutoGenerateEveryDayTopicUtil {
    public static void main(String[] args) throws Exception{
        //addWord2Cover("什么是泛型？有什么好处？你好我是朱猛1");
//        addWord2Content("ThreadLocal是java.lang下面的一个类，是用来解决java多线程程序中并发问题的一种途径；通过为每一个线程创建一份共享变量的副本来保证各个线程之间的变量的访问和修改互相不影响；\n" +
//                "ThreadLocal存放的值是线程内共享的，线程间互斥的，主要用于线程内共享一些数据，避免通过参数来传递，这样处理后，能够优雅的解决一些实际问题。111\nThreadLocal存放的值是线程内共享的，线程间互斥的，主要用于线程内共享一些数据，避免通过参数来传递，这样处理后，能够优雅的解决一些实际问题。111");

        autoGenerate();

    }



    /**
     * 自动生成
     * @author zhum
     * @date 2024/1/7 18:40
     * @param
     * @return void
     */
    public static void autoGenerate() {
        //读取文档内容
        String allContent = FileUtil.readString(new File("C:\\Users\\13117\\Desktop\\每日一题.txt"), "utf-8");
        //工作目录
        String workDir = "C:\\Users\\13117\\Desktop\\每日一题生成";
        //######分隔每一天的
        String[] everyDayContentArr = allContent.split("######");
        for (int i = 0; i < everyDayContentArr.length; i++) {
            String everyDayContent = everyDayContentArr[i];
            //&&&区分标题和正文
            String[] titleAndContent = everyDayContent.split("&&&");
            String title = titleAndContent[0];
            String content = titleAndContent[1];
            //生成封面
            String coverPath = addWord2Cover(title);
            //生成正文
            List<String> contentPathList = addWord2Content(content);
            //打包成文件夹
            String dirPath = workDir + "\\" + (i+1);
            FileUtil.mkdir(dirPath);

            //移动封面
            FileUtil.copyFile(coverPath, dirPath, StandardCopyOption.REPLACE_EXISTING);
            FileUtil.del(coverPath);

            //移动内容
            for (String contentPath : contentPathList) {
                FileUtil.copyFile(contentPath, dirPath, StandardCopyOption.REPLACE_EXISTING);
                FileUtil.del(contentPath);
            }

            //移动结尾
            FileUtil.copyFile("C:\\Users\\13117\\Desktop\\结尾.png", dirPath, StandardCopyOption.REPLACE_EXISTING);

            //原文本内容
            File file = FileUtil.newFile(dirPath + "\\文本内容.txt");
            FileUtil.writeString(everyDayContent, file, "utf-8");

        }
        //生成压缩文件
        ZipUtil.zip(workDir);
    }

    /**
     * 添加正文到图片
     * @author zhum
     * @date 2024/1/7 18:39
     * @param word
     * @return void
     */
    public static List<String> addWord2Content(String word) {
        String templateImgPath = "C:\\Users\\13117\\Desktop\\content_template.png";

        Integer fontSize = 45;
        Integer step = 19;
        Integer initX = 220;
        Integer initY = 530;
        Integer offsetY = 70;

        //判断一共要多少页 默认每两端一页
        word = word.replaceFirst("\r\n", "");
        String[] splits = word.split("\r\n");
        Queue<String> stringQueue = new ArrayDeque<>(Arrays.asList(splits));

        int a = 0;
        List<String> list = new ArrayList<>();
        while (stringQueue.size() > 0) {
            //出栈两个
            String content = stringQueue.poll();
            if (stringQueue.size() > 0) {
                content += "\r\n" + stringQueue.poll();
            }
            String outPath = "C:\\Users\\13117\\Desktop\\" + ++a + ".png";
            addWord2Image(templateImgPath, outPath, content, fontSize, step, initX, initY, offsetY);
            list.add(outPath);
        }
        return list;
    }

    /**
     * 封面加文字
     * @author zhum
     * @date 2024/1/6 17:13
     * @param
     * @return void
     */
    public static String addWord2Cover(String word){
        String templateImgPath = "C:\\Users\\13117\\Desktop\\cover_template.png";
        String outPath = "C:\\Users\\13117\\Desktop\\封面.png";
        Integer fontSize = 90;
        Integer step = 9;
        Integer initX = 150;
        Integer initY = 850;
        Integer offsetY = 120;

        addWord2Image(templateImgPath, outPath, word, fontSize, step, initX, initY, offsetY);
        return outPath;
    }


    /**
     * 向图片中添加文字
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
            String[] sections = word.split("\r\n");

            for (int i = 0; i < sections.length; i++) {
                String section = sections[i];
                if (StrUtil.isEmpty(section)) {
                    continue;
                }
                Integer[] integers = addWord2G2d(g2d, section, step, initX, initY, offsetY);
                initX = integers[0];
                //换段落加两倍
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

    /**
     * 获取x 和 y 的位置
     * @author zhum
     * @date 2024/1/19 16:59
     * @param x
     * @param y
     * @param offsetY
     * @return int[]
     */
    private static int[] getXY(Integer x, Integer y, int offsetY){
        //x不变 纵轴增加
        return new int[]{x, y + offsetY};
    }

    /**
     * 向 Graphics2D 对象中添加文字
     * @author zhum
     * @date 2024/1/19 16:59
     * @param g2d
     * @param word 一段文字
     * @param step 步长（一行放多少个文字）
     * @param initX 初始x位置
     * @param initY 初始y位置
     * @param offsetY y轴偏移量（换行的时候y轴向下移动的距离）
     * @return java.lang.Integer[]
     */
    private static Integer[] addWord2G2d (Graphics2D g2d, String word, Integer step, Integer initX, Integer initY, Integer offsetY) {

        /*
            获取一共有多少行
            因为英文、中文、标点符号所占用的空间不一样，所以不按照步长的形式进行平均分
            而是根据英文、中文、标点的实际情况去分，最大长度不超过 step
            @author zhum
            @date 2024/1/19 17:19
         */

        //获取每一行的内容
        List<String> rowList = getPerRowStr(word, step);
        for (String rowStr : rowList) {
            // 将文字添加到图片上
            g2d.drawString(rowStr, initX, initY);
            int[] xy = getXY(initX, initY, offsetY);
            initX = xy[0];
            initY = xy[1];
            //高度不能超
//            if (initY > 1400) {
//                throw new RuntimeException("该页文字过长");
//            }
        }
        return new Integer[]{initX, initY};
    }

    /**
     * 根据规则获取每一行的内容
     * @author zhum
     * @date 2024/1/19 17:47
     * @param word 一段内容
     * @param step 标准步长
     * @return java.util.List<java.lang.String>
     */
    private static List<String> getPerRowStr(String word, Integer step) {
        char[] chars = word.toCharArray();
        Queue<Character> queue = new ArrayDeque<>();
        for (char aChar : chars) {
            queue.add(aChar);
        }

        List<String> rows = new ArrayList<>();

        double currentRowLength = 0d;
        String row = "";
        while (queue.size() > 0) {
            Character pollChar = queue.poll();
            Character.UnicodeBlock block = Character.UnicodeBlock.of(pollChar);
            row += pollChar;
            //中文
            if (block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
                    block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
                    Objects.equals(pollChar.toString(), "，") ||
                    Objects.equals(pollChar.toString(), "、")
            ) {
                currentRowLength ++;

            } else {
                //非中文
                currentRowLength += 0.5;
            }

            //判断是否达到一行的标准
            if (currentRowLength >= step || queue.size() == 0) {
                currentRowLength = 0d;
                //判断如果下一个字节是标点符号 则添加到行后面
                if (queue.size() > 0) {
                    String s = queue.peek().toString();
                    if (s.equals("。") || s.equals("、") || s.equals("，") || s.equals("；")) {
                        row += queue.poll();
                    }
                }
                rows.add(row);
                row = "";
            }
        }

        return rows;
    }
}
