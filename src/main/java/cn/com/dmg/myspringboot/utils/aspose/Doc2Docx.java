package cn.com.dmg.myspringboot.utils.aspose;

import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import org.springframework.util.FileCopyUtils;

import java.io.*;

public class Doc2Docx {
    public static void main(String[] args) throws Exception {
        String srcfile = "C:\\Users\\zhum\\Desktop\\1111.doc";
        String targetfile = "C:\\Users\\zhum\\Desktop\\1111.docx";
        ByteArrayToFile(convertDocIs2DocxIs( new FileInputStream(new File(srcfile))),targetfile) ;
    }
    // 字节数组到文件的过程
    public static void ByteArrayToFile(byte[] data,String newFileNmae) {
        File file = new File(newFileNmae);
        //选择流
        FileOutputStream fos = null;
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(data);
            fos = new FileOutputStream(file);
            int temp;
            byte[] bt = new byte[1024*10];
            while((temp = bais.read(bt))!= -1) {
                fos.write(bt,0,temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关流
            try {
                if(null != fos)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 将doc输入流转换为docx输入流
    private static byte[] convertDocIs2DocxIs(InputStream docInputStream) throws IOException {
        byte[] docBytes = FileCopyUtils.copyToByteArray(docInputStream);
        byte[] docxBytes = convertDocStream2docxStream(docBytes);
        return docxBytes;
    }
    // 将doc字节数组转换为docx字节数组
    private static byte[] convertDocStream2docxStream(byte[] arrays) {
        byte[] docxBytes = new byte[1];
        if (arrays != null && arrays.length > 0) {
            try (
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    InputStream sbs = new ByteArrayInputStream(arrays)
            ) {
                getLicense();
                com.aspose.words.Document doc = new com.aspose.words.Document(sbs);
                doc.save(os, SaveFormat.DOCX);
                docxBytes = os.toByteArray();
            } catch (Exception e) {
                System.out.println("出错啦");
            }
        }
        return docxBytes;
    }


    public static boolean getLicense() {
        boolean result = false;
        try {
            //  license.xml应放在..\WebRoot\WEB-INF\classes路径下
            InputStream is = Doc2Docx.class.getClassLoader().getResourceAsStream("Aspose.license.lic");
            License asposeLic = new License();
            asposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
