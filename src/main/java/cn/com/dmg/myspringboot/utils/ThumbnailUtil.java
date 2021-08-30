package cn.com.dmg.myspringboot.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;

import java.io.File;

public class ThumbnailUtil {

    public static void main(String[] args) {



        String jpgPath = "C:\\Users\\zhum\\Desktop\\1.jpg";
        String jpgPath1 = "C:\\Users\\zhum\\Desktop\\2.jpg";
        String jpgPath2 = "C:\\Users\\zhum\\Desktop\\3.jpg";
        String jpgPath3 = "C:\\Users\\zhum\\Desktop\\4.jpg";
        String jpgDestPath = "C:\\Users\\zhum\\Desktop\\scale1_0.5.jpg";
        String jpgDestPath1 = "C:\\Users\\zhum\\Desktop\\scale2_0.5.jpg";
        String jpgDestPath2 = "C:\\Users\\zhum\\Desktop\\scale3_0.5.jpg";
        String jpgDestPath3 = "C:\\Users\\zhum\\Desktop\\scale4_0.5.jpg";

        File file = new File(jpgPath);
        File file1 = new File(jpgPath1);
        File file2 = new File(jpgPath2);
        File file3 = new File(jpgPath3);
        File destFile = new File(jpgDestPath);
        File destFile1 = new File(jpgDestPath1);
        File destFile2 = new File(jpgDestPath2);
        File destFile3 = new File(jpgDestPath3);


        long start = System.currentTimeMillis();
        ImgUtil.scale(file,destFile,0.1f);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        ImgUtil.scale(file1,destFile1,0.5f);
        ImgUtil.scale(file2,destFile2,0.5f);
        ImgUtil.scale(file3,destFile3,0.5f);

    }



}
