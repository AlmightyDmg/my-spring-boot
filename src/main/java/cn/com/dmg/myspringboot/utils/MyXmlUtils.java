package cn.com.dmg.myspringboot.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyXmlUtils {

    @Autowired
    private static RedisUtil redisUtil;

    public static void main(String[] args) {
        String xmlPath = "C:/Users/zhum/Desktop/jz_new.xml";
        File file = new File(xmlPath);
        Document document = XmlUtil.readXML(file);

        Element rootElement = XmlUtil.getRootElement(document);

        Element elementByXPath = XmlUtil.getElementByXPath("//catalogue[@id='2']", document);


        Element item = document.createElement("item");

        item.setAttribute("aaa","321");

        elementByXPath.appendChild(item);

        String s = XmlUtil.toStr(document);

        System.out.println(s);


    }

    /**
     * @Description 根据 跟节点 获得所有 tag为catalogue的子节点
     * @author zhum
     * @date 2021/7/27 15:46
     * @param elementList
     * @param rootElement
     * @Return void
     */
    public static void getAllElements(Map<Element,List<Element>> elementListMap, Element rootElement){
        if(rootElement.hasChildNodes()){
            List<Element> elements = XmlUtil.getElements(rootElement,"catalogue");
            elementListMap.put(rootElement,elements);
            for (Element element : elements) {
                getAllElements(elementListMap,element);
            }
        }
    }

    public static void test(){
        String xmlPath = "C:/Users/zhum/Desktop/jz.xml";
        File file = new File(xmlPath);
        Document document = XmlUtil.readXML(file);

        //将xml转换为字符串
        String xmlStr = XmlUtil.toStr(document);


        long start = System.currentTimeMillis();
        //将string字符串序列化
        byte[] serialize = ObjectUtil.serialize(xmlStr);
        System.out.println(serialize.length);

        List<Object> list = new ArrayList<>(serialize.length);
        for (int i = 0; i < serialize.length; i++) {
            list.add(serialize[i]);
        }

        //存储到redis
        String key = "test_save_jz_xml";
        redisUtil.del(key);
        redisUtil.lSet(key,list);

        long end = System.currentTimeMillis();
        System.out.println("序列化并存储到Redis用时：" + (end - start) + "毫秒");


        //反序列化
        List<Object> objects = redisUtil.lGet(key, 0, -1);
        byte[] bytes = new byte[objects.size()];
        //存到redis中的时候是Integer类型，需要先转换为byte类型
        for (int i = 0; i < objects.size(); i++) {
            int a = (int)objects.get(i);
            bytes[i] = (byte)a;
        }
        //反序列化
        Object deserialize = ObjectUtil.deserialize(bytes);
        String s = String.valueOf(deserialize);
        long end1 = System.currentTimeMillis();
        System.out.println("从Redis中查询并反序列化用时：" + (end1 - end) + "毫秒");

        //字符串转为xmlDocument
        Document document1 = XmlUtil.parseXml(s);
    }




    public static void xml2Json(String xmlStr){
        //将xml字符串转换为json，处理的不够好，节点的属性会丢失
        JSONObject jsonObject = XML.toJSONObject(xmlStr);
        String s1 = jsonObject.toStringPretty();
        System.out.println(s1);
    }










}
