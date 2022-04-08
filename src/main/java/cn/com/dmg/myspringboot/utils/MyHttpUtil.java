package cn.com.dmg.myspringboot.utils;

import cn.hutool.http.HttpRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class MyHttpUtil {
    public static void main(String[] args) {

        String result = HttpRequest.post("http://127.0.0.1:56302/push/message/send").
                header("X-Access-Token",
                        "skyvis_interface_dGVzdA_098f6bcd4621d373cade4e832627b4f6_1643175448690").
                body("{\n" +
                        "    \"content\": \"你好你好\",\n" +
                        "    \"sender\": \"outer\",\n" +
                        "    \"receiver\": \"inner\",\n" +
                        "    \"groupId\": \"skyvis\",\n" +
                        "    \"courtCode\": 1984,\n" +
                        "    \"appKey\": \"123456789\",\n" +
                        "    \"isNeedMsgReceipt\":0\n" +
                        "}").execute().body();
        System.out.println(result);

        CloseableHttpClient client = HttpClients.createDefault();


    }


}
