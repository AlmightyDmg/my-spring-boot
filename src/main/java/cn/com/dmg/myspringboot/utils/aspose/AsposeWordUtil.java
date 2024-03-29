package cn.com.dmg.myspringboot.utils.aspose;

import com.aspose.words.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AsposeWordUtil {
    public static void main(String[] args) {
        // 验证License 若不验证则转化出的pdf文档会有水印产生
        if (!getLicense()) {
            return;
        }

        testGenerateWordByTemp();

    }

    public static void testGenerateWordByTemp(){
        try {
            FileInputStream fs = new FileInputStream("C:\\Users\\zhum\\Desktop\\智能会议系统使用手册(V3.0.0).docx");
            String targetPath = "C:\\Users\\zhum\\Desktop\\dddd.docx";
            Map<String,Object> map = new HashMap<>();
            map.put("{使用手册}","1111");
            map.put("{sysc}","2222");
            generateWordByTemp(fs,targetPath,map);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void testWord2Pdf(){
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                String pdfPath = "C:\\Users\\zhum\\Desktop\\ws1-aspose" + UUID.randomUUID() + ".pdf";
                word2Pdf("C:\\Users\\zhum\\Desktop\\ws1.docx", pdfPath);
            }).start();
        }
    }

    /**
     * @Description: TODO 根据模板生成word文件
     * @author: zhum
     * @date: 2021/2/18 10:49
     * @param
     * @Return: void
     */
    public static void generateWordByTemp(InputStream wordInputStream, String targetWordPath, Map<String,Object> tagAndValueMap){
        try {
            if (!getLicense()) {
                return;
            }
            Document doc = new Document(wordInputStream);
            Range range = doc.getRange();
            FindReplaceOptions options = new FindReplaceOptions();
            //区分大小写
            options.setMatchCase(true);
            //True表示旧值必须是独立字。
            //options.setFindWholeWordsOnly(true);
            if(tagAndValueMap != null) {
                tagAndValueMap.forEach((tag, value)->{
                    try {
                        range.replace(tag,String.valueOf(value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            doc.save(targetWordPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: TODO word转pdf 格式不会丢失
     * @author: zhum
     * @date: 2021/2/18 11:29
     * @param
     * @Return: void
     */
    public static void word2Pdf(String wordPath,String pdfPath){
        try {
            FileInputStream fi = new FileInputStream(wordPath);
            word2Pdf(fi,pdfPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: TODO word转pdf
     * @author: zhum
     * @date: 2021/2/23 16:00
     * @param wordInputStream word的input流
     * @param pdfPath pdf文件位置
     * @Return: void
     */
    public static void word2Pdf(InputStream wordInputStream,String pdfPath){
        try {
            // 验证License 若不验证则转化出的pdf文档会有水印产生
            if (!getLicense()) {
                return;
            }
            //long old = System.currentTimeMillis();
            //新建一个pdf文档
            File file = new File(pdfPath);
            FileOutputStream os = new FileOutputStream(file);
            //Address是将要被转化的word文档
            Document doc = new Document(wordInputStream);
            //全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            doc.save(os, com.aspose.words.SaveFormat.PDF);
            //long now = System.currentTimeMillis();
            os.close();
            //转化用时
            //System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description 获得文档的页数
     * @author zhum
     * @date 2021/9/6 10:42
     * @param inputStream
     * @Return java.lang.Integer
     */
    public static Integer getDocPageCount(InputStream inputStream){
        if (!getLicense()) {
            return null;
        }
        Integer count = null;
        try {
            Document document = new Document(inputStream);
            count = document.getPageCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public static Integer getDocPageCount(byte[] fileBytes) {
        InputStream is = new ByteArrayInputStream(fileBytes);
        return getDocPageCount(is);
    }


    /**
     * @Description: TODO 插入印章  这个相当于是插入图片了，还是能够复制出来
     * @author: zhum
     * @date: 2021/2/18 10:51
     * @param
     * @Return: void
     */
    public static void addSeal2Word(){
        String wordPath = "C:\\Users\\zhum\\Desktop\\ws1.docx";
        String imagePath = "C:\\Users\\zhum\\Desktop\\1111111.png";
        String saveDocPath = "C:\\Users\\zhum\\Desktop\\aspose-waterMark.docx";


        try {
            Document doc = new Document(wordPath);
            Shape watermark = new Shape(doc, ShapeType.IMAGE);
            watermark.getImageData().setImage(imagePath);
            // 水印大小
            //watermark.setWidth(150);
            //watermark.setHeight(30);
            //锁定形状的横纵比
            watermark.setAspectRatioLocked(true);
            // 左下到右上
            //watermark.setRotation(-20);
            //设置位置 x,y
            watermark.setDistanceLeft(500);
            watermark.setDistanceBottom(500);
            //设置在文字上方还是下方
            watermark.setBehindText(true);


            Paragraph watermarkPara = new Paragraph(doc);
            watermarkPara.appendChild(watermark);

            for (Section sect : doc.getSections()) {
//                Body body = sect.getBody();
//                body.appendChild(watermarkPara.deepClone(true));
                //------------以下方法是插入在页眉或者页脚
                int headerType = HeaderFooterType.HEADER_PRIMARY;
                HeaderFooter header = sect.getHeadersFooters().getByHeaderFooterType(headerType);
                if (header == null) {
                    header = new HeaderFooter(sect.getDocument(), headerType);
                    sect.getHeadersFooters().add(header);
                }
                try {
                    header.appendChild(watermarkPara.deepClone(true));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            doc.save(saveDocPath);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static void addWaterMark2Doc(){
        String wordPath = "C:\\Users\\zhum\\Desktop\\ws1.docx";
        String imagePath = "C:\\Users\\zhum\\Desktop\\1111111.png";
        String saveDocPath = "C:\\Users\\zhum\\Desktop\\aspose-waterMark.docx";
        try {
            Document doc = new Document(wordPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean getLicense() {
        boolean result = false;
        try {
            //  license.xml应放在..\WebRoot\WEB-INF\classes路径下
            InputStream is = AsposeWordUtil.class.getClassLoader().getResourceAsStream("Aspose.license.lic");
            License asposeLic = new License();
            asposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



}
