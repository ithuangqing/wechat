经过上一篇的讲解，我们已经知道如何响应用户发送的文本消息了，对于与微信公众号的互动这块，就是你发个用户什么，需要用户响应给你什么，这里记住关键一点就是数据的传输都是通过XML，只要正确解析微信请求中的XML数据包就能知道用户发送的是什么，然后只要你返回给微信服务器正确的XML数据，用户就能收到正确的响应。

接下来要说的是，如何调用官方技术文档中的API去获得数据，这里讲主要的两种方式，也就是Get请求和Post请求。

对于Get请求，我们已获取access_token为例，首先看官方技术文档

**接口调用请求说明**

```
https请求方式: GET
https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
```

**参数说明**

| 参数       | 是否必须 | 说明                                  |
| ---------- | -------- | ------------------------------------- |
| grant_type | 是       | 获取access_token填写client_credential |
| appid      | 是       | 第三方用户唯一凭证                    |
| secret     | 是       | 第三方用户唯一凭证密钥，即appsecret   |

**返回说明**

正常情况下，微信会返回下述JSON数据包给公众号：

```
{"access_token":"ACCESS_TOKEN","expires_in":7200}
```

**参数说明**

| 参数         | 说明                   |
| ------------ | ---------------------- |
| access_token | 获取到的凭证           |
| expires_in   | 凭证有效时间，单位：秒 |

那依据这个文档我们该如何在编码中通过get请求获取access_token 呢？首先需要获取到这个API接口，也就是这个URL

```java
String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
```

也就是一个字符串，现在这个字符串中可是缺少参数的，那么该怎么填充这些参数，这就牵涉都URL的拼接，那么该如何拼接呢？如下

```java 
String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        
        String requestUrl = token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
```

以上就是对URL的一个拼接。

那么该如何发起请求获得数据呢？比如这个获取access_token的请求，当你成功发起请求，返回的数据是json，这就必然牵涉到对json数据的解析，我们需要将json数据解析成我们能用的数据，这里一般解析成Java对象。

现在梳理一下，比如你编写代码发起一个get请求，然后成功得到返回的json数据，然后对json数据进行解析，得到我们想要的数据。

# http请求工具类



现在有这么一个工具类

```Java
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
}
```

这个工具类可以发送http请求，包括get和post两种方式，你只要传入正确的URL还有请求方式，如果是post请求添加正确的json数据，这个json数据也就是你post请求提交的数据，然后这个工具类就会将请求返回的json数据封装成一个JSONObject对象，然后你就可以**通过JSONObject.get(key)的方式获取json对象的属性值**了。

比如这里的获取access_token

## 发起Get请求获取数据



```java 
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
```

这段代码不难理解，首先拼接正确的URL，然后请求方式是get，所以第三个参数不用填，因为没有要提交的数据，所以发起请求就是这样

```java 
JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
```

然后就返回给你一个JSONObject对象，就可以通过getkey的方式获取其中的属性值了。

## 发起Post请求获取数据

那么如果请求是Post的该怎样操作呢？相比于get请求就是在Post请求中需要添加提交的数据，而这个提交当然数据也是json数据，可以这样操作

```Java
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
```

这个以微信公众号创建标签为例

```
http请求方式：POST（请使用https协议）
https://api.weixin.qq.com/cgi-bin/tags/create?access_token=ACCESS_TOKEN
```

POST数据格式：JSON 
POST数据示例：

```
{   "tag" : {     "name" : "广东"//标签名   } }
```

**参数说明**

| 参数         | 说明                   |
| ------------ | ---------------------- |
| access_token | 调用接口凭据           |
| name         | 标签名（30个字符以内） |

**返回说明（正常时返回的json数据包示例）**

```
{   "tag":{ "id":134,//标签id "name":"广东"   } }
```

这里已经给出了提交json数据的示例，那么除了拼接正确的URL，因为是Post请求还要添加请求数据，就是这样发起请求了

```java 
//提交的json数据
        String jsonData = "{\"tag\":{\"name\":\"%s\"}}";
        JSONObject jsonObject = HttpUtil.httpsRequest(requestUrl, "POST", String.format(jsonData,labelname));
```

这里主要就是对提交的json数据做处理，工具类的第三个参数就是填写提交的json数据，只不过需要填写我们要提交的数据，比如这里的json数据是这样

```java 
//提交的json数据
        String jsonData = "{\"tag\":{\"name\":\"%s\"}}";
```

其实这里的%s就可以直接换成我们想要创建的标签的名字，然后直接将jsonData作为第三个参数传入，只不过这里使用到了

```java 
String.format(jsonData,labelname)
```

相比你也知道这样处理的作用及目的，详细的用法可以搜索String.format的用法即可！



以上就是给你一个API接口，我们该怎么调用获取数据的说明了，当然，针对的数据格式是json，这在实际的开发中用到的非常多，所以一定要熟悉！ 