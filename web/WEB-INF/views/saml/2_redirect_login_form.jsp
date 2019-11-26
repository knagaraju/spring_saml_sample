<%--
  Created by IntelliJ IDEA.
  User: NANDSOFT
  Date: 2019-11-21
  Time: 오후 2:35
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.net.*"%>
<%
    String error = (String) request.getAttribute("error");
    String SAMLRequest = (String) request.getAttribute("SAMLRequest");
    String redirectURL = (String) request.getAttribute("redirectURL");
%>
<html><head> <meta http-equiv="content-type" content="text/html; charset=UTF-8"><title>SAML-based Single Sign-On Service </title></head>
<script>
    alert('<%=SAMLRequest%>');
</script>
<%
    if (error != null) {
%>
<body><center><font color="red"><b><%= error %></b></font></center><p>
<%
    } else {
        if (SAMLRequest != null && redirectURL != null) {
%>
    <body onload="document.location = '<%=redirectURL%>';return true;">
    <h1 style="margin-bottom:6px">Submitting login request to Identity provider</h1>
<%
       } else {
%>
    <body>
    <center><font color="red"><b>no SAMLRequest or redirectURL</b></font></center><p>
<%
       }
     }
%>
</body></html>