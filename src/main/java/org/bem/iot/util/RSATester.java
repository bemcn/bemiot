package org.bem.iot.util;

import java.util.Map;

public class RSATester {
    public static void main(String[] args) throws Exception {
        //生成密钥对
        Map<String, String> keyMap = RsaEncryptUtil.generateKeyPair(true);
        String publicKeyMin = keyMap.get("publicKey");
        String privateKeyMin = keyMap.get("privateKey");
        Map<String, String> reKeyMap = RsaEncryptUtil.formatKey(keyMap, "PKCS#1");
        String publicKey = reKeyMap.get("publicKey");
        String privateKey = reKeyMap.get("privateKey");
        System.out.println("公钥：" + publicKey);
        System.out.println("私钥：" + privateKey);

        //格式化密钥
        Map<String, String> loadKeyMap = RsaEncryptUtil.loadFormatKey(reKeyMap, "PKCS#1");
        publicKey = loadKeyMap.get("publicKey");
        privateKey = loadKeyMap.get("privateKey");
        System.out.println("公钥M：" + publicKeyMin);
        System.out.println("公钥S：" + publicKey);
        System.out.println("私钥M：" + privateKeyMin);
        System.out.println("私钥S：" + privateKey);

        String plainText = "工具类中用到了 BASE64 需要借助第三方类库";
        // 公钥加密
        String cipherText = RsaEncryptUtil.encryptByPublicKey(plainText, publicKey, true, true);
        System.out.println("加密结果：" + privateKey);
        // 私钥签名
        String sign = RsaEncryptUtil.signByPrivateKey(cipherText, privateKey, true,true);
        // 公钥签名认证
        boolean isPassed = RsaEncryptUtil.verifySignPublicKey(cipherText, sign, publicKey, true,true);
        if (isPassed) {
            System.out.println("签名正确！");
        } else {
            System.out.println("签名错误！");
        }
        // 私钥解密
        String txt = RsaEncryptUtil.decryptByPrivateKey(cipherText, privateKey,true,true);
        System.out.println("解密结果：" + txt);



        // 测试前后端使用流程
        // 1、后端生成密钥对，公钥分享给前端,前端获取到服务器给的公钥。
        // 2、前端用服务器端给的公钥 及算法：RSA/CBC/PKCS1Padding 加密"xhy 我爱你 中国 依芸Yiyun ！!!" 生成加密数据。
        // 3、前端然后生成自己的密钥对，用自己的私钥及算法SHA1withRSA生成摘要签名。
        // 4、前端把加密数据、自己的公钥与签名一并发给后端。
        // 3、后端收到加密数据、签名数据、前端的公钥，先用前端公钥、签名数据、算法SHA1withRSA 验签，验签通过后再用后端的私钥、算法:RSA/CBC/PKCS7Padding 解密数据。
        // 注意：密钥与密文全部base64编码

        //RsaEncryptByUtil rsaUtil = new RsaEncryptByUtil(true);

//        String  privateKey_s = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIfKRPRzS0a9Rg1LQizkfIL1ciAMEFs45tl49ERuwIA1kcUrtB1Cjj3bKMLO0Sp7992ECOWVZsE6DZPle3kVYFufIBT4pjR1oJqRs4Z9g5bkwY6p743eGnT1pxri5LNqBdevlKsjqwcfIdOhIqz2BaeM3PT1O52PI9e+U40XEri7AgMBAAECgYAmNYNLqbmP0SiKCxg226AxlXEklWBw2sUSgpdxPhzKtsgqzA5lgVnXC/kfP+TZaIKpgUKjn3OHgZdae2NQAfTXxTcvhNGYSOeJ8VgslQueoJW7ypgQ/IoNy2DeglObAJ3uCgA4F566j6H7IvcllKGmDT/6PUlljxZJpBMfslspgQJBAP19EMRxmV4vYL7o55oR397UEUXn3vO88SPo2gxaPZ/ltzgaHM5R1zALPE1EfPIPqVdGf2hcowr22pC1BG+nlXsCQQCJIq4USfgNmjGwquo5PyksQ9vsYc/OxGBxEqTpVez24eJb7tvoqvbYfpleeEyWgtvzHqnlY24QdONhVVm5zOXBAkAxt7PwM6+3D2fUSe4TA+p60/FHWsEZ4TcSqfsKbTClCfMzp7t6pAamv61mIka3W2cFXShkGbdI0T3xH+/szlu9AkBi3SSgrd7td39hPSaU1MsLBXT0SmO1Te+1NNq8+VxXc+trmZzidPZ2h3ZsG9AjJf4JnM6g9/iuVoZiclS4VVZBAkEAsPkIGRvX4Nj3ljiBjgdJ68JRZC3gK/kXLNeefIeHg6F/4eyg729PlfdD2mvPb8hiszvsT1zvF8gvxGi4lT6B/w==" ;
//        String  publicKey_s = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHykT0c0tGvUYNS0Is5HyC9XIgDBBbOObZePREbsCANZHFK7QdQo492yjCztEqe/fdhAjllWbBOg2T5Xt5FWBbnyAU+KY0daCakbOGfYOW5MGOqe+N3hp09aca4uSzagXXr5SrI6sHHyHToSKs9gWnjNz09TudjyPXvlONFxK4uwIDAQAB";
//        String  publicKey_c ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQPgskvyi9D/IuD0x73M2UOxBH3daAGbxLfUiSraG3cEgZCp7/o1RKM/Uckoplw/DDD665je4wVc0R2zZ8E9LQrrHvvVgddaCvaFZkcIno4EVtHCLldKOFzAIr8ucxCHelV9oGhrcCmeGwYnVTeXOerY9iFi2KDWwF30e2PHRpRQIDAQAB";

//        // publicKey_s = rsaUtil.getKeyMap().get("publicKey");
//        // privateKey_s =rsaUtil.getKeyMap().get("privateKey");
//        System.out.println("<--- 1 --->服务器端生成公钥：" + publicKey_s );
//        // System.out.println("<--- 1 --->服务器端生成私钥："+ privateKey_s);
//        System.out.println("<--- 1 --->服务器端生成私钥,不公开");
//        System.out.println("<--- 2 --->客户端获取到服务器端给定的公钥："+ publicKey_s);
//        System.out.println("<--- 3 --->客户端用此公钥执行加密，加密原始数据：xhy 我爱你 中国 依芸Yiyun ！!!");
//
//        // 客户端用服务器端的公钥生成的密文
//        String cipherText_c ="eCOu/WkaQ8tZHk2u+Y9bh6RKOVMQGsssjnQB5DVlUeDPhjiIybeQSe7JH7fG5FgsucCi6uFwdU7yWzmkJFmMKGnE1pGLReqSaWgecviSTl1P4jjrq84VJvreoeCmcNUCoqxQvmYuMxB/D4rZ+PTuv0B2sQ4Q5fOH6fbqoj3uD5w=";
//        cipherText_c ="MoYui9R3RZ+OYamuTvwAGWy2u8rllBQpjGzTXzje9aUM/KiD5nt8DYf1wna5a7DkBYpZ0CRVNHtOHiHgmrheBkpLuAXdh58eVn2hFLn0xCXqIRk+yEZSrF8fHJ7EBhE00WcyfqOoAM+uum57DFxtvATcEnFiEdkRb32JWkumdIY=";
//        System.out.println("<--- 3 --->客户端生成的加密数据:"+cipherText_c);
//        System.out.println("<--- 4 --->客户端生成自己的公钥："+ publicKey_c);
//        System.out.println("<--- 4 --->客户端生成自己的私钥,不公开");
//
//        // 客户端利用自己的私钥生成摘要后进行的签名
//        String  signedText_c = "uFy+PqjxdxusV5+a9VR0cvk1XY0+Th8jWBT581irWVEDyzq00xGphQ8KIyApgvPw5+KP1DB/M7tMfd0viUT4w8i4VcyhGmRlk0XNkuRhQDgcWeZ5XKIoJ1ORQ0ecxcAAAAlPwMe2wCbPClXFmhJzypJtS7nKFzE/oeZg7nr91zg=";
//        signedText_c ="reCyRzzxeo1i269BdV5TBrYqiYNyWEmk+i6Oxq/0MJz581tThBikh6/Z+hTZ3vY03UngoXwxB7pZBPW7FUxtwdqS8FcKtlVShaz0ZwU22BHbqFBvCvr4C224HQAdNeVoeHb3o8O/DEjNruUzM1NkweLqayI0unieRYxebvCweTE=";
//        System.out.println("<--- 4 --->客户端利用自己的私钥生成摘要而后签名数据:"+signedText_c);
//        System.out.println("<--- 5 --->客户端发送数据至服务器端，1自己的公钥，2自己的加密数据，3自己的签名数据");
//        System.out.println("<--- 6 --->服务器端接收到来自客户端的数据：1客户端的公钥，2客户端的加密数据，3客户端的签名数据");
//        System.out.println("<--- 6 --->1客户端的公钥："+publicKey_c);
//        System.out.println("<--- 6 --->2客户端的加密数据："+cipherText_c);
//        System.out.println("<--- 6 --->3客户端的签名数据："+signedText_c);

//        boolean isPassed = RsaEncryptUtil.verifySignPublicKey(cipherText_c, signedText_c, publicKey_c, true,true);
//        System.out.println("<--- 7 --->服务器端用自己的公钥进行签名验证，结果："+isPassed);
//        if (isPassed)
//        {
//            System.out.println("<--- 8 --->服务器端验签成功,开始解密数据...");
//            String  plainText = "";
//            plainText = RsaEncryptUtil.decryptByPrivateKey(cipherText_c,privateKey_s,true,true);
//
//            System.out.println("<--- 9 --->服务器端用自己的私钥解密数据成功："+ plainText);
//        }

        /* 测试代码
        // 服务器端用服务器端的公钥生成的密文
        String cipherText_s ="";
        String plainText = "xhy 我爱你 中国 依芸Yiyun ！!!";
        cipherText_s = rsaUtil.encryptByPublicKey(plainText, publicKey_s,true, true,false);
        System.out.println("服务器端执行加密，公钥加密:"+cipherText_s);

        // 服务器端利用客户端的私钥进行签名
        String  signedText_s = rsaUtil.signByPrivateKey(cipherText_c, privateKey_c,true, true,false);
        System.out.println("服务器端执行签名，私钥签名:"+signedText_s);
        */

          /* String  privateKey_c = "MIICXAIBAAKBgQDQPgskvyi9D/IuD0x73M2UOxBH3daAGbxLfUiSraG3cEgZCp7/o1RKM/Uckoplw/DDD665je4wVc0R2zZ8E9LQrrHvvVgddaCvaFZkcIno4EVtHCLldKOFzAIr8ucxCHelV9oGhrcCmeGwYnVTeXOerY9iFi2KDWwF30e2PHRpRQIDAQABAoGARuvaf7la9ojnwigTtFuO6Fz1PoSe+SHKrysL/GiGGyNyapTjccz+eAcaA5Ek8WO6K7S7nRZpeKzAGsS92aQmt66BpOqI+JJ2uM+K1HzH5K5rQ4rnaC/Hbd+4zsltVzuLbsICDGSlkpTSKK5YdIkA5YPMXoQek4zoYpUnKT2AxEECQQDoDrjIJ4MllIpc" +
                "gAWjahga1YrcTIcQPBwG9rfX7zk2nKFZF5rOB6iDHjE9mo9EOD/s7j3Z5eefwVkp" +
                "hRnbXJp3AkEA5bpMSf8zyBKfMZll3vdtDTDqnsVzOu89RxQYgceyWZ/OcFgvc9hg" +
                "NYoV/EkGQXcHWL1gPQwWpMRfS8L/DjbNIwJBAL3NBL/Y6YB8TOq5X2M4bHzOOiRT" +
                "h4j00Su08ctxA8eyNpnrH5fyVZbgw/+SAioXI9oDRp2JWHinKOk3z11HEaMCQDI/" +
                "qLY60xm9MQMJWaYGmtzayUcHS2glslKcy6t/gbxm3yHluCNvvcOYO6zeUDb7kSjQ" +
                "638O6NkLdwi8U0vJot8CQHEfumEFZ0LYbz914TZOWe2q0UKOUZaHgQIwoJ3n2yxJ" +
                "p7Ps3k9t2Of8Tm+HqZYCkSz8henOM8aFCS2GPD8Pkf4=" ;
        */
    }
}
