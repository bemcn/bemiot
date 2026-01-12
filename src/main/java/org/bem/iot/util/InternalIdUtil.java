package org.bem.iot.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * ID编码生成
 * @author jakybland
 */
public class InternalIdUtil {
	/**
	 * 生成ID
	 * @return 返回ID
	 */
	public static String createUUID() {
		String uuid = UUID.randomUUID().toString();	//获取UUID并转化为String对象
		uuid = uuid.replace("-", "");
		return uuid;
	}

	/**
	 * 生成ID
	 * @return 返回ID
	 */
	public static String createId() {
		int machineId = 1;
        int hashCode = UUID.randomUUID().toString().hashCode();
        if(hashCode < 0) {
			hashCode = -hashCode;
		}
		long timestamp = System.currentTimeMillis();
        String idStr = machineId + String.format("%015d", hashCode) + timestamp;
		return EncryptUtil.encryptMd5By16(idStr);
	}

	/**
	 * 生成统计ID
	 * @param year 年
	 * @param month 月
	 * @param day 日
	 * @param hour 小时
	 * @param minute 分钟
	 * @return 返回统计ID
	 */
	public static int createStatisticsId(int year, int month, int day, int hour, int minute) {
		return optionStatisticsId(year, month, day, hour, minute);
	}

	/**
	 * 生成统计ID
	 * @param year 年
	 * @param month 月
	 * @param day 日
	 * @param hour 小时
	 * @return 返回统计ID
	 */
	public static int createStatisticsId(int year, int month, int day, int hour) {
		return optionStatisticsId(year, month, day, hour, -1);
	}

	/**
	 * 生成统计ID
	 * @param year 年
	 * @param month 月
	 * @param day 日
	 * @return 返回统计ID
	 */
	public static int createStatisticsId(int year, int month, int day) {
		return optionStatisticsId(year, month, day, -1, -1);
	}

	/**
	 * 生成统计ID
	 * @param year 年
	 * @param month 月
	 * @return 返回统计ID
	 */
	public static int createStatisticsId(int year, int month) {
		return optionStatisticsId(year, month, 0, -1, -1);
	}

	/**
	 * 生成统计ID
	 * @param year 年
	 * @param month 月
	 * @param day 日
	 * @param hour 小时
	 * @param minute 分钟
	 * @return 返回统计ID
	 */
	private static int optionStatisticsId(int year, int month, int day, int hour, int minute) {
		String strMonth = "";
		String strDay = "";
		String strHour = "";
		String strMinute = "";
		if(month > 0) {
			if(month < 10) {
				strMonth = "0" + month;
			} else {
				strMonth = month + "";
			}
		}
		if(day > 0) {
			if(day < 10) {
				strDay = "0" + day;
			} else {
				strDay = day + "";
			}
		}
		if(hour > -1) {
			if(hour < 10) {
				strHour = "0" + hour;
			} else {
				strHour = hour + "";
			}
		}
		if(minute > -1) {
			if(minute < 10) {
				strMinute = "0" + minute;
			} else {
				strMinute = minute + "";
			}
		}
		String strStatisticsId = year + strMonth + strDay + strHour + strMinute;
		return Integer.parseInt(strStatisticsId);
	}

	/**
	 * 创建AppId
	 * @return 创建AppId
	 */
	public static String createAppId() {
		String uuid = UUID.randomUUID().toString();
		return EncryptUtil.encryptMd5By16(uuid);
	}

	/**
	 * 创建运用密钥
	 * @return 创建密钥
	 */
	public static String createSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			SecretKey secretKey = keyGenerator.generateKey();
			return ConvertUtil.bytesToHex(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
	}

	/**
	 * 创建密码盐
	 * @return 创建密码盐
	 */
	public static String createPassSalt() {
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ#@*&~%()^$0123456789";
		StringBuilder sb = new StringBuilder(16);

		Random random = new Random();
		for (int i = 0; i < 16; i++) {
			int index = random.nextInt(characters.length());
			sb.append(characters.charAt(index));
		}
		return sb.toString();
	}

	/**
	 * 创建产品授权码
	 * @param timer 时间戳
	 * @param index 序号
	 * @param uuid uuid
	 * @return 创建授权码
	 */
	public static String createProductAuthCode(long timer, int index, String uuid) {
		//获取6位随机数
		int random = (int) (Math.random() * 1000000);
		String code = random + "&" + timer + "_" + index + "_" + random + "&" + uuid;
		return EncryptUtil.encryptMd5By16(code);
	}

	/**
	 * 创建设备Id
	 * @return 创建设备码
	 */
	public static String createDeviceId() {
		String uuid = UUID.randomUUID().toString();
		long timestamp = System.currentTimeMillis();
		String code = timestamp + "*" + uuid + "&" + uuid + "*" + timestamp;
		return EncryptUtil.encryptMd5By16(code);
	}

	/**
	 * 获取设备类型编码
	 * @param deviceType 设备类型
	 * @return 设备类型编码
	 */
	public static String getDeviceTypeCode(String deviceType) {
		return switch (deviceType) {
			case "DVR" -> "111";
			case "NVR" -> "118";
			case "HVR" -> "130";
			case "视频服务器" -> "112";
			case "报警控制器" -> "117";
			case "摄像机" -> "131";
			case "IPC网络摄像机" -> "132";
			case "显示器" -> "133";
			case "报警输入设备" -> "134";
			case "报警输出设备" -> "135";
			case "语音输入设备" -> "136";
			case "语音输出设备" -> "137";
			case "移动传输设备" -> "138";
			case "URL拉流" -> "139";
			default -> "132"; //IPC网络摄像机--IPC
		};

	}

	/**
	 * 生成授权认证Code
	 * @return 返回Code
	 */
	public static String createCode(String appId, String secretKey) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String timp = dateFormat.format(date);
		
		int machineId = 1;
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if(hashCodeV < 0) {
			hashCodeV = -hashCodeV;
		}
        String machineCode = machineId + String.format("%015d", hashCodeV);
        String machCode = machineCode + secretKey + timp + appId + timp;
        return EncryptUtil.encryptMd5By16(machCode);
	}
	
	/**
	 * 生成授权认证Token
	 * @return 返回Token
	 */
	public static String createAccessToken(String appId, String secretKey) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String timp = dateFormat.format(date);
		
		int machineId = 1;
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if(hashCodeV < 0) {
			hashCodeV = -hashCodeV;
		}
        String machineCode = machineId + String.format("%015d", hashCodeV);
        String machCode = timp + appId + timp + machineCode + secretKey + "&#" + timp;
        machCode = EncryptUtil.encryptSha1(machCode);
        return EncryptUtil.encryptSha256(machCode);
	}
	
	/**
	 * 生成授权认证Token
	 * @return 返回Token
	 */
	public static String createRefreshToken(String appId, String secretKey) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String timp = dateFormat.format(date);
		
		int machineId = 1;
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if(hashCodeV < 0) {
			hashCodeV = -hashCodeV;
		}
        String machineCode = machineId + String.format("%015d", hashCodeV);
        String machCode = timp + appId + timp + machineCode + secretKey + "*%" + timp;
        return EncryptUtil.encryptSha256(Objects.requireNonNull(EncryptUtil.encryptMd5By32(machCode)));
	}

}
