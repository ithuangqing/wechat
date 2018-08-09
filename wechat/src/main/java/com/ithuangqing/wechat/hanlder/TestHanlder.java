package com.ithuangqing.wechat.hanlder;

import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxMessageHandler;
import com.soecode.wxtools.bean.WxXmlMessage;
import com.soecode.wxtools.bean.WxXmlOutMessage;
import com.soecode.wxtools.bean.WxXmlOutTextMessage;
import com.soecode.wxtools.exception.WxErrorException;

import java.util.Map;

public class TestHanlder implements WxMessageHandler {

    @Override
    public WxXmlOutMessage handle(WxXmlMessage wxMessage, Map<String, Object> context, IService iService) throws WxErrorException {


        return WxXmlOutMessage.TEXT().content("欢迎关注！").toUser(wxMessage.getFromUserName()).fromUser(wxMessage.getToUserName()).build();
    }
}
