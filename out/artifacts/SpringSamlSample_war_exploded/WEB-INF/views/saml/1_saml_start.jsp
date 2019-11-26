<%--
  Created by IntelliJ IDEA.
  User: NANDSOFT
  Date: 2019-11-21
  Time: 오후 2:39
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form name="ServiceProviderForm" action="http://localhost.com:8887/saml/CreateSAMLRequest" method="post">
    <input type="hidden" name="loginForm" value="http://localhost.com:8887/saml/3_login_form" />
    <input type="hideen" name="providerName" value="localhost.com" />
    <input type="hidden" name="RelayState" value="http://localhost.com:8887/saml/6_relay_state" />
    <input type="hidden" name="acsURL" value="http://localhost.com:8887/saml/ProcessACSWork" />
    <input type="submit" value="SAML Authentication START">
</form>