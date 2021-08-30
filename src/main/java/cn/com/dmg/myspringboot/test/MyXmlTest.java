package cn.com.dmg.myspringboot.test;

import cn.hutool.core.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

import java.io.File;
import java.util.Scanner;

@Slf4j
public class MyXmlTest {

    public static String  xmlPath = "C:/Users/zhum/Desktop/jz_max.xml";


    public static void main(String[] args) throws Exception{

        File xmlFile = new File(xmlPath);
        Document document = XmlUtil.readXML(xmlFile);
        String s = XmlUtil.toStr(document);


        Scanner scanner = new Scanner(System.in);

        while (true){
            String next = scanner.next();
            System.out.println(next);
            long start = System.currentTimeMillis();
            Document doc = XmlUtil.parseXml(s);
            String s1 = XmlUtil.toStr(doc);
            long end = System.currentTimeMillis();
            log.info("使用org.w3c.dom.Document转换Document并转换为xmlString用时{}毫秒",end-start);
        }



    }

}
