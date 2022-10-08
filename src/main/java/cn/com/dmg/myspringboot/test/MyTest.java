package cn.com.dmg.myspringboot.test;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyTest {

    public static void main(String[] args) throws Exception {

        System.out.println(Base64.encode("skyvis"));
        byte[] mzIxes = Base64.decode("MzIx");
        String s = new String(mzIxes);
        System.out.println(s);

    }

}
