<%@ page import="kr.co.cleanbrain.kr.co.cleanbrain.util.Util" %><%--
  Created by IntelliJ IDEA.
  User: NANDSOFT
  Date: 2019-11-21
  Time: 오후 2:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String SAMLRequest = request.getParameter("SAMLRequest");
    String RelayState = request.getParameter("RelayState");
    String issueInstant = "";
    String ServiceProvider = "";
    String acsURL = "";
    if (SAMLRequest == null || SAMLRequest.equals("null")) {
        ServiceProvider = "";
    } else {
        // 압축 후 Base64로 인코딩 된 SAMLRequest 문자열을 디코드 후 압축 해제
        String decompressedSAMLRequest = Util.decodeBase64AndDecompress(SAMLRequest);
        String[] samlRequestAttributes = Util.getRequestAttributes(decompressedSAMLRequest);
        issueInstant = samlRequestAttributes[0];
        ServiceProvider = samlRequestAttributes[1];
        acsURL = samlRequestAttributes[2];
    }
%>

<html>
<head>
    <title>Login Page</title>
</head>
<body>
<h1><%=ServiceProvider%> Service Login</h1>
<form name="IdentityProviderForm" action="http://localhost.com:8887/saml/CreateSAMLResponse" method="post">
    <input type="hidden" name="SAMLRequest" value="<%=SAMLRequest%>"/>
    <input type="hidden" name="RelayState" value="<%=RelayState%>"/>
    <input type="hidden" name="returnPage" value="<%=acsURL%>">
    username : <input type="text" name="username" id="username" size="18">
    <br />
    password : <input type="password" name="password" id="password" size="18"><br />
    <input type="submit" value="로그인">
</form>
</body></html>
