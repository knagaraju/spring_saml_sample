<%--
  Created by IntelliJ IDEA.
  User: NANDSOFT
  Date: 2019-11-21
  Time: 오후 4:14
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html; charset=UTF-8"%>
<%
    String acsURL = (String) request.getAttribute("acsURL");
    String SAMLResponse = (String) request.getAttribute("SAMLResponse");
    String signedSAMLResponse = (String) request.getAttribute("signedSAMLResponse");
    String RelayState = (String) request.getAttribute("RelayState");
    String authstatus = (String) request.getAttribute("authstatus");
%>
<script>
    alert('authstatus : <%=authstatus%>');
</script>
<%
    if (authstatus == null) authstatus = "FAIL";
    if (RelayState == null) RelayState = "";
%>
<html>
<head>
    <title>forward to ACS</title>
</head>
<%
    if (SAMLResponse != null && authstatus.equals("SUCCESS")) {
%>
<body onload="javascript:document.acsForm.submit();">
<form name="acsForm" action="<%=acsURL%>" method="post">
    <div style="display: none">
        <textarea rows=10 cols=80 name="SAMLResponse"><%=SAMLResponse%></textarea>
        <textarea rows=10 cols=80 name="RelayState"><%=RelayState%></textarea>
        <textarea rows=10 cols=80 name="signedSAMLResponse"><%=signedSAMLResponse%></textarea>
    </div>
</form>
<%
} else {
%><script>alert('Login error'); history.back(-2); </script><%
    }
%>
</body>
</html>
