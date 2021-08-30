package cn.com.dmg.myspringboot.utils;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @ClassName MyVideoUtils 视频工具类
 * @Description
 * @author zhum
 * @date 2021/8/26 10:48
 */
public class MyVideoUtils {


    public static void main(String[] args) throws Exception {

        File file = new File("C:\\Users\\zhum\\Desktop\\111.mp3");

        long start = System.currentTimeMillis();
        randomGrabberFFmpegImage(file);
        long end = System.currentTimeMillis();

        System.out.println(end - start);

    }

    /**
     * @param file 视频路径
     * @throws Exception
     */
    public static void randomGrabberFFmpegImage(File file) throws Exception {

        FFmpegFrameGrabber ff = FFmpegFrameGrabber.createDefault(file);
        ff.start();
        System.out.println(ff.getLengthInTime() / (1000 * 1000));
        String rotate = ff.getVideoMetadata("rotate");
        Frame f;
        int i = 0;
        while (i < 1) {
            f = ff.grabImage();
            IplImage src = null;
            if (null != rotate && rotate.length() > 1) {
                OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
                src = converter.convert(f);
                f = converter.convert(rotate(src, Integer.valueOf(rotate)));
            }
            doExecuteFrame(f, "C:\\Users\\zhum\\Desktop\\", "123");
            i++;
        }

        ff.stop();
    }

    /*
     * 旋转角度的
     */
    public static IplImage rotate(IplImage src, int angle) {
        IplImage img = IplImage.create(src.height(), src.width(), src.depth(), src.nChannels());
        opencv_core.cvTranspose(src, img);
        opencv_core.cvFlip(img, img, angle);
        return img;
    }

    public static void doExecuteFrame(Frame f, String targerFilePath, String targetFileName) {

        if (null == f || null == f.image) {
            return;
        }
        Java2DFrameConverter converter = new Java2DFrameConverter();
        String imageMat = "jpg";
        String FileName = targerFilePath + File.separator + targetFileName + "." + imageMat;
        BufferedImage bi = converter.getBufferedImage(f);
        System.out.println("width:" + bi.getWidth());
        System.out.println("height:" + bi.getHeight());
        File output = new File(FileName);
        try {
            ImageIO.write(bi, imageMat, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
