package cn.com.dmg.myspringboot.utils.aspose;

import com.aspose.pdf.License;

import java.io.InputStream;

public class LicenseUtil {

    public static boolean getLicense() {
        boolean result = false;
        try {
            //  license.xml应放在..\WebRoot\WEB-INF\classes路径下
            InputStream is = PdfUtil.class.getClassLoader().getResourceAsStream("Aspose.license.lic");
            License asposeLic = new License();
            asposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
