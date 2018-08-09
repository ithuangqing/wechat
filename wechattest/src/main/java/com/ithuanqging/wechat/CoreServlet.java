package com.ithuanqging.wechat;

import com.ithuanqging.wechat.service.CoreService;
import com.ithuanqging.wechat.service.MyWxService;
import com.ithuanqging.wechat.utils.SignUtil;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxService;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 核心servlet，处理微信服务器发送过来的消息
 */
public class CoreServlet extends HttpServlet {


    private IService iService = new WxService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 微信加密签名
        String signature = req.getParameter("signature");
        // 时间戳
        String timestamp = req.getParameter("timestamp");
        // 随机数
        String nonce = req.getParameter("nonce");
        // 随机字符串
        String echostr = req.getParameter("echostr");
        PrintWriter out = resp.getWriter();
        if (iService.checkSignature(signature, timestamp, nonce,echostr)) {
            out.print(echostr);
        }
        out.close();
        out = null;
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        CoreService coreService = new CoreService();
        String s = coreService.parseWxRequest(req);

        // 响应消息，将相应的xml数据转发给微信服务器
        out.print(s);
        System.out.println("消息："+s);
        out.close();
    }


}