//package cn.com.dmg.myspringboot.utils.aspose;
//
//
//import cn.com.dmg.myspringboot.dtos.AddText2PdfContentDto;
//import cn.com.dmg.myspringboot.dtos.AddText2PdfDto;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.aspose.pdf.*;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//public class PdfUtil {
//
//    public static void main(String[] args) throws Exception{
//
//
////        FontRepository.addLocalFontPath("C:\\Users\\zhum\\Desktop\\simsun\\simsun.ttf");
////        Font font = FontRepository.openFont("C:\\Users\\zhum\\Desktop\\simsun\\simsun.ttf");
////        Font 宋体 = FontRepository.findFont("宋体");
//
////        img2PdfAndAddText("qsz1");
////        img2PdfAndAddText("qsz2");
//       // img2PdfAndAddText("qsz3");
//        //img2PdfAndAddText("ocr");
//        //getJsonString("C:\\Users\\zhum\\Desktop\\qsz3.txt");
//
//        getPdfWidthHeight();
//
//    }
//
//    public static void getPdfWidthHeight(){
//        Document pdfDocument = new Document("C:\\Users\\zhum\\Desktop\\2022年物业费发票.pdf");
//        PageInfo pageInfo = pdfDocument.getPageInfo();
//        double width = pageInfo.getWidth();
//        double height = pageInfo.getHeight();
//        log.info("PDF文档的宽度为：{}，高度为：{}",width,height);
//    }
//
//
//
//    public static void img2PdfAndAddText(String imgName) throws Exception{
//        String absolutePath = "C:\\Users\\zhum\\Desktop\\";
//        //获得ocr识别的json
//        String jsonPath = absolutePath + imgName + ".txt";
//        AddText2PdfDto addText2PdfDto = getAddText2PdfDto(jsonPath);
//        //将图片转换为pdf
//        String imgPath = absolutePath + imgName + ".jpg";
//        String targetPdfPath = absolutePath + imgName + ".pdf";
//        ConvertAnImageToPDF.pdfImageApproach(imgPath,targetPdfPath,addText2PdfDto.getPageWidth(),addText2PdfDto.getPageHeight());
//
//        //向转换完成的pdf中加入文字，生成双层pdf
//        String doublePdfPath = absolutePath + imgName + "_double.pdf";
//        addText(addText2PdfDto,targetPdfPath,doublePdfPath);
//    }
//
//
//
//    public static void addText(AddText2PdfDto addText2PdfDto, String sourcePdfPath, String doublePdfPath){
//        if (!LicenseUtil.getLicense()) {
//            return;
//        }
//
//
//        // open document
//        Document pdfDocument = new Document(sourcePdfPath);
//        PageInfo pageInfo = pdfDocument.getPageInfo();
//        double width = pageInfo.getWidth();
//        double height = pageInfo.getHeight();
//        log.info("PDF文档的宽度为：{}，高度为：{}",width,height);
//
//        Integer pageWidth = addText2PdfDto.getPageWidth();
//        Integer pageHeight = addText2PdfDto.getPageHeight();
//
//        double propX = width / pageWidth;
//        double propY = height / pageHeight;
//        log.info("根据width算出的缩放比例为{}，根据height算出的缩放比例为：{}",propX,propY);
//
//
//
//
//
//
//        // get particular page
//        Page pdfPage = pdfDocument.getPages().get_Item(1);
//
//
//        // create TextBuilder object
//        TextBuilder textBuilder = new TextBuilder(pdfPage);
//        List<AddText2PdfContentDto> contents = addText2PdfDto.getContents();
//        for (AddText2PdfContentDto content : contents) {
//            // create text fragment
//            TextFragment textFragment = new TextFragment(content.getContent());
//
//            //偏移量
//            double offset = content.getHeight() * propX;
//            log.info("计算后的位置的偏移量为{}",offset);
//
//            Position position = new Position(propX * content.getX(), height - (propY * content.getY() + offset));
//            textFragment.setPosition(position);
//
//            // set text properties
//            Font font = FontRepository.openFont("C:\\Users\\zhum\\Desktop\\simsun\\simsun.ttf");
//            textFragment.getTextState().setFont(font);
//            //textFragment.getTextState().setFont(FontRepository.findFont("宋体"));
//            //textFragment.getTextState().setForegroundColor(Color.getTransparent());
//            //textFragment.getTextState().setForegroundColor(Color.getWhite());
//
//            //获得字体大小
//            Integer fontSize = getFontSize(new Double(content.getHeight() * propX).intValue());
//            log.info("设置字体的大小为{},文字内容为：{}",fontSize,content.getContent());
//
//            textFragment.getTextState().setFontSize(fontSize);
//            textFragment.getTextState().setForegroundColor(Color.getBlue());
//            //textFragment.getTextState().setStrokingColor(Color.getTransparent());
//            //textFragment.getTextState().setBackgroundColor(Color.getGray());
//            //textFragment.setZIndex(-10);
//            //将文字设置为不可见
//            textFragment.getTextState().setInvisible(true);
//
//            // append the text fragment to the PDF page
//            textBuilder.appendText(textFragment);
//        }
//
//        // save updated PDF file
//        pdfDocument.save(doublePdfPath);
//    }
//
//
//
//    /**
//     * @Description 将识别后的json统一封装成一种格式
//     * @author zhum
//     * @date 2021/8/31 10:20
//     * @param
//     * @Return cn.com.dmg.myspringboot.dtos.AddText2PdfDto
//     */
//    public static AddText2PdfDto getAddText2PdfDto(String jsonPath) throws Exception{
//        String jsonString = getJsonString(jsonPath);
//        JSONObject jsonObject = JSONObject.parseObject(jsonString);
//        JSONObject task_result = jsonObject.getJSONObject("task_result");
//        JSONArray page = task_result.getJSONArray("page");
//        AddText2PdfDto addText2PdfDto = new AddText2PdfDto();
//        for (int i = 0; i < page.size(); i++) {
//            JSONObject jsonObject1 = page.getJSONObject(i);
//            JSONObject rect = jsonObject1.getJSONObject("rect");
//            addText2PdfDto.setPageWidth(rect.getInteger("w"));
//            addText2PdfDto.setPageHeight(rect.getInteger("h"));
//            JSONArray textRegion = jsonObject1.getJSONArray("TextRegion");
//            for (int i1 = 0; i1 < textRegion.size(); i1++) {
//                JSONObject jsonObject2 = textRegion.getJSONObject(i1);
//                JSONArray textline = jsonObject2.getJSONArray("textline");
//                List<AddText2PdfContentDto> list = new ArrayList<>();
//                for (int i2 = 0; i2 < textline.size(); i2++) {
//                    JSONObject jsonObject3 = textline.getJSONObject(i2);
//                    JSONObject rect1 = jsonObject3.getJSONObject("rect");
//                    AddText2PdfContentDto addText2PdfContentDto = new AddText2PdfContentDto();
//                    addText2PdfContentDto.setX(rect1.getInteger("x"));
//                    addText2PdfContentDto.setY(rect1.getInteger("y"));
//                    addText2PdfContentDto.setWidth(rect1.getInteger("w"));
//                    addText2PdfContentDto.setHeight(rect1.getInteger("h"));
//                    //文字高度作为偏移量
//                    if(addText2PdfDto.getOffset() == null){
//                        addText2PdfDto.setOffset(rect1.getInteger("h"));
//                    }
//                    JSONArray sents = jsonObject3.getJSONArray("sent");
//                    JSONObject jsonObject4 = sents.getJSONObject(0);
//                    addText2PdfContentDto.setContent(jsonObject4.getString("value"));
//                    list.add(addText2PdfContentDto);
//                }
//                addText2PdfDto.setContents(list);
//            }
//        }
//
//        return addText2PdfDto;
//    }
//
//
//    public static String getJsonString(String jsonPath) throws Exception{
//        //File file = new File("C:\\Users\\zhum\\Desktop\\ocr2.txt");
//        File file = new File(jsonPath);
//
//        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
//
//        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//        StringBuffer stringBuffer = new StringBuffer();
//        String lineTXT;
//        while ((lineTXT = bufferedReader.readLine()) != null) {
//            stringBuffer.append(lineTXT.trim());
//        }
//        bufferedReader.close();
//
//        JSONObject jsonObject = JSONObject.parseObject(stringBuffer.toString());
//        log.info(jsonObject.getString("data"));
//
//        return stringBuffer.toString();
//    }
//
//    public static Integer getFontSize(Integer height){
//        /*
//            对应关系
//            9   12px
//            10.5 14px
//            12 16px
//            14 18.7px
//            15 20px
//            16 21.3px
//            @author zhum
//            @date 2021/8/31 14:48
//         */
//        Integer fontSize;
//        if(height < 12){
//            fontSize = 8;
//        } else if(12 <= height && height < 14){
//            fontSize = 9;
//        } else if(14 <= height &&  height < 16){
//            fontSize = 11;
//        } else if(16 <= height &&  height < 18.7){
//            fontSize = 12;
//        } else if(18.7 <= height &&  height < 20){
//            fontSize = 14;
//        } else if(20 <= height &&  height < 21.3){
//            fontSize = 14;
//        } else {
//            fontSize = 16;
//        }
//
//        //
//        return fontSize + 4;
//    }
//
//
//
//
//}
