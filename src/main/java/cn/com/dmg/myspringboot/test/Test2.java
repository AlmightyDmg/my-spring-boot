package cn.com.dmg.myspringboot.test;

import java.util.ArrayList;
import java.util.List;

public class Test2 {

    public static void main(String[] args) throws Exception {

       StringBuilder a = new StringBuilder();
       test(a);
        System.out.println(a);


    }

    public static void test(StringBuilder a){
        a.append("1");
    }

    class Tec{
        public void test(String a){
            a = "1";
        }
    }
}
