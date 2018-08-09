package com.ithuanqging.wechat.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.ithuanqging.wechat.bean.ExistLabelInfo;
import com.ithuanqging.wechat.bean.NewLabel;
import com.ithuanqging.wechat.bean.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类名: HttpUtil </br>
 * 描述: 通用工具类 </br>
 */
public class HttpUtil {
    private static Logger log = LoggerFactory.getLogger(HttpUtil.class);
    /**
     * 发送https请求
     *
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            //jsonObject = JSONObject.fromObject(buffer.toString());
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (ConnectException ce) {
            log.error("连接超时：{}", ce);
        } catch (Exception e) {
            log.error("https请求异常：{}", e);
        }
        return jsonObject;
    }
    /**
     * 获取接口访问凭证
     *
     * @param appid 凭证
     * @param appsecret 密钥
     * @return
     */
    public static Token getToken(String appid, String appsecret) {
        // 凭证获取（GET）
        String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        Token token = null;
        String requestUrl = token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
        // 发起GET请求获取凭证
        JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {
            try {
                token = new Token();
                token.setAccessToken(jsonObject.getString("access_token"));
                token.setExpiresIn(jsonObject.getInteger("expires_in"));
            } catch (JSONException e) {
                token = null;
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInteger("errcode"), jsonObject.getString("errmsg"));
            }
        }
        return token;
    }


    /**
     * 1、创建新的标签
     * @param labelname 只能创建一次，也就是标签唯一
     * @param token
     * @return
     */
    public static NewLabel creatNewLabel(String labelname, String token){
        NewLabel newLabel = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/tags/create?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", token);
        //提交的json数据
        String jsonData = "{\"tag\":{\"name\":\"%s\"}}";
        JSONObject jsonObject = HttpUtil.httpsRequest(requestUrl, "POST", String.format(jsonData,labelname));

        if (null != jsonObject){
            newLabel = new NewLabel();
            int id = jsonObject.getJSONObject("tag").getInteger("id");
            String name = jsonObject.getJSONObject("tag").getString("name");
            newLabel.setId(id);
            newLabel.setLabelname(name);

        }else if (0 != jsonObject.getInteger("errcode")){
            System.out.println("创建菜单失败 errcode:{} errmsg:{}"+jsonObject.getInteger("errcode")+jsonObject.getString("errmsg"));
        }
        return newLabel;
    }

    /**
     * 2、获取公众号已创建的标签
     * 返回一个标签列表
     */
    public static List<ExistLabelInfo> getAllLabel(String token){

        List LabelInfoList = new ArrayList<>();

        //拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", token);
        JSONObject jsonObject = HttpUtil.httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {

            try {
                JSONArray tags1 = jsonObject.getJSONArray("tags");
                LabelInfoList = JSON.parseArray(tags1.toJSONString(), ExistLabelInfo.class);


            } catch (JSONException e) {

                LabelInfoList = null;

                int errorCode = jsonObject.getInteger("errcode");

                String errorMsg = jsonObject.getString("errmsg");

                System.out.println("获取标签失败"+errorCode+errorMsg);
            }
        }
        return LabelInfoList;
    }


}
