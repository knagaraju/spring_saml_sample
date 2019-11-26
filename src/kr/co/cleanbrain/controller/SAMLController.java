package kr.co.cleanbrain.controller;

import kr.co.cleanbrain.exception.SAMLException;
import kr.co.cleanbrain.kr.co.cleanbrain.util.Util;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import static kr.co.cleanbrain.kr.co.cleanbrain.util.Util.decodeBase64AndDecompress;
import static kr.co.cleanbrain.kr.co.cleanbrain.util.Util.getRequestAttributes;

/**
 * Created with IntelliJ IDEA.
 * User: 노상현
 * Date: 2019-11-21
 * SAML Controller
 */

@Controller
public class SAMLController {

    @RequestMapping(value="/saml/SamlStart", method={RequestMethod.GET})
    public String samlStart() {
        return "/saml/1_saml_start";
    }

    @RequestMapping(value="/saml/CreateSAMLRequest", method={RequestMethod.POST})
    public String createSAMLRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String loginForm = request.getParameter("loginForm");
        String providerName = request.getParameter("providerName");
        String RelayState = request.getParameter("RelayState");
        String acsURL = request.getParameter("acsURL");

        // SAML Request XML 생성(SAMLRequestTemplate.xml 템플릿 이용)
        String SAMLRequest = createSAMLRequest(request, acsURL, providerName);
        request.setAttribute("SAMLRequest", SAMLRequest);

        // IDP(Identity Provider)에 SAML Request 전달되도록 URL 생성)
        // IDP 가기 전 로그인 페이지에서 사용자 계정 정보 입력하므로 2_redirect_login_form
        // 페이지에서 로그인 페이지로 redirect
        String redirectURL = makeURL(loginForm, SAMLRequest, RelayState);
        request.setAttribute("redirectURL", redirectURL);

        return "/saml/2_redirect_login_form";
    }

    // 사용자 로그인 화면 이동
    @RequestMapping(value="/saml/3_login_form", method=RequestMethod.GET)
    public String loginForm() {
        return "/saml/3_login_form";
    }

    // SAML Resopnse 생성
    @RequestMapping(value="/saml/CreateSAMLResponse", method=RequestMethod.POST)
    public String createSAMLResponse(HttpServletRequest request, HttpServletResponse response) {
        // hosts 파일(127.0.0.1  localhost.com) 기설정
        String domainName = "localhost.com";

        String SAMLRequest = request.getParameter("SAMLRequest");
        String acsPage = request.getParameter("returnPage");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String RelayState = request.getParameter("RelayState");

        boolean continueLogin = true;

        if (SAMLRequest == null || SAMLRequest.equals("null")) {
            continueLogin = false;
            request.setAttribute("error","ERROR: Unspecified SAML parameters.");
            request.setAttribute("authstatus","FAIL");
        } else if (acsPage != null) {
            try {
                String requestXmlString = decodeBase64AndDecompress(SAMLRequest);
                String[] samlRequestAttributes = getRequestAttributes(requestXmlString);
                String issueInstant = samlRequestAttributes[0];
                String providerName = samlRequestAttributes[1];
                String acsURL = samlRequestAttributes[2];
                // 유저 인증
                boolean isValiduser = login(username, password);

                if (!isValiduser) {
                    request.setAttribute("error", "Login Failed: Invalid user.");
                    request.setAttribute("authstatus","FAIL");
                } else {
                    request.setAttribute("issueInstant", issueInstant);
                    request.setAttribute("providerName", providerName);
                    request.setAttribute("acsURL", acsURL);
                    request.setAttribute("domainName", domainName);
                    request.setAttribute("username", username);
                    request.setAttribute("RelayState", RelayState);

                    String privateKeyFilePath = "C:\\sso_private.der";
                    String publicKeyFilePath = "C:\\sso_public.der";
                    RSAPrivateKey privateKey = (RSAPrivateKey) Util.getPrivateKey(privateKeyFilePath, "RSA");
                    RSAPublicKey publicKey = (RSAPublicKey) Util.getPublicKey(publicKeyFilePath, "RSA");

                    long now = System.currentTimeMillis();
                    long nowafter = now + 1000*60*60*24;
                    long before = now - 1000*60*60*24;

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                    java.util.Date pTime = new java.util.Date(now);
                    String notBefore = dateFormat.format(pTime);

                    java.util.Date aTime = new java.util.Date(nowafter);
                    String notOnOrAfter = dateFormat.format(aTime);

                    request.setAttribute("notBefore", notBefore);
                    request.setAttribute("notOnOrAfter", notOnOrAfter);

                    // SAML 시간 표현식에 대한 유효성 검사(필요시 구현)
                    // if (!validSamlDateFormat(issueInstant)) {
                    //    continueLogin = false;
                    //    request.setAttribute("error", "ERROR: Invalid NotBefore date specified - " + notBefore);
                    //    request.setAttribute("authstatus","FAIL");
                    // } else if (!validSamlDateFormat(notOnOrAfter)) {
                    //    continueLogin = false;
                    //    request.setAttribute("error", "ERROR: Invalid NotOnOrAfter date specified - " + notOnOrAfter);
                    //    request.setAttribute("authstatus","FAIL");
                    // }

                    if (continueLogin) {
                        // 서명전에 SAMLResponse 생성(SAMLResponseTemplate.xml 템플릿 이용)
                        String SAMLResponse = createSAMLResponse(request, username, notBefore, notOnOrAfter);
                        // SAMLResponse에 대한 전자서명
                        String signedSAMLResponse = Util.signSAMLResponse(SAMLResponse, privateKey, publicKey);

                        request.setAttribute("SAMLResponse", SAMLResponse);
                        request.setAttribute("signedSAMLResponse", signedSAMLResponse);
                        request.setAttribute("authstatus","SUCCESS");
                    } else {
                        request.setAttribute("authstatus","FAIL");
                    }
                }
            } catch (SAMLException e) {
                request.setAttribute("error", e.getMessage());
                request.setAttribute("authstatus","FAIL");
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // redirect to ACS(전자서명 검증 및 relaystate(서비스) URL 리다이렉트 위해)
        response.setContentType("text/html; charset=UTF-8");
        return "/saml/4_redirect_acs";
    }

    // 전자 서명된 SAML Response 유효성 확인
    @RequestMapping(value="/saml/ProcessACSWork", method=RequestMethod.POST)
    public String processACSWork(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String signedSAMLResponse = request.getParameter("signedSAMLResponse");
        String SAMLResponse = request.getParameter("SAMLResponse");
        String RelayState = request.getParameter("RelayState");

        // ACS는 공개키만 알 수 있음
        String publicKeyFilePath = "C:\\sso_public.der";
        RSAPublicKey publicKey = (RSAPublicKey) Util.getPublicKey(publicKeyFilePath,"RSA");

        // 전자 서명된 SAML Response 유효성 확인
        boolean isVerified = Util.verifySAMLResponse(signedSAMLResponse, SAMLResponse, publicKey);

        if (isVerified) {
            String loginid = null;
            Document doc = Util.createJdomDoc(SAMLResponse);
            Iterator itr = doc.getDescendants();
            itr = doc.getDescendants(new ElementFilter());

            while (itr.hasNext()) {
                Content c = (Content) itr.next();
                if (c instanceof Element) {
                    Element e = (Element) c;

                    // System.out.println("Element : " + e.getName());

                    if ("NameID".equals(e.getName())) {
                        loginid = e.getText().trim();
                        break;
                    }
                }
            }
            request.setAttribute("loginid", loginid);
            request.setAttribute("RelayState", RelayState);
            response.setContentType("text/html; charset=UTF-8");

            // relaystate(서비스 URL)로 리다이렉트 하기 위한 페이지로 이동
            return "/saml/5_redirect_relaystate";
        } else {
            System.out.println("SAMLResponse is modified!!");
            return "/saml/samlresponse-modified-error";
        }
    }

    // 서비스 화면 이동(SAMl 프로세스 마지막 페이지)
    @RequestMapping(value="/saml/6_relay_state", method={RequestMethod.GET, RequestMethod.POST})
    public String relayState() {
        return "/saml/6_relay_state";
    }

    // SAML Request 생성
    private String createSAMLRequest(HttpServletRequest request, String acsURL, String providerName) throws IOException {
        String filepath = request.getSession().getServletContext().getRealPath("/") + "/WEB-INF/classes/template/SAMLRequestTemplate.xml";
        String authRequest = Util.readFileContents(filepath);
        authRequest = StringUtils.replace(authRequest, "##PROVIDER_NAME##", providerName);
        authRequest = StringUtils.replace(authRequest, "##ACS_URL##", acsURL);
        authRequest = StringUtils.replace(authRequest, "##AUTHN_ID##", Util.createID());
        authRequest = StringUtils.replace(authRequest, "##ISSUE_INSTANT##", Util.getDateAndTime());
        return authRequest;
    }

    // URL 생성
    private String makeURL(String ssoURL, String authnRequest, String RelayState) throws IOException {
        StringBuffer buf = new StringBuffer();
        buf.append(ssoURL);

        buf.append("?SAMLRequest=");
        buf.append(URLEncoder.encode(Util.compressAndEncodeBase64(authnRequest), "UTF-8"));

        buf.append("&RelayState=");
        buf.append(URLEncoder.encode(RelayState, "UTF-8"));
        return buf.toString();
    }

    // 로그인 성공 여부 판별 메소드
    private boolean login(String username, String password) {
        // IDP 에서 사용자의 인증 정보를 확인하는 로직으로
        // 실제 자체적인 인증 로직이 작성되는 부분이나
        // 이 예제에서는 아이디가 saml 인 경우에만 인증시킴
        if ("saml".equalsIgnoreCase(username)) {
            return true;
        } else {
            return false;
        }
    }

    // SAML Response 생성
    private String createSAMLResponse(HttpServletRequest request, String authenticatedUser, String notBefore, String notOnOrAfter) throws SAMLException, IOException {
        String filepath = request.getSession().getServletContext().getRealPath("/") + "/WEB-INF/classes/template/SAMLResponseTemplate.xml";
        String samlResponse = Util.readFileContents(filepath);
        samlResponse = StringUtils.replace(samlResponse, "##USERNAME_STRING##", authenticatedUser);
        samlResponse = StringUtils.replace(samlResponse, "##RESPONSE_ID##", Util.createID());
        samlResponse = StringUtils.replace(samlResponse, "##ISSUE_INSTANT##", Util.getDateAndTime());
        samlResponse = StringUtils.replace(samlResponse, "##AUTHN_INSTANT##", Util.getDateAndTime());
        samlResponse = StringUtils.replace(samlResponse, "##NOT_BEFORE##", notBefore);
        samlResponse = StringUtils.replace(samlResponse, "##NOT_ON_OR_AFTER##", notOnOrAfter);
        samlResponse = StringUtils.replace(samlResponse, "##ASSERTION_ID##", Util.createID());
        return samlResponse;
    }

}
