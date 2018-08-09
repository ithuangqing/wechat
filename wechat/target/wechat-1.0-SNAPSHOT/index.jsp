<%--
  Created by IntelliJ IDEA.
  User: ithuangqing
  Date: 2018/7/28
  Time: 14:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="apple-touch-fullscreen" content="YES">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <title>如鹏网--专注大学生在线教育</title>
</head>
<body>
<img src="http://i66.tinypic.com/2d1ttl5.png">

<form action="/dotag" method="post">
    <h2>１请问你对以下哪方面最感兴趣呢？</h2>

    <input type="radio" name="code" value="Java" id="1"/>
    <label for="1">Java</label>
    <input type="radio" name="code" value="python" id="2"/>
    <label for="2">python</label>
    <input type="radio" name="code" value=".net" id="3"/>
    <label for="3">python</label>
    <h2>2 请问您还对以下哪些技术感兴趣呢？（多选）</h2>


    <input type="checkbox" name="hobby" /> C
    <input type="checkbox" name="hobby" /> C++
    <input type="checkbox" name="hobby" /> PHP
    <input type="checkbox" name="hobby" /> Android
    <input type="checkbox" name="hobby" /> 其他


    <input type="submit" value="提交" />
</form>

</body>
</html>