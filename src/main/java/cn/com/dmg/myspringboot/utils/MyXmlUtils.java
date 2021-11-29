package cn.com.dmg.myspringboot.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyXmlUtils {

    @Autowired
    private static RedisUtil redisUtil;

    public static void main(String[] args) {
        String xmlPath = "C:/Users/zhum/Desktop/jz_simple.xml";
        File file = new File(xmlPath);
        Document document = XmlUtil.readXML(file);
        Element catalogue = XmlUtil.getElementByXPath("//catalogue[@id='1-1']", document);
        Node parentNode = catalogue.getParentNode();
        NamedNodeMap attributes = parentNode.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String nodeName = item.getNodeName();
            String nodeValue = item.getNodeValue();
            System.out.println("name:" + nodeName + "，value:" + nodeValue);
        }


//        //查询目录
//        Element catalogue = XmlUtil.getElementByXPath("//catalogue[@id='a']", document);
//        //新增一个item 包含子节点
//        Element itemP = document.createElement("item");
//        itemP.setAttribute("id","p");
//        //查询两个节点，放到itemP下
//        Element itemEle1 = XmlUtil.getElementByXPath("//catalogue[@id='a']/item[@id='1']", document);
//        Element itemEle2 = XmlUtil.getElementByXPath("//catalogue[@id='a']/item[@id='2']", document);
//        //先在目录下删除，再移动
//        catalogue.removeChild(itemEle1);
//        catalogue.removeChild(itemEle2);
//        //移动
//        itemP.appendChild(itemEle1);
//        itemP.appendChild(itemEle2);
//        //将itemP放到目录下
//        catalogue.appendChild(itemP);
//
//        catalogue.removeChild(itemP);
//
//        System.out.println(XmlUtil.toStr(document));


    }



    public static void getAllCatalogueId(Document document){
        List<String> idList = new ArrayList<>();
        Set<String> idSet = new HashSet<>();
        //获得所有节点
        Map<Element,List<Element>> elementListMap = new HashMap<>();
        Element rootElement = XmlUtil.getRootElement(document);
        getAllElements(elementListMap,rootElement);
        //获得所有节点的id
        elementListMap.forEach((root,elementList)->{
            for (Element element : elementList) {
                String id = element.getAttribute("id");
                idList.add(id);
                idSet.add(id);
            }
        });

        //最后加上根节点的id
        String id = rootElement.getAttribute("id");
        idList.add(id);
        idSet.add(id);

        System.out.println(idList);
        System.out.println("list的长度为：" + idList.size());
        System.out.println(idSet);
        System.out.println("set的长度为：" + idSet.size());

    }



    public static void editEle(Document document){
        Element itemEle = XmlUtil.getElementByXPath("//catalogue[@id='a']/item[@id='1']", document);
        itemEle.setAttribute("itemName","5555");
        System.out.println(XmlUtil.toStr(document));
    }


    /**
     * @Description 批量新增item
     * @author zhum
     * @date 2021/8/3 10:09
     * @param document
     * @Return void
     */
    public static void testBatchAddItem(Document document){
        Element catalogueEle = XmlUtil.getElementByXPath("//catalogue[@id='a']", document);
        for (int i = 1; i <= 1000; i++) {
            Element item = document.createElement("item");
            item.setAttribute("id",Integer.toString(i));
            catalogueEle.appendChild(item);
        }
        String format = XmlUtil.format(document);
        System.out.println(format);
    }

    /**
     * @Description 根据 跟节点 获得所有 tag为catalogue的子节点
     * @author zhum
     * @date 2021/7/27 15:46
     * @param elementListMap
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

    /**
     * @Description 测试排序
     * @author zhum
     * @date 2021/8/3 10:08
     * @param document
     * @Return void
     */
    public static void testSort(Document document){
        Element catalogueEle = XmlUtil.getElementByXPath("//catalogue[@id='2-3']", document);

        //NodeList childNodes = elementByXPath.getChildNodes();

        //List<Element> elementList = XmlUtil.transElements(childNodes);

        //排序之后的id
        List<String> afterSortIds = new ArrayList<>();
        afterSortIds.add("111");
        afterSortIds.add("444");
        afterSortIds.add("222");
        afterSortIds.add("555");
        afterSortIds.add("333");
        //根据目录id 和 itemid获得ele
        for (String afterSortId : afterSortIds) {
            Element itemEle = XmlUtil.getElementByXPath("//catalogue[@id='2-3']/item[@id='"+afterSortId+"']", document);

            //先删除
            catalogueEle.removeChild(itemEle);
            //再新增
            catalogueEle.appendChild(itemEle);
        }

        String s = XmlUtil.toStr(document);
        //去除换行
        Pattern p = Pattern.compile("\r|\n");
        Matcher m = p.matcher(s);
        String s2 = m.replaceAll("");
        //格式化
        String format = XmlUtil.format(s2);
        System.out.println(format);
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
