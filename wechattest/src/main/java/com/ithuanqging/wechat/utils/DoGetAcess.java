package com.ithuanqging.wechat.utils;

import com.ithuanqging.wechat.bean.ExistLabelInfo;
import com.ithuanqging.wechat.bean.NewLabel;
import com.ithuanqging.wechat.bean.Token;

import java.util.List;

public class DoGetAcess {


    public static void main(String[] args) {

        String appID = "wx930348e81404aae7";
        String appsecret = "763c62f8a9ee1d28ad12f2b6d7b364cc";

        Token token = HttpUtil.getToken(appID, appsecret);
        System.out.println(token.getAccessToken());

//        NewLabel newLabel = HttpUtil.creatNewLabel("新标签", token.getAccessToken());
//        System.out.println(newLabel);
        List<ExistLabelInfo> allLabel = HttpUtil.getAllLabel(token.getAccessToken());
        for (ExistLabelInfo info:allLabel){
            System.out.println(info.getName());

        }
    }
}
