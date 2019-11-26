package kr.co.cleanbrain.test;

import java.io.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created with IntelliJ IDEA.
 * User: 노상현
 * Date: 2019-11-21
 */
public class TestClass {

    public static void main(String[] arsg) throws IOException, DataFormatException, NoSuchAlgorithmException, InvalidKeySpecException {
//        encodeDecodeDeflateInflateEx();
//        encodeDecodeDeflateInflateEx2();
//        simpleBase64EncodeDecodeEx();
//        compareBytesEx();
//        deflateinflateEx();
//        base64EncodeDecode_CompreeDecompress_Ex();
//        stringTest();
//        keyGeneratorEx();
        keyGeneratorEx2();
    }

    public static void deflateinflateEx() throws IOException, DataFormatException {
        String str = "test string";
        byte[] strBytes = str.getBytes("UTF-8");

        Deflater deflater = new Deflater();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        deflater.setInput(strBytes);
        deflater.finish();
        byte[] buffer = new byte[10240];
        int count = 0;
        while (!deflater.finished()) {
            count = deflater.deflate(buffer);
            stream.write(buffer, 0, count);
        }

        String deflatedStr = new String(stream.toByteArray(), "UTF-8");
        System.out.println(deflatedStr);
        stream.close();
        deflater.end();

        Inflater inflater = new Inflater(true);
        stream = new ByteArrayOutputStream();

        inflater.setInput(deflatedStr.getBytes("UTF-8"));
        buffer = new byte[10240];

        while (!inflater.finished()) {
            count = 0;
            count = inflater.inflate(buffer);
            stream.write(buffer, 0, count);
        }

        String retStr = new String(stream.toByteArray(), "UTF-8");
        inflater.end();
        stream.close();

        System.out.println("inflated str : " + retStr);
    }

    public static void base64EncodeDecode_CompreeDecompress_Ex() throws IOException, DataFormatException {
        String str = "test String";
        byte[] strBytes = str.getBytes("UTF-8");

        byte[] compressedBytes = compress(strBytes);
        compressedBytes = base64Encode(compressedBytes);

        System.out.println("encoded compressed str : " + new String(compressedBytes));

        compressedBytes = base64Decode(compressedBytes);

        System.out.println("decoded compressed str : " + new String(compressedBytes));

        byte[] decompressedBytes = decompress(compressedBytes);

        String compressedStr = new String(compressedBytes, "UTF-8");
        String decompressedStr = new String(decompressedBytes, "UTF-8");

        System.out.println("original str : " + str);
        System.out.println("compressed str : " + compressedStr);
        System.out.println("decompressed str : " + decompressedStr);
    }

    public static void compareBytesEx() throws UnsupportedEncodingException {
        String str = "test string";

        byte[] encodedBytes = Base64.getEncoder().encode(str.getBytes("UTF-8"));
        byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);

        System.out.println("encoded bytes : " + encodedBytes);
        System.out.println("decoded bytes : " + decodedBytes);

        String encodedStr = new String(encodedBytes);
        String decodedStr = new String(decodedBytes);

        System.out.println("original str : " + str);
        System.out.println("encoded str : " + encodedStr);
        System.out.println("decoded str : " + decodedStr);
    }

    public static void encodeDecodeDeflateInflateEx() throws IOException, DataFormatException {
        String str = "test string" + "test string" + "test string" + "test string" + "test string" + "test string" + "test string" + "test string" + "test string" + "test string";

        String deflatedStr = deflate(str);
        System.out.println("----- deflate -----");
        System.out.println("str : " + deflatedStr);
//        System.out.println("base64 decode str : " + Base64.getDecoder().decode(str));
        System.out.println();
        System.out.println();

        String inflatedStr = inflate(deflatedStr);
        System.out.println("----- inflate -----");
        System.out.println("str : " + inflatedStr);
        System.out.println();
        System.out.println();
    }

    public static void encodeDecodeDeflateInflateEx2() throws IOException, DataFormatException {
        String str = "test string";

        Base64.Encoder encoder = Base64.getEncoder();
        Base64.Decoder decoder = Base64.getDecoder();

        byte[] strBytes = str.getBytes("UTF-8");

        Deflater deflater = new Deflater();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        deflater.setInput(strBytes);
        deflater.finish();
        byte[] buffer = new byte[1024];
        int count = 0;
        while (!deflater.finished()) {
            count = deflater.deflate(buffer);
            stream.write(buffer, 0, count);
        }
    stream.close();
        byte[] encodedBytes = encoder.encode(stream.toByteArray());
        String encodedStr = new String(encodedBytes);
        System.out.println("encodedStr : " + encodedStr);

        byte[] strBytes2 = encodedStr.getBytes("UTF-8");
        byte[] decodedBytes = decoder.decode(strBytes2);

        String decodedStr = new String(decodedBytes);
        System.out.println("decodedStr : " + decodedStr);

        System.out.println("\nencodedBytes : " + encodedBytes);
        System.out.println("decodedBytes : " + decodedBytes);

        Inflater inflater = new Inflater(true);
        stream = new ByteArrayOutputStream();

        inflater.setInput(decodedStr.getBytes("UTF-8"));
        buffer = new byte[1024];

        while (!inflater.finished()) {
            count = 0;
            count = inflater.inflate(buffer);
            stream.write(buffer, 0, count);
        }
        inflater.end();
        stream.close();
        String retStr = new String(stream.toByteArray());
        System.out.println("inflated str : " + retStr);
    }

    public static String deflate(String input) throws IOException {
        Deflater deflater = new Deflater();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        deflater.setInput(input.getBytes("UTF-8"));
        deflater.finish();
        byte[] buffer = new byte[1024];

        int count = 0;

        while (!deflater.finished()) {
            count = deflater.deflate(buffer);
            stream.write(buffer, 0, count);
        }

        Base64.Encoder encoder = Base64.getEncoder();
        String retStr = new String(encoder.encode(stream.toByteArray()));

        stream.close();
        deflater.end();

        return retStr;
    }

    public static String inflate(String input) throws IOException, DataFormatException {
        Base64.Decoder decoder = Base64.getDecoder();

        System.out.println("before base64Decode input : " + input);

        byte[] inputBytes = input.getBytes("UTF-8");
        byte[] decodedInputBytes = decoder.decode(inputBytes);
        String decodedInput = new String(decodedInputBytes);

//        String decodedInput = new String(decoder.decode(input.getBytes("UTF-8")));

        /*Inflater inflater = new Inflater(true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        inflater.setInput(input.getBytes("UTF-8"));
        byte[] buffer = new byte[1024];

        while (!inflater.finished()) {
            int count = 0;
            count = inflater.inflate(buffer);
            stream.write(buffer, 0, count);
        }

        inflater.end();
        String retStr = new String(stream.toByteArray());
        stream.close();

        return retStr;*/

        return "";
    }

    public static void simpleBase64EncodeDecodeEx() throws UnsupportedEncodingException {
//        String text = "ktko";
        String text = "eJwrSS0uUSguKcrMSy+hPRMAriAspw==";
        byte[] targetBytes = text.getBytes(("UTF-8"));

        // Base64 인코딩 ///////////////////////////////////////////////////
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(targetBytes);

        // Base64 디코딩 ///////////////////////////////////////////////////
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedBytes);

        System.out.println("인코딩 전 : " + text);
//        System.out.println("인코딩 text : " + new String(encodedBytes));
//        System.out.println("디코딩 text : " + new String(decodedBytes));

        String encodedStr = new String(encodedBytes);
        String decodedStr = new String(decodedBytes);

        System.out.println("인코딩 text : " + encodedStr);
        System.out.println("디코딩 text : " + decodedStr);
        System.out.println();

        System.out.println(new String(decoder.decode(encodedStr.getBytes("UTF-8"))));
    }

    public static byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        System.out.println("Original: " + data.length / 1024 + " Kb");
        System.out.println("Compressed: " + output.length / 1024 + " Kb");
        return output;
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        System.out.println("Original: " + data.length);
        System.out.println("Compressed: " + output.length);
        return output;
    }

    public static byte[] base64Encode(byte[] data) {
        return Base64.getEncoder().encode(data);
    }

    public static byte[] base64Decode(byte[] data) {
        return Base64.getDecoder().decode(data);
    }

    public static void stringTest() {
        System.out.println(String.format("%501s", "TEST MESSAGE"));
    }

    public static void keyGeneratorEx() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);

        String plain = String.format("%s501", "TEST MESSAGE");

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey pub = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey pvt = (RSAPrivateKey) keyPair.getPrivate();

        System.out.println(pub);
        System.out.println(pvt);
    }

    public static void keyGeneratorEx2() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String PRIVATE_KEY="C:\\sso_private.der";
        String PUBLIC_KEY="C:\\sso_public.der";

        //get the private key
        File file = new File(PRIVATE_KEY);
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);

        byte[] keyBytes = new byte[(int) file.length()];
        dis.readFully(keyBytes);
        dis.close();

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(spec);
        System.out.println("Exponent :" + privKey.getPrivateExponent());
        System.out.println("Modulus" + privKey.getModulus());

        //get the public key
        File file1 = new File(PUBLIC_KEY);
        FileInputStream fis1 = new FileInputStream(file1);
        DataInputStream dis1 = new DataInputStream(fis1);
        byte[] keyBytes1 = new byte[(int) file1.length()];
        dis1.readFully(keyBytes1);
        dis1.close();

        X509EncodedKeySpec spec1 = new X509EncodedKeySpec(keyBytes1);
        KeyFactory kf1 = KeyFactory.getInstance("RSA");
        RSAPublicKey pubKey = (RSAPublicKey) kf1.generatePublic(spec1);

        System.out.println("Exponent :" + pubKey.getPublicExponent());
        System.out.println("Modulus" + pubKey.getModulus());

    }
}

