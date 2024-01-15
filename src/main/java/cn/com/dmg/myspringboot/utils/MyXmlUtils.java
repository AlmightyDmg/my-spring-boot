//package cn.com.dmg.myspringboot.utils;
//
//import cn.com.dmg.myspringboot.constant.JzxtElementAttrNameConstants;
//import cn.com.dmg.myspringboot.constant.JzxtTagNameConstants;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.core.util.XmlUtil;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.XML;
//import org.jeecg.common.util.RedisUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.w3c.dom.*;
//
//import java.io.File;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class MyXmlUtils {
//
//    //@Autowired
//    //private static RedisUtil redisUtil;
//
//    public static void main(String[] args) {
//        String xmlPath = "C:/Users/zhum/Desktop/test.xml";
//        File file = new File(xmlPath);
//        Document document = XmlUtil.readXML(file);
//
////        Element item = XmlUtil.getElementByXPath("//item[@id='2534443']", document);
//        NodeList item = XmlUtil.getNodeListByXPath("//item[contains(@pushUserIds,'1390202975103791105')]", document);
////        Node parentNode = item.getParentNode();
////        System.out.println(XmlUtil.toStr(document));
////        parentNode.removeChild(item);
////        NamedNodeMap attributes = parentNode.getAttributes();
////        Node id = attributes.getNamedItem("id");
////        String nodeValue = id.getNodeValue();
////
////        System.out.println(XmlUtil.toStr(document));
//
//        queryXmlByUserId(document);
//
//
////
////        System.out.println(XmlUtil.toStr(document));
//
//
//    }
//
//
//
//
//    public static void queryXmlByUserId(Document document){
//        String userId = "1448933929359163393";
//        String catalogueCode = "E900";
//        Element elementByCatalogueCode = getElementByCatalogueCode(document, catalogueCode);
//        //获得 code 目录下的所有子节点
//        NodeList childNodes = elementByCatalogueCode.getChildNodes();
//
//        for (int i = 0; i < childNodes.getLength(); i++) {
//            Node catalogue = childNodes.item(i);
//            String nodeName = catalogue.getNodeName();
//            //目录
//            if(nodeName.equals(JzxtTagNameConstants.CATALOGUE)){
//                NamedNodeMap attributes = catalogue.getAttributes();
//                Node idNode = attributes.getNamedItem(JzxtElementAttrNameConstants.ID);
//                String id = idNode.getNodeValue();
//
//                //如果目录id 为 当前用户id 则全目录保存
//                if(userId.equals(id)){
//                    continue;
//                } else {
//                    //如果不为当前目录id  则需要 遍历节点 查看pushUserIds中是否包含当前目录节点 不包含则删除
//                    NodeList itemChildNodes = catalogue.getChildNodes();
//                    for (int i1 = 0; i1 < itemChildNodes.getLength(); i1++) {
//                        Node itemNode = itemChildNodes.item(i1);
//                        String itemNodeName = itemNode.getNodeName();
//                        //item节点
//                        if(itemNodeName.equals(JzxtTagNameConstants.ITEM)){
//                            //判断是否包含userid
//                            NamedNodeMap attributes1 = itemNode.getAttributes();
//                            Node namedItem = attributes1.getNamedItem(JzxtElementAttrNameConstants.PUSH_USER_IDS);
//                            if(namedItem!=null){
//                                String pushUserIds = namedItem.getNodeValue();
//                                if(!pushUserIds.contains(userId)){
//                                    //移除
//                                    catalogue.removeChild(itemNode);
//                                }
//                            }
//                        }
//
//                    }
//                }
//            }
//        }
//
//        NodeList nodeListByXPath = XmlUtil.getNodeListByXPath("/root/catalogue", document);
//        Element rootElement = XmlUtil.getRootElement(document);
//        System.out.println(nodeListByXPath.getLength());
//        for (int i = 0; i < nodeListByXPath.getLength(); i++) {
//            Node item = nodeListByXPath.item(i);
//            NamedNodeMap attributes = item.getAttributes();
//            Node namedItem = attributes.getNamedItem(JzxtElementAttrNameConstants.CODE);
//            if(!catalogueCode.equals(namedItem.getNodeValue())){
//                System.out.println(namedItem.getNodeValue());
//                rootElement.removeChild(item);
//            }
//        }
//
//        System.out.println(XmlUtil.toStr(document));
//
//
//    }
//
//
//
//    public static void getAllCatalogueId(Document document){
//        List<String> idList = new ArrayList<>();
//        Set<String> idSet = new HashSet<>();
//        //获得所有节点
//        Map<Element,List<Element>> elementListMap = new HashMap<>();
//        Element rootElement = XmlUtil.getRootElement(document);
//        getAllElements(elementListMap,rootElement);
//        //获得所有节点的id
//        elementListMap.forEach((root,elementList)->{
//            for (Element element : elementList) {
//                String id = element.getAttribute("id");
//                idList.add(id);
//                idSet.add(id);
//            }
//        });
//
//        //最后加上根节点的id
//        String id = rootElement.getAttribute("id");
//        idList.add(id);
//        idSet.add(id);
//
//        System.out.println(idList);
//        System.out.println("list的长度为：" + idList.size());
//        System.out.println(idSet);
//        System.out.println("set的长度为：" + idSet.size());
//
//    }
//
//
//
//    public static void editEle(Document document){
//        Element itemEle = XmlUtil.getElementByXPath("//catalogue[@id='a']/item[@id='1']", document);
//        itemEle.setAttribute("itemName","5555");
//        System.out.println(XmlUtil.toStr(document));
//    }
//
//    /**
//     * @Description 根据目录Code查询Element
//     * @author zhum
//     * @date 2021/11/22 15:09
//     * @param document
//     * @param catalogueCode
//     * @Return org.w3c.dom.Element
//     */
//    public static Element getElementByCatalogueCode(Document document,String catalogueCode){
//        return getElementByTagNameAndAttr(document, JzxtTagNameConstants.CATALOGUE, JzxtElementAttrNameConstants.CODE,catalogueCode);
//    }
//
//    /**
//     * @Description 批量新增item
//     * @author zhum
//     * @date 2021/8/3 10:09
//     * @param document
//     * @Return void
//     */
//    public static void testBatchAddItem(Document document){
//        Element catalogueEle = XmlUtil.getElementByXPath("//catalogue[@id='a']", document);
//        for (int i = 1; i <= 1000; i++) {
//            Element item = document.createElement("item");
//            item.setAttribute("id",Integer.toString(i));
//            catalogueEle.appendChild(item);
//        }
//        String format = XmlUtil.format(document);
//        System.out.println(format);
//    }
//
//    /**
//     * @Description 根据 跟节点 获得所有 tag为catalogue的子节点
//     * @author zhum
//     * @date 2021/7/27 15:46
//     * @param elementListMap
//     * @param rootElement
//     * @Return void
//     */
//    public static void getAllElements(Map<Element,List<Element>> elementListMap, Element rootElement){
//        if(rootElement.hasChildNodes()){
//            List<Element> elements = XmlUtil.getElements(rootElement,"catalogue");
//            elementListMap.put(rootElement,elements);
//            for (Element element : elements) {
//                getAllElements(elementListMap,element);
//            }
//        }
//    }
//
//    /**
//     * @Description 测试排序
//     * @author zhum
//     * @date 2021/8/3 10:08
//     * @param document
//     * @Return void
//     */
//    public static void testSort(Document document){
//        Element catalogueEle = XmlUtil.getElementByXPath("//catalogue[@id='2-3']", document);
//
//        //NodeList childNodes = elementByXPath.getChildNodes();
//
//        //List<Element> elementList = XmlUtil.transElements(childNodes);
//
//        //排序之后的id
//        List<String> afterSortIds = new ArrayList<>();
//        afterSortIds.add("111");
//        afterSortIds.add("444");
//        afterSortIds.add("222");
//        afterSortIds.add("555");
//        afterSortIds.add("333");
//        //根据目录id 和 itemid获得ele
//        for (String afterSortId : afterSortIds) {
//            Element itemEle = XmlUtil.getElementByXPath("//catalogue[@id='2-3']/item[@id='"+afterSortId+"']", document);
//
//            //先删除
//            catalogueEle.removeChild(itemEle);
//            //再新增
//            catalogueEle.appendChild(itemEle);
//        }
//
//        String s = XmlUtil.toStr(document);
//        //去除换行
//        Pattern p = Pattern.compile("\r|\n");
//        Matcher m = p.matcher(s);
//        String s2 = m.replaceAll("");
//        //格式化
//        String format = XmlUtil.format(s2);
//        System.out.println(format);
//    }
//
//    public static void test(){
//        String xmlPath = "C:/Users/zhum/Desktop/jz.xml";
////        File file = new File(xmlPath);
////        Document document = XmlUtil.readXML(file);
////
////        //将xml转换为字符串
////        String xmlStr = XmlUtil.toStr(document);
////
////
////        long start = System.currentTimeMillis();
////        //将string字符串序列化
////        byte[] serialize = ObjectUtil.serialize(xmlStr);
////        System.out.println(serialize.length);
////
////        List<Object> list = new ArrayList<>(serialize.length);
////        for (int i = 0; i < serialize.length; i++) {
////            list.add(serialize[i]);
////        }
////
////        //存储到redis
////        String key = "test_save_jz_xml";
////        //redisUtil.del(key);
////        //redisUtil.lSet(key,list);
////
////        long end = System.currentTimeMillis();
////        System.out.println("序列化并存储到Redis用时：" + (end - start) + "毫秒");
////
////
////        //反序列化
////        List<Object> objects = redisUtil.lGet(key, 0, -1);
////        byte[] bytes = new byte[objects.size()];
////        //存到redis中的时候是Integer类型，需要先转换为byte类型
////        for (int i = 0; i < objects.size(); i++) {
////            int a = (int)objects.get(i);
////            bytes[i] = (byte)a;
////        }
////        //反序列化
////        Object deserialize = ObjectUtil.deserialize(bytes);
////        String s = String.valueOf(deserialize);
////        long end1 = System.currentTimeMillis();
////        System.out.println("从Redis中查询并反序列化用时：" + (end1 - end) + "毫秒");
//
//        //字符串转为xmlDocument
////        Document document1 = XmlUtil.parseXml(s);
//    }
//
//
//
//
//    /**
//     * @Description 根据id获得Element
//     * @author zhum
//     * @date 2021/7/30 10:16
//     * @param document
//     * @param tagName
//     * @param id
//     * @Return org.w3c.dom.Element
//     */
//    private static Element getElementById(Document document, String tagName, String id) {
//        return getElementByTagNameAndAttr(document,tagName, JzxtElementAttrNameConstants.ID,id);
//    }
//
//    /**
//     * @Description 通过标签名和属性名获得Element
//     * @author zhum
//     * @date 2021/7/30 10:21
//     * @param document
//     * @param tagName
//     * @param attrName
//     * @param attrValue
//     * @Return org.w3c.dom.Element
//     */
//    public static Element getElementByTagNameAndAttr(Document document,String tagName,String attrName,String attrValue){
//        if(document == null || StrUtil.isEmpty(tagName) || StrUtil.isEmpty(attrName) || StrUtil.isEmpty(attrValue)){
//            return null;
//        }
//        String xPath = "//" + tagName + "[@"+attrName+"='"+attrValue+"']";
//        Element elementByXPath = XmlUtil.getElementByXPath(xPath, document);
//        return elementByXPath;
//    }
//
//
//    public static void xml2Json(String xmlStr){
//        //将xml字符串转换为json，处理的不够好，节点的属性会丢失
//        JSONObject jsonObject = XML.toJSONObject(xmlStr);
//        String s1 = jsonObject.toStringPretty();
//        System.out.println(s1);
//    }
//
//
//
//
//
//
//
//
//
//
//}
