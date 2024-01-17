package cn.com.dmg.myspringboot.test;

import cn.hutool.core.io.FileUtil;

public class MyTest {

    public static void main(String[] args) throws Exception {
//        String text = "((((标题=1) AND 标题=2) OR 标题=3) AND 标题=4) AND 关键词=5*";
//        //首先找到 ( 最后一次出现的位置
//        int lastIndexOf = text.lastIndexOf("(");
//        //找到)第一次出现的位置
//        System.out.println(text.indexOf(")"));
//        //裁剪
//        String substring = text.substring(lastIndexOf, text.indexOf(")") + 1);
//        System.out.println(substring);
//        //替换
//        System.out.println(text.replace(substring, ""));
        System.out.println(FileUtil.getPrefix("123.pdf"));
    }

}
