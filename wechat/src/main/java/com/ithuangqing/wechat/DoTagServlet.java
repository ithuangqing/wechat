package com.ithuangqing.wechat;

import com.ithuangqing.wechat.service.DoTagService;
import com.ithuangqing.wechat.service.MyWxService;
import com.soecode.wxtools.api.IService;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public class DoTagServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");

        HttpSession session = req.getSession();
        String token = (String) session.getAttribute("token");
        String openid = (String) session.getAttribute("openid");
        String code = req.getParameter("code");
        IService iService = new MyWxService();
        PrintWriter writer = resp.getWriter();

        String s = DoTagService.doTag(openid, token, code);

        writer.print(s);


    }
}
