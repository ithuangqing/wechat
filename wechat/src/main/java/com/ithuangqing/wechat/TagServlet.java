package com.ithuangqing.wechat;

import com.ithuangqing.wechat.service.MyWxService;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.bean.result.WxOAuth2AccessTokenResult;
import com.soecode.wxtools.exception.WxErrorException;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public class TagServlet extends HttpServlet {
    String code = null;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        code = req.getParameter("code");

        //TODO 再次发送用户请求
        //此处code即为要给用户打的标签
        req.setAttribute("mytag",code);
        //req.getRequestDispatcher("wechat/coreServlet").forward(req,resp);
        System.out.println("你选择的标签是："+req.getAttribute("mytag"));

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //解决乱码问题
        resp.setContentType("text/html:charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        PrintWriter writer = resp.getWriter();

        IService iService = new MyWxService();
        //拿code换token和openid
        WxOAuth2AccessTokenResult result = null;
        try {
            result = iService.oauth2ToGetAccessToken(req.getParameter("code"));

            //通过网页授权获取到的openID和token
            String access_token = result.getAccess_token();
            String openid = result.getOpenid();
            System.out.println(access_token);
            System.out.println(openid);


        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        String state = req.getParameter("state");

        System.out.println("state"+state);


        HttpSession session = req.getSession();
        session.setAttribute("token",result.getAccess_token());
        session.setAttribute("openid",result.getOpenid());


        req.getRequestDispatcher("/index.jsp").forward(req,resp);







    }
}
