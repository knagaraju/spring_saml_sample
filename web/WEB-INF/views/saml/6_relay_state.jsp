<%--
  Created by IntelliJ IDEA.
  User: NANDSOFT
  Date: 2019-11-25
  Time: 오후 5:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Relay State</title>
</head>
<%
    String userid = (String) request.getParameter("loginid");
    // String RelayState = (String) request.getAttribute("RelayState");
%>
<body>
    <p>'<%=userid%>' Welcome Service Page!</p>
</body>
</html>
