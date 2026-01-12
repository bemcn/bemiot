package org.bem.iot.util;

import lombok.Getter;
import lombok.Setter;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
public class RsaEncryptByUtil {
    /**
     * 密钥长度与原文长度对应,越长速度越慢
     * 1024bit 密钥 能加密明文最大的长度是 1024/8 -11 = 117 byte
     * 2048bit 密钥 能加密明文最大的长度是 2048/8 -11 = 245 byte
     */
    private final static int KEY_SIZE = 1024;
    /*
     * RSA算法 RSA-1024位 RSA2-2048位
     */
    private final static String ALGORITHM = "RSA";
    //java默认"RSA"="RSA/ECB/PKCS1Padding"
    private final static String PADDING_MODE = "RSA/ECB/PKCS1Padding";

    /*
     * 签名算法
     * RSA: 常用 SHA1WithRSA，有 MD2withRSA、MD5withRSA、SHA1withRSA；
     * RSA2:  常用：SHA256WithRSA，有 SHA224withRSA、SHA256withRSA、SHA384withRSA、SHA512withRSA 、RIPEMD128withRSA、RIPEMD160withRSA；
     */
    public static final String SIGN_ALGORITHM = "SHA1withRSA";

    /*
     * 字符编码
     */
    private final static String ENCODING = "UTF-8";

    /*
     * RSA最大加密明文大小
     * RSA：117
     * RSA2：245
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /*
     * RSA最大解密密文大小
     * RSA：128
     * RSA2：256
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 用于封装随机产生的公钥与私钥
     */
    private Map<String, String> keyMap;


    public RsaEncryptByUtil(boolean isKeyBase64) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException {
        setKeyMap(generateKeyPair(isKeyBase64));
    }

    /**
     * 随机生成密钥对
     * @param isKeyBase64 密钥base64加密为true,hex为false
     */
    private Map<String, String> generateKeyPair(boolean isKeyBase64) throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = isKeyBase64 ? Base64.getEncoder().encodeToString(publicKey.getEncoded()) : ConvertUtil.bytesToHex(publicKey.getEncoded());

        // 得到私钥字符串
        String privateKeyString = isKeyBase64 ? Base64.getEncoder().encodeToString(privateKey.getEncoded()) : ConvertUtil.bytesToHex(privateKey.getEncoded());
        // 将公钥和私钥保存到Map
        Map<String, String> map = new HashMap<>(32);
        //表示公钥
        map.put("publicKey", publicKeyString);
        //表示私钥
        map.put("privateKey", privateKeyString);

        return map;
    }

    /**
     * 流程1：RSA公钥加密，再BASE64加密，再base64解密，再RSA私钥解密
     * 流程2：RSA私钥加密，再BASE64加密，再base64解密，再RSA公钥解密
     *
     * @param text        加密字符串/解密字符串
     * @param secretKey   加密秘钥
     * @param isPublicKey 公钥/私钥
     * @param isEncrypt   加密/解密
     * @param isKeyBase64 密钥base64加密为true,hex为false
     * @param isTextBase64 密文base64加密为true,hex为false
     * @return 密文/明文
     * @throws Exception 加密过程中的异常信息
     */
    private String enDecrypt(String text, String secretKey, boolean isPublicKey, boolean isEncrypt, boolean isKeyBase64, boolean isTextBase64) throws Exception {
        //base64编码的秘钥
        byte[] decodedKey = isKeyBase64 ? Base64.getDecoder().decode(secretKey.getBytes(ENCODING)) : ConvertUtil.hexToByteArray(secretKey);
        byte[] decodeText;
        int encodeMode = 0;
        int max_block = 0;
        int textLength = 0;
        Cipher cipher = Cipher.getInstance(PADDING_MODE);


        if (isEncrypt) {
            encodeMode = Cipher.ENCRYPT_MODE;
            max_block = MAX_ENCRYPT_BLOCK;
            //待加密字符串
            decodeText = text.getBytes(ENCODING);
            textLength = text.getBytes(ENCODING).length;
        } else {
            encodeMode = Cipher.DECRYPT_MODE;
            max_block = MAX_DECRYPT_BLOCK;
            decodeText = text.getBytes(ENCODING);
            // 注意:byte[]转String,不能 byte[].toString(); 可用new String(byte[])
            decodeText = isTextBase64 ? Base64.getDecoder().decode(decodeText) : ConvertUtil.hexToByteArray(new String(decodeText, ENCODING));
            textLength = decodeText.length;
        }

        if (isPublicKey) {
            cipher.init(encodeMode, KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(decodedKey)));
        } else {
            cipher.init(encodeMode, KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decodedKey)));
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (textLength - offSet > 0) {
            if (textLength - offSet > max_block) {
                cache = cipher.doFinal(decodeText, offSet, max_block);
            } else {
                cache = cipher.doFinal(decodeText, offSet, textLength - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * max_block;
        }
        byte[] byteText = out.toByteArray();
        out.close();

        String resultText = out.toString();
        if (isEncrypt) {
            resultText = isTextBase64 ? Base64.getEncoder().encodeToString(byteText) : ConvertUtil.bytesToHex(byteText);
        }

        return resultText;
    }

    public String encryptByPublicKey(String plainText, String publicKey, boolean isKeyBase64, boolean isTextBase64) throws Exception {
        return enDecrypt(plainText, publicKey, true, true, isKeyBase64, isTextBase64);
    }

    public String decryptByPrivateKey(String cipherText, String privateKey, boolean isKeyBase64, boolean isTextBase64) throws Exception {
        return enDecrypt(cipherText, privateKey, false, false, isKeyBase64, isTextBase64);
    }

    /**
     * RSA签名
     *
     * @param signText   待签名数据
     * @param privateKey 私钥
     * @param isKeyBase64 密钥base64加密为true,hex为false
     * @param isTextBase64 密文base64加密为true,hex为false
     * @return 签名值
     */
    public String signByPrivateKey(String signText, String privateKey, boolean isKeyBase64, boolean isTextBase64) {
        String signValue = "";
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            byte[] decodeKey = isKeyBase64 ? Base64.getDecoder().decode(privateKey) : ConvertUtil.hexToByteArray(privateKey);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodeKey);
            //DER input, Integer tag error ,原因密钥错误
            PrivateKey private_key = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initSign(private_key);

            String  sign_text = isTextBase64 ? ConvertUtil.bytesToHexString(Base64.getDecoder().decode(signText.getBytes(ENCODING))):signText;
            //  System.out.println("服务器端：待签名数据："+sign_text);

            assert sign_text != null;
            byte[] digestText = genDigest(sign_text,SIGN_ALGORITHM );

            // 生成消息摘要 因客户端签名时参数为16进制， 可以通过加密算法验证是否正确
            signature.update(ConvertUtil.bytesToHexString(digestText).getBytes(ENCODING));
            // System.out.println("服务器端：摘要数据："+new String(digestText));

            byte[] signedByte = signature.sign();
            // System.out.println("服务器端：签名数据："+Convert.bytesToHexString(signedByte));

            signValue = isTextBase64 ? Base64.getEncoder().encodeToString(signedByte) : ConvertUtil.bytesToHex(signedByte);

        } catch (Exception ignored) {
        }
        return signValue;
    }


    /*
     * 生成消息摘要
     */
    public byte[] genDigest(String plainText, String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest messageDigest = MessageDigest.getInstance(algorithm.split("w")[0]);
        //  System.out.println("服务器端，哈希算法："+algorithm.split("w")[0]);
        return messageDigest.digest(plainText.getBytes(ENCODING));
        // System.out.println("服务器端，摘要："+bytesToHexString(digestText));
    }

    /**
     * RSA验签名检查
     *
     * @param signText   待签名数据
     * @param signedText 已签名数据
     * @param publicKey  公钥
     * @param isKeyBase64 密钥base64加密为true,hex为false
     * @param isTextBase64 密文base64加密为true,hex为false
     * @return 布尔值
     */
    public boolean verifySignPublicKey(String signText, String signedText, String publicKey, boolean isKeyBase64, boolean isTextBase64) {
        boolean isVerified = false;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            byte[] decodedKey = isKeyBase64 ? Base64.getDecoder().decode(publicKey) : ConvertUtil.hexToByteArray(publicKey);
            PublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);

            signature.initVerify(pubKey);

            signText = isTextBase64 ? ConvertUtil.bytesToHexString( Base64.getDecoder().decode(signText.getBytes(ENCODING))) :signText;
            // 生成消息摘要
            assert signText != null;
            // System.out.println("验证，待签名数据："+signText);

            byte[] digestText = genDigest(signText,SIGN_ALGORITHM );
            // 生成消息摘要
            signature.update(ConvertUtil.bytesToHexString(digestText).getBytes(ENCODING));

            byte[] bytesText = isTextBase64 ? Base64.getDecoder().decode(signedText) : ConvertUtil.hexToByteArray(signedText);

            //DER input, Integer tag error ,或 DerInputStream.getLength(): lengthTag=111, too big.原因密钥错误
            isVerified = signature.verify(bytesText);

        } catch (Exception ignored) {
        }
        return isVerified;
    }


    // PKCS#1 与PKCS#8 格式转换
    // 带头和尾的密钥，去掉头和尾后，再按指定格式增加头和尾
    public Map<String,String> convertKeyFormat(Map<String,String> mapSource,String keyMode) {
        Map<String,String> map = removeHeaderAndBottom(mapSource,keyMode);
        map = formatKey (map,keyMode);

        return map;
    }

    public Map<String,String>   removeHeaderAndBottom (Map<String,String> mapSource,String keyMode) {
        Map<String,String> map = new HashMap<>();
        Map<String,String> mapHeaderAndBottom_publicKey = genKeyHeaderAndBottom(keyMode,false);
        Map<String,String> mapHeaderAndBottom_privateKey  = genKeyHeaderAndBottom(keyMode,true);

        String privateKey = mapSource.get("privateKey");
        String publicKey =  mapSource.get("publicKey");

        publicKey =  publicKey.replaceAll(mapHeaderAndBottom_publicKey.get("header"),"");
        publicKey = publicKey.replaceAll(mapHeaderAndBottom_publicKey.get("bottom"),"");

        privateKey=privateKey.replaceAll(mapHeaderAndBottom_privateKey.get("header"),"");
        privateKey= privateKey.replaceAll(mapHeaderAndBottom_privateKey.get("bottom"),"");

        map.put("publicKey",publicKey);
        map.put("privateKey",privateKey);

        return map;
    }

    // 不带头和尾的密钥，按指定格式增加头和尾
    public Map<String,String> formatKey( Map<String,String> mapSource, String keyMode) {
        Map<String,String> map = new HashMap<>();

        map.put("publicKey", formatKey(mapSource.get("publicKey"),keyMode,false));
        map.put("privateKey", formatKey(mapSource.get("privateKey"),keyMode,true));

        return map;
    }

    // 格式化key，增加头和尾
    private String formatKey( String  keyBody, String keyMode,boolean isPrivateKey) {
        Map<String,String> map = genKeyHeaderAndBottom(keyMode,isPrivateKey);
        String keyHeader = map.get("header");
        String keyBottom =map.get("bottom");
        String  strKey = keyHeader + "\n";

        int nPrivateKeyLen = keyBody.length();
        char[] status = keyBody.toCharArray();
        for(int i = 64; i < nPrivateKeyLen; i+=64)
        {
            if(status[i] != '\n')
            {
                status[i]= '\n';
            }
            i++;
        }
        strKey += String.valueOf(status);
        strKey += "\n";
        strKey += keyBottom;
        strKey += "\n";

        return strKey;
    }

    // 按指定格式生成头和尾
    public Map<String,String> genKeyHeaderAndBottom(String keyMode,boolean isPrivateKey) {
        Map<String,String> map = new HashMap<>();
        if (Objects.equals(keyMode, "PKCS#1")) {
            if (isPrivateKey) {
                map.put("header", "-----BEGIN RSA PRIVATE KEY-----");
            } else {
                map.put("header", "-----BEGIN RSA PUBLIC KEY-----");
            }
            if (isPrivateKey) {
                map.put("bottom", "-----END RSA PRIVATE KEY-----");
            } else {
                map.put("bottom", "-----END RSA PUBLIC KEY-----");
            }
        }
        else if (Objects.equals(keyMode, "PKCS#8")) {
            if (isPrivateKey) {
                map.put("header", "-----BEGIN PRIVATE KEY-----");
            } else {
                map.put("header", "-----BEGIN PUBLIC KEY-----");
            }
            if (isPrivateKey) {
                map.put("bottom", "-----END PRIVATE KEY-----");
            } else {
                map.put("bottom", "-----END PUBLIC KEY-----");
            }
        }
        return map;
    }
}
