package cn.com.dmg.myspringboot.test;

import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

public class MyTest {

    public static void main(String[] args) throws Exception {
        String url = "http://10.10.12.117:9000/cms/2023/12/25/dd5643ed09f04e618f5fd433900fb085.png";
        System.out.println(url.substring(url.indexOf("/cms/")));
    }

}
