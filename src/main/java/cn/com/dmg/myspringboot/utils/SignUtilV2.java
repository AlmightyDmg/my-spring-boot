package cn.com.dmg.myspringboot.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SignUtilV2 {

    public static final String SIGN_SIGN_KEY_NEW = "x-xy-sign";
    public static final String SIGN_SIGN_TYPE_KEY_NEW = "x-xy-signtype";

    public static String signSecret = "6c670544eb8d4bf3a2b5dcdebb04293e";
    public static String x_xy_clientid = "AvMfA24zIimrzumkD3vyyUkf";
    public static String access_token = "9513361b18754d778e301965fcc1a63d";
    public static String x_xy_signtype = SignType.HMAC_SHA256.name();

    /**
     *test
     *@param args
     */
    public static void main(String[] args) {
        String method = "POST";
        String uri = "/api/rest/external/v1/create_meeting?enterpriseId=c660f7b1c086211e5a201336dc819bfcbc841901";
        String requestBody = "{\"meetingName\":\"测试会议室\",\"startTime\":1656518400000,\"endTime\":1656518400000}";
        Map<String, String> headersMap = new HashMap<>(16);
        headersMap.put("x-xy-nonce", RandomStringUtils.random(60, true, false));
        headersMap.put("x-xy-timestamp", String.valueOf(System.currentTimeMillis()));
        headersMap.put("x-xy-clientid", x_xy_clientid);
        headersMap.put("x-xy-signtype", x_xy_signtype);

        byte[] requestBodyBytes = requestBody.getBytes();
        // 加密密钥
        String encryptionKey = signSecret + "&";
        // 签名计算
        String sign = SignUtilV2.getSignV2(
                method,
                headersMap,
                uri,
                requestBodyBytes,
                encryptionKey);
        System.out.println("最终签名："+sign);
        headersMap.put("Authorization","Bearer "+access_token);
        headersMap.put(SIGN_SIGN_KEY_NEW,sign);
        System.out.println("最终header："+headersMap);
    }

    /**
     *计算签名
     * @param method
     * @param headersMap
     * @param uri
     * @param requestBodyBytes
     *@param encryptionKey 加密密钥
     * @return
     */
    private static String getSignV2(String method, Map<String, String> headersMap, String uri, byte[] requestBodyBytes, String encryptionKey) {
        if (headersMap == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        //1.添加method
        sb.append(method).append("\n");
        //header排序
        Set<String> keySet = headersMap.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        //2.添加Headers
        for (String k : keyArray) {
            if (k.equals(SIGN_SIGN_KEY_NEW)) {
                continue;
            }
            Object param = headersMap.get(k);
            String paramStr = String.valueOf(param);
            if (paramStr.trim().length() > 0) {
                // 参数值为空，则不参与签名
                sb.append(k).append("=").append(paramStr.trim()).append("&");
            }
        }
        sb = sb.deleteCharAt(sb.length()-1);//去除最后一个&
        sb.append("\n");
        //3.添加uri
        sb.append(uri);
        //4.添加请求的body
        sb.append("\n");
        if(requestBodyBytes != null && requestBodyBytes.length > 0) {
            sb.append(DigestUtils.md5Hex(requestBodyBytes));
        }else{
            sb.append(DigestUtils.md5Hex("".getBytes(Charset.forName("UTF-8"))));
        }
        //5.添加密钥（signSecret + "&"）
        sb.append("\n");
        sb.append(encryptionKey);
        System.out.println("======================sign string=============================");
        System.out.println(sb.toString());
        System.out.println("======================sign string=============================");
        String signStr = "";
        //加密
        SignType type = null;
        String signType = headersMap.get(SIGN_SIGN_TYPE_KEY_NEW);
        if (signType != null && signType.length() > 0) {
            type = SignType.valueOf(signType);
        }
        if (type == null) {
            type = SignType.MD5;
        }
        switch (type) {
            case MD5:
                signStr = DigestUtils.md5Hex(sb.toString()).toUpperCase();
                break;
            case SHA256:
                signStr = DigestUtils.sha256Hex(sb.toString()).toUpperCase();
                break;
            case HMAC_SHA256:
                signStr = sha256HMAC(sb.toString(),encryptionKey).toUpperCase();
                break;
            default:
                break;
        }
        return signStr;
    }

    /**
     * sha256_HMAC加密
     * @param message 消息
     * @param secret  秘钥
     * @return 加密后字符串
     */
    public static String sha256HMAC(String message, String secret) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes("UTF-8"));
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
        }
        return hash;
    }
    /**
     * 将加密后的字节数组转换成字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    public  static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    public enum SignType {
        MD5,
        SHA256,
        HMAC_SHA256;

        public static boolean contains(String type) {
            for (SignType typeEnum : SignType.values()) {
                if (typeEnum.name().equals(type)) {
                    return true;
                }
            }
            return false;
        }
    }
}
