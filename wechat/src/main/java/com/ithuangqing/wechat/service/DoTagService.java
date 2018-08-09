package com.ithuangqing.wechat.service;

import com.ithuangqing.wechat.utils.StringUtil;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.WxUserList;
import com.soecode.wxtools.bean.result.WxError;
import com.soecode.wxtools.bean.result.WxUserTagResult;
import com.soecode.wxtools.exception.WxErrorException;

import java.util.ArrayList;
import java.util.List;

public class DoTagService {


    public static String doTag(String openid,String token,String code){
        String resp = null;

        IService iService = new MyWxService();



        try {
            //拿token换用户信息
            WxUserList.WxUser user = iService.oauth2ToGetUserInfo(token, new WxUserList.WxUser.WxUserGet(openid, WxConsts.LANG_CHINA));

            String s = DoTagService.creatTag(openid, token, code, user);
            resp = s;
        } catch (WxErrorException e) {
            e.printStackTrace();
        }




        return resp;
    }




    //为用户创建标签的方法
    public static String creatTag(String openid,String token,String code,WxUserList.WxUser user){

        String resp = null;

        IService iService = new MyWxService();

        System.out.println("看这里：==========="+openid+token+code+user.toString());

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
            if (StringUtil.isIn(code,tagname_list)){
                System.out.println("已经存在code:"+code);
                //此时表明已存在该标签，所以需要查看用户是否已经拥有该标签，没有拥有则为其设置
                //首先获取用户基本信息，从基本信息中得到他拥有的标签
                //获得该用户拥有的标签列表
                String[] tagid_list = user.getTagid_list();
                int aspID = 0;

                for (WxUserTagResult.WxUserTag tag : tags){
                    if (tag.getName().equals(code)){
                        System.out.println("已经存在code:"+code);
                        System.out.println(tag.getName());
                        aspID = tag.getId();
                        System.out.println(aspID);
                    }
                }
                System.out.println(aspID);
                //如果用户拥有该标签ArrayUtils.contains(tagid_list,String.valueOf(aspID))
                if ( StringUtil.isIn(String.valueOf(aspID),tagid_list)){

                    resp = "你已经拥有该标签："+code;
                }else {
                    //为用户打上此标签
                    List<String> openidList = new ArrayList<>();
                    openidList.add(openid);
                    WxError wxError = iService.batchMovingUserToNewTag(openidList, aspID);
                    System.out.println(wxError.getErrmsg());
                    if (wxError.getErrmsg().equals("ok")){

                        resp = "你选择的是："+code+"已成功为你创建标签："+code+aspID;
                    }
                }
            }else {
                System.out.println("重新创建");
                WxUserTagResult userTagresult = iService.createUserTag(code);
                //2、为用户打上此标签
                List<String> openidList = new ArrayList<>();
                openidList.add(openid);
                WxError wxError = iService.batchMovingUserToNewTag(openidList, userTagresult.getTag().getId());
                System.out.println(wxError.getErrmsg());
                if (wxError.getErrmsg().equals("ok")){

                    resp = "你选择的是："+code+"已成功为你创建标签："+code;
                }
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        return resp;
    }

}
