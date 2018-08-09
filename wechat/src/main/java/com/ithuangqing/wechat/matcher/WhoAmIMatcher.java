package com.ithuangqing.wechat.matcher;

import com.soecode.wxtools.api.WxMessageMatcher;
import com.soecode.wxtools.bean.WxXmlMessage;
import com.soecode.wxtools.util.StringUtils;

public class WhoAmIMatcher implements WxMessageMatcher {
    @Override
    public boolean match(WxXmlMessage message) {
        if(StringUtils.isNotEmpty(message.getContent())){
            if(message.getContent().equals("我是谁")){
                return true;
            }
        }
        return false;
    }
}