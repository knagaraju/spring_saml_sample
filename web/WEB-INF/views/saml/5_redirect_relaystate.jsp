<%@ page import="java.net.URLEncoder" %><%--
  Created by IntelliJ IDEA.
  User: NANDSOFT
  Date: 2019-11-21
  Time: 오후 5:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<%
    String loginid = (String) request.getAttribute("loginid");
    String RelayState = (String) request.getAttribute("RelayState") + "?loginid=" + loginid;
%>
<%--<body onload="document.location = '<%=RelayState%>';return true;">--%>
<body onload="document.location = '<%=RelayState%>'; return true;"
<%--<form action="<%=RelayState%>">--%>
<%--    <input type="text" name="loginid" value="<%=loginid%>" />--%>
<%--    <input type="text" name="RelayState" value="<%=RelayState%>" />--%>
<%--</form>--%>
</body>
</html>