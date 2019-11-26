package kr.co.cleanbrain.kr.co.cleanbrain.util;

import kr.co.cleanbrain.exception.SAMLException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created with IntelliJ IDEA.
 * User: 노상현
 * Date: 2019-11-21
 * SAML UTIL
 */
public class Util {

    public static String readFileContents(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String retStr = "";
        String tempStr = "";

        while( (tempStr = br.readLine()) != null ) {
            retStr = retStr.concat(tempStr);
        }

        br.close();

        return retStr;
    }

    // 일련번호(예제이므로 1로 고정)
    public static String createID() {
        return "1";
    }

    // SAML 시간 형식
    public static String getDateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        return sdf.format(new Date());
    }

    // 압축 및 Base64 인코딩
    public static String compressAndEncodeBase64(String data) throws IOException {
        byte[] bytes = data.getBytes("UTF-8");
        Deflater deflater = new Deflater();
        deflater.setInput(bytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);
        deflater.finish();
        byte[] buffer = new byte[1024];

        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }

        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return new String(Base64.getEncoder().encode(output), "UTF-8");
    }

    // Base64 디코딩 및 압축해제
    public static String decodeBase64AndDecompress(String data) throws DataFormatException, IOException {
        byte[] bytes = data.getBytes("UTF-8");
        byte[] decodedBytes = Base64.getDecoder().decode(bytes);

        Inflater inflater = new Inflater();
        inflater.setInput(decodedBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(decodedBytes.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return new String(output);
    }

    public static String[] getRequestAttributes(String xmlString) throws SAMLException, IOException, JDOMException {
        Document doc = Util.createJdomDoc(xmlString);

        if (doc != null) {
            String[] samlRequestAttributes = new String[3];
            samlRequestAttributes[0] = doc.getRootElement().getAttributeValue("IssueInstant");
            samlRequestAttributes[1] = doc.getRootElement().getAttributeValue("ProviderName");
            samlRequestAttributes[2] = doc.getRootElement().getAttributeValue("AssertionConsumerServiceURL");
            return samlRequestAttributes;
        } else {
            throw new SAMLException("Error parsing AuthnRequest XML: Null document");
        }
    }

    // 비밀키 획득
    public static PrivateKey getPrivateKey(String filename, String encryptAlgorithm) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);

        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(encryptAlgorithm);
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(spec);

        return privKey;
    }

    // 공개키 획득
    public static PublicKey getPublicKey(String filename, String encryptAlgorithm) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);

        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();

        X509EncodedKeySpec spec1 = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf1 = KeyFactory.getInstance(encryptAlgorithm);
        RSAPublicKey pubKey = (RSAPublicKey) kf1.generatePublic(spec1);

        return pubKey;
    }

    // SAML Response 전자 서명
    public static String signSAMLResponse(String SAMLResponse, RSAPrivateKey privateKey, RSAPublicKey publicKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, SignatureException, DataFormatException, IOException {
        Signature signer = Signature.getInstance("NONEwithRSA");
        signer.initSign(privateKey);
        signer.update(MessageDigest.getInstance("SHA-256").digest(SAMLResponse.getBytes("UTF-8")));
        return Base64.getEncoder().encodeToString(signer.sign());
    }

    // SAML Response 전자 서명 유효성 확인
    public static boolean verifySAMLResponse(String signedSAMLResponse, String SAMLResponse, RSAPublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        Signature verifier = Signature.getInstance("NONEwithRSA");
        verifier.initVerify(publicKey);
        verifier.update(MessageDigest.getInstance("SHA-256").digest(SAMLResponse.getBytes("UTF-8")));
        return verifier.verify(Base64.getDecoder().decode(signedSAMLResponse.getBytes("UTF-8")));
    }

    // Jdom 생성
    public static Document createJdomDoc(String SAMLResponse) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new StringReader(SAMLResponse));
    }
}
