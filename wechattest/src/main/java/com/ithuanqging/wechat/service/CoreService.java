package com.ithuanqging.wechat.service;

import com.ithuanqging.wechat.bean.TextMessage;
import com.ithuanqging.wechat.utils.MessageUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CoreService {
    public static String parseWxRequest(HttpServletRequest request) {
        // xml格式的消息数据
        String respXml = null;
        // 默认返回的文本消息内容
        String respContent = "未知的消息类型！";

        try {
            // 调用parseXml方法解析请求消息
            Map<String, String> requestMap = MessageUtil.parseXml(request);
            // 发送方帐号,一个openID
            String fromUserName = requestMap.get("FromUserName");
            // 开发者微信号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            // 接收用户发送的文本消息内容
            String content = requestMap.get("Content");
            //回复文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(System.currentTimeMillis());
            textMessage.setMsgType(MessageUtil.REQ_MESSAGE_TYPE_TEXT);

            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                respContent = "你回复的是文本消息："+content;
                textMessage.setContent(respContent);
                String xml = MessageUtil.messageToXml(textMessage);
                respXml = xml;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return respXml;
    }

}
