package cn.com.dmg.myspringboot.test;

import java.io.IOException;

public class AddSrtToVideo {
    public static void main(String[] args) {
        String inputVideo = "C:\\\\Users\\\\13117\\\\Desktop\\\\videoTest\\\\douyin.mp4";
        String inputSrt = "C:\\\\Users\\\\13117\\\\Desktop\\\\videoTest\\\\douyin.srt";
        String inputSrta = "C\\\\:/Users/13117/Desktop/videoTest/douyin.srt";
        String outputSrt = "C:\\\\Users\\\\13117\\\\Desktop\\\\videoTest\\\\douyin_out.mp4";
        addSrtToVideo(inputVideo, inputSrt, inputSrta, outputSrt);
    }

    public static void addSrtToVideo(String inputVideo, String inputSrt, String inputSrta, String outputVideo){
        String ffmpegCommand = "ffmpeg " +
                "-i \""+inputVideo+"\" " +
                "-i \""+inputSrt+"\" " +
                "-c:v libx264 " +
                "-c:a copy " +
                "-c:s mov_text " +
                "-metadata:s:s:0 " +
                "language=eng " +
                "-vf \"subtitles="+inputSrta+":force_style='FontName=Arial,FontSize=24,PrimaryColour=&Hffffff&'\" " +
                "\""+outputVideo+"\"";
        System.out.println("执行的命令为：" + ffmpegCommand);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand.split(" "));
            processBuilder.inheritIO(); // 将子进程的输入输出流与主进程绑定，方便调试
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Command executed successfully");
            } else {
                System.err.println("Command execution failed");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
