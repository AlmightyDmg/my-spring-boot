package cn.com.dmg.myspringboot.test;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MiddlegroundInterTest {

    private static final String ip = "http://127.0.0.1:9999/";

    public static void main(String[] args) {
        //getResultByReqTaskId();
        word2Pdf();
    }

    public static void getResultByReqTaskId(){
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(ip + "/tech/word/getResultByTaskId/1369113999774904321");
        try {
            CloseableHttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            String s = EntityUtils.toString(entity);
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void word2Pdf(){
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost post = new HttpPost(ip + "/tech/word/word2PdfSyn");

        String jsonParam = "{\n" +
                "    \"appId\": \"2222\",\n" +
                "    \"secret\": \"\",\n" +
                "    \"courtCode\": 1984,\n" +
                "\t\"businessId\":\"2222\",\n" +
                "\t\n" +
                "    \"dataList\": [\n" +
                "        {\n" +
                "            \"thirdId\": \"\",\n" +
                "            \"wordUrl\": \"http://192.168.5.162:9000/skyvis-bucket/asposeTemplate.docx\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"thirdId\": \"\",\n" +
                "            \"wordUrl\": \"http://192.168.5.162:9000/skyvis-bucket/asposeTemplate.docx\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        try {
            StringEntity stringEntity = new StringEntity(jsonParam);
            post.setEntity(stringEntity);
            post.setHeader("X-Access-token","skyvis_interface_test_1370259927589724161");
            CloseableHttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(entity));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}
