package com.ithuangqing.wechat.service;

import com.ithuangqing.wechat.utils.StringUtil;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxConsts;

import com.soecode.wxtools.api.WxMessageRouter;
import com.soecode.wxtools.bean.*;
import com.soecode.wxtools.bean.result.WxError;
import com.soecode.wxtools.bean.result.WxMediaUploadResult;
import com.soecode.wxtools.bean.result.WxUserTagResult;
import com.soecode.wxtools.exception.WxErrorException;
import com.soecode.wxtools.util.xml.XStreamTransformer;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据用户请求做出相应响应
 * 核心处理类
 */

public class CoreService {
    public  String parseWxRequest(HttpServletRequest request){




        //要返回给用户的xml数据包
        String respXml = null;
        //核心业务处理接口
        IService iService = new MyWxService();
        // 创建一个路由器
        WxMessageRouter router = new WxMessageRouter(iService);
        // 微信服务器推送过来的是XML格式。
        WxXmlMessage wx = null;
        try {
            /**
             * 解析用户请求得到的数据wx
             */
            wx = XStreamTransformer.fromXml(WxXmlMessage.class, request.getInputStream());
            String content = wx.getContent();
            /**
             * 根据用户发送的content做出响应
             */
            String msgType = wx.getMsgType();

            /**
             * 首次关注，做出响应
             */
            if (msgType.equals("event")){

                //第一步：构造URL获取Code
                String oauthUrl = iService.oauth2buildAuthorizationUrl("http://dd7d9514.ngrok.io/tagservlet",WxConsts.OAUTH2_SCOPE_USER_INFO, "ithuangqing");
                System.out.println(oauthUrl);


                //上传封面图
                WxMediaUploadResult result = iService.uploadTempMedia(WxConsts.MEDIA_IMAGE,new File("F://test.jpg"));
                System.out.println(result.getMedia_id());


                //回复图文消息
                WxXmlOutNewsMessage.Item item = new WxXmlOutNewsMessage.Item();
                item.setDescription("专注于大学生在线教育！");

                item.setPicUrl("http://i65.tinypic.com/107q4j9.png");
                item.setTitle("欢迎关注！");
                item.setUrl(oauthUrl);
                //将图文转换成xml
                WxXmlOutNewsMessage build = WxXmlOutMessage.NEWS().addArticle(item).toUser(wx.getFromUserName()).fromUser(wx.getToUserName()).build();
                respXml = build.toXml();


//
//                respXml = "<xml>\n" +
//                        "  <ToUserName><![CDATA[oBVev0Xm2GoDOMkaFd4aCZ62ieQE]]></ToUserName>\n" +
//                        "  <FromUserName><![CDATA[gh_0a334c5ed294]]></FromUserName>\n" +
//                        "  <CreateTime>1532685515</CreateTime>\n" +
//                        "  <MsgType><![CDATA[news]]></MsgType>\n" +
//                        "  <ArticleCount>1</ArticleCount>\n" +
//                        "  <Articles>\n" +
//                        "    <item>\n" +
//                        "      <Title><![CDATA[测试图文标题]]></Title>\n" +
//                        "      <Description><![CDATA[这是测试图文的一些描述]]></Description>\n" +
//                        "      <PicUrl><![CDATA[http://i66.tinypic.com/2d1ttl5.png]]></PicUrl>\n" +
//                        "      <Url><![CDATA[oauthUrl/]]></Url>\n" +
//                        "    </item>\n" +
//                        "  </Articles>\n" +
//                        "</xml>";


//                //文本消息
//                WxXmlOutTextMessage build = WxXmlOutMessage.TEXT().content(oauthUrl).toUser(wx.getFromUserName()).fromUser(wx.getToUserName()).build();
//                respXml = build.toXml();


            }else
            /**
             * 文字消息响应
             */

            if (content.equals("测试图文")){
                //回复图文消息
                WxXmlOutNewsMessage.Item item = new WxXmlOutNewsMessage.Item();
                item.setDescription("这是测试图文的一些描述");
                item.setPicUrl("http://i65.tinypic.com/107q4j9.png");
                item.setTitle("测试图文标题");
                item.setUrl("https://www.baidu.com/");
                //将图文转换成xml
                WxXmlOutNewsMessage build = WxXmlOutMessage.NEWS().addArticle(item).toUser(wx.getFromUserName()).fromUser(wx.getToUserName()).build();
                respXml = build.toXml();
            }else if (content.equals("创建标签")){
                //创建标签
                content = "欢迎关注，为了给你带来更好的服务，我们推出了标签功能，以便为你推荐你更感兴趣的内容请选择你感兴趣的方向：0-ASP.NET，1-python,2-Java，3-C语言，4-C#，5-其他，回复相应数字即可！";
                WxXmlOutTextMessage build = WxXmlOutMessage.TEXT().content(content).toUser(wx.getFromUserName()).fromUser(wx.getToUserName()).build();
                respXml = build.toXml();
            }
            //asp.net标签
            else if (content.equals("0")){
                String C = CoreService.creatTag("ASP.NET", wx.getContent(),wx.getFromUserName(),wx.getToUserName());

                respXml = C;
            }
            //python标签
            else if (content.equals("1")){
                String python = CoreService.creatTag("python", wx.getContent(),wx.getFromUserName(),wx.getToUserName());
                respXml = python;
            } //java标签
            else if (content.equals("2")){
                String Java = CoreService.creatTag("Java", wx.getContent(),wx.getFromUserName(),wx.getToUserName());
                respXml = Java;
            } //C语言标签
            else if (content.equals("3")){
                String C = CoreService.creatTag("C语言", wx.getContent(),wx.getFromUserName(),wx.getToUserName());
                respXml = C;
            } //C#标签
            else if (content.equals("4")){
                String cc = CoreService.creatTag("C#", wx.getContent(),wx.getFromUserName(),wx.getToUserName());
                respXml = cc;
            }//其他标签
            else if (content.equals("5")){
                String other = CoreService.creatTag("其他", wx.getContent(),wx.getFromUserName(),wx.getToUserName());
                respXml = other;
            } else if (content.equals("666")){
                String other = CoreService.creatTag("VIP", wx.getContent(),wx.getFromUserName(),wx.getToUserName());
                respXml = other;
            }
            else if (content.equals("888")){
                String other = CoreService.creatTag("SSVIP", wx.getContent(),wx.getFromUserName(),wx.getToUserName());
                respXml = other;
            }
            else if (content.equals("查看标签")){
                //查找用户的所有标签
                try {
                    WxUserTagResult result = iService.queryAllUserTag();
                    List<WxUserTagResult.WxUserTag> tags = result.getTags();
                    String existtags = "存在的标签有：";
                    for (WxUserTagResult.WxUserTag tag : tags){
                        existtags += tag.getName()+",";
                    }
                    System.out.println(result.toString());
                    WxXmlOutTextMessage build = WxXmlOutMessage.TEXT().content(existtags).toUser(wx.getFromUserName()).fromUser(wx.getToUserName()).build();
                    respXml = build.toXml();

                } catch (WxErrorException e) {
                    e.printStackTrace();
                }
            } else {
                //文本消息
                WxXmlOutTextMessage build = WxXmlOutMessage.TEXT().content(wx.getContent()).toUser(wx.getFromUserName()).fromUser(wx.getToUserName()).build();
                respXml = build.toXml();
                System.out.println(build.getContent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        //返回xml数据包
        return respXml;
    }

    //为用户创建标签的方法
    public static String creatTag(String codeName,String content,String getFromUserName,String getToUserName){

        String respXml = null;

        IService iService = new MyWxService();

        // 微信服务器推送过来的是XML格式。
       // WxXmlMessage wx = null;
        //为用户创建Java标签
        //1、创建标签
        try {
            //首先需要判断是否已存在该标签,没有则创建
            WxUserTagResult AllUserTagresult = iService.queryAllUserTag();

            List<WxUserTagResult.WxUserTag> tags = AllUserTagresult.getTags();
            //得到标签名称数组
            String[] tagname_list = new String[tags.size()];
            for (int i=0;i<tags.size();i++){
                tagname_list[i] = tags.get(i).getName();
            }
            System.out.println(tagname_list[1]);
            //如果已经存在该标签ArrayUtils.contains(tagname_list,codeName)
            if (StringUtil.isIn(codeName,tagname_list)){
                System.out.println("已经存在");
                //此时表明已存在该标签，所以需要查看用户是否已经拥有该标签，没有拥有则为其设置
                //首先获取用户基本信息，从基本信息中得到他拥有的标签
                WxUserList.WxUser user = iService.getUserInfoByOpenId(new WxUserList.WxUser.WxUserGet(getFromUserName, WxConsts.LANG_CHINA));
                //获得该用户拥有的标签列表
                String[] tagid_list = user.getTagid_list();
                int aspID = 0;

                for (WxUserTagResult.WxUserTag tag : tags){
                    if (tag.getName().equals(codeName)){
                        aspID = tag.getId();
                    }
                }
                System.out.println(aspID);
                //如果用户拥有该标签ArrayUtils.contains(tagid_list,String.valueOf(aspID))
                if ( StringUtil.isIn(String.valueOf(aspID),tagid_list)){
                    WxXmlOutTextMessage build = WxXmlOutMessage.TEXT().content("你已经拥有该标签："+codeName).toUser(getFromUserName).fromUser(getToUserName).build();
                    respXml = build.toXml();
                }else {
                    //为用户打上此标签
                    List<String> openidList = new ArrayList<>();
                    openidList.add(getFromUserName);
                    WxError wxError = iService.batchMovingUserToNewTag(openidList, aspID);
                    System.out.println(wxError.getErrmsg());
                    if (wxError.getErrmsg().equals("ok")){
                        WxXmlOutTextMessage build = WxXmlOutMessage.TEXT().content("你回复的数字是："+content+"已成功为你创建标签："+codeName).toUser(getFromUserName).fromUser(getToUserName).build();
                        respXml = build.toXml();
                    }
                }
            }else {
                System.out.println("重新创建");
                WxUserTagResult userTagresult = iService.createUserTag(codeName);
                //2、为用户打上此标签
                List<String> openidList = new ArrayList<>();
                openidList.add(getFromUserName);
                WxError wxError = iService.batchMovingUserToNewTag(openidList, userTagresult.getTag().getId());
                System.out.println(wxError.getErrmsg());
                if (wxError.getErrmsg().equals("ok")){
                    WxXmlOutTextMessage build = WxXmlOutMessage.TEXT().content("你回复的数字是："+content+"已成功为你创建标签:"+codeName).toUser(getFromUserName).fromUser(getToUserName).build();
                    respXml = build.toXml();
                }
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        return respXml;
    }
}
