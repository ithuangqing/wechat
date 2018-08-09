package com.ithuangqing.wechat.service;

import com.soecode.wxtools.api.WxService;
import com.soecode.wxtools.exception.WxErrorException;

/**
 * 继承自WxService以便修改响应方法
 * 比如对token的存储
 */
public class MyWxService extends WxService {
    @Override
    public String getAccessToken() throws WxErrorException {
        /**
         * token每天调取上限位2000次
         * 为避免达到上限
         * 将获得的token以硬编码方式存储
         * 一个token的使用时长有两个小时左右
         */
        //开发测试采用,两小时过后需要调用上线采用的方法刷新获取新的token
        return "12_2sztWZDG-06oyyQ5ObBFwW1TiN3LibX7nJ1FCoxLfVpj0TOdu0YLfwQHpCvZTIgi0mavu0B9D0wPQ3DA98e2lTGTB0gQLWHftgVLk1WnKcHCeSgDluza-tUzYKxxZqWDLImTwcjg1bnuy9yqCYAbACAUQS";

        //正常上线采用
//        return super.getAccessToken();
    }
}
