package org.bem.iot.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * 常用加密解密
 * @author jaky
 *
 */
public class EncryptUtil {
	/**
	 * SHA1 加密
	 * @param plaintext 明文
	 * @return 密文
	 */
	public static String encryptSha1(String plaintext) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(plaintext.getBytes());
			byte[] messageDigest = digest.digest();
			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			// 字节数组转换为 十六进制 数
			for (byte b : messageDigest) {
				String shaHex = Integer.toHexString(b & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString().toLowerCase();

		} catch (NoSuchAlgorithmException ignored) {
		}
		return "";
	}

	/**
	 * SHA256 加密
	 * @param plaintext 明文
	 * @return 密文
	 */
	public static String encryptSha256(String plaintext) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(plaintext.getBytes());
			byte[] messageDigest = digest.digest();
			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			// 字节数组转换为 十六进制 数
			for (byte b : messageDigest) {
				String shaHex = Integer.toHexString(b & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString().toLowerCase();

		} catch (NoSuchAlgorithmException ignored) {
		}
		return "";
	}
	
	/**
	 * MD5 加密（16位）
	 * @param plaintext 明文
	 * @return 密文
	 */
	public static String encryptMd5By16(String plaintext) {
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.update(plaintext.getBytes());  
            byte[] b = md.digest();
  
            int i;
            StringBuilder buf = new StringBuilder();
			for (byte value : b) {
				i = value;
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
            return buf.substring(8, 24).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            return null;  
        }  
  
    } 
	
	/**
	 * MD5 加密（32位）
	 * @param plaintext 明文
	 * @return 密文
	 */
	public static String encryptMd5By32(String plaintext) {
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.update(plaintext.getBytes());  
            byte[] b = md.digest();
  
            int i;  
  
            StringBuilder buf = new StringBuilder();
			for (byte value : b) {
				i = value;
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
            return buf.toString().toLowerCase();   
        } catch (NoSuchAlgorithmException e) {
            return null;  
        }  
    }
	
	/**
	 * HMAC加密  
	 * @param source 待加密字符
	 * @param secret 密钥（app_secret）
	 * @return 已加密字符
	 */
    public static String encryptHmac(String source, String secret) {
    	SecretKey secretKey;
		try {
			secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacMD5");
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
	        mac.init(secretKey);
			byte[] bytes = mac.doFinal(source.getBytes(StandardCharsets.UTF_8));
			return ConvertUtil.bytesToHex(bytes).toLowerCase();
		} catch (Exception e) {
			return null;
		}
    }

	/**
	 * 创建AES密钥
	 * @return 32位密钥
	 */
	public static String createAesKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256, new SecureRandom());
			SecretKey secretKey = keyGenerator.generateKey();
			return EncryptUtil.encryptMd5By32(ConvertUtil.bytesToHex(secretKey.getEncoded()));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建AES IV
	 * @return 16位IV
	 */
	public static String createAesIv() {
		byte[] iv = new byte[16];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		return EncryptUtil.encryptMd5By16(ConvertUtil.bytesToHex(iv));
	}

	/**
	 * AES CBC 加密
	 * @param plaintext 明文
	 * @param key 密钥
	 * @param iv IV
	 * @return 加密后的字符串
	 */
	public static String encryptAes(String plaintext, String key, String iv) {
		try {
			IvParameterSpec initVector = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, initVector);

			byte[] encrypted = cipher.doFinal(plaintext.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			throw new RuntimeException(e);
		}
    }

	/**
	 * AES CBC 解密
	 * @param secret 密文
	 * @param key 密钥
	 * @param iv IV
	 * @return 解密后的字符串
	 */
	public static String decryptAes(String secret, String key, String iv) {
		try {
			IvParameterSpec initVector = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, initVector);

			byte[] original = cipher.doFinal(Base64.getDecoder().decode(secret));

			return new String(original);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			throw new RuntimeException(e);
		}
    }

	public static void main(String[] args) {
		String plaintext = "我有一所庭院，藤蔓爬满了窗台，露水打湿了衣裳";
		String key = "c3164f37a2d0e44f1fbc5df23db16ca2";
		String iv = "cc7fbaf1833aab52";

		String baseEn = Base64.getEncoder().encodeToString(plaintext.getBytes());
		String baseDe = new String(Base64.getDecoder().decode(baseEn));
		String md5En = encryptMd5By32(plaintext);
		String HmacEn = encryptHmac(plaintext, "adgu9383619");
		String aesSecret = encryptAes(plaintext, key, iv);
		String aesPlaintext = decryptAes(aesSecret, key, iv);
		System.out.println("原文：" + plaintext);
		System.out.println("Base64加密：" + baseEn);
		System.out.println("Base64解密：" + baseDe);
		System.out.println("Md5加密：" + md5En);
		System.out.println("Hmac加密：" + HmacEn);
		System.out.println("AES加密：" + aesSecret);
		System.out.println("AES解密：" + aesPlaintext);
	}
}
