package org.bem.iot.util;

/**
 * 数据脱敏工具类
 */
public class DataMaskingUtil {
    // 手机号脱敏
    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            return phoneNumber; // 无法脱敏时，直接返回原号码
        }
        return phoneNumber.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    // 身份证号脱敏
    public static String maskIdCardNumber(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return idCard; // 不符合18位身份证长度
        }
        return idCard.replaceAll("(\\d{6})\\d{8}(\\d{4})", "$1********$2");
    }

    // 邮箱地址脱敏
    public static String maskEmailAddress(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        return email.replaceAll("(\\w{2})\\w*(\\w{1}@.*)", "$1****$2");
    }

    // 银行卡号脱敏
    public static String maskBankCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 16) {
            return cardNumber;
        }
        return cardNumber.replaceAll("(\\d{4})\\d{8,12}(\\d{4})", "$1****$2");
    }

    // 示例：脱敏信用卡号只保留最后4位
    public static String maskCreditCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 16) {
            return cardNumber;
        }
        return cardNumber.replaceAll("\\d{12}(\\d{4})", "**** **** **** $1");
    }

    public static void main(String[] args) {
        String phoneNumber = "13812345678";
        String idCard = "420123199001010000";
        String email = "cx@163.com";
        String cardNumber = "622908117885442633";
        String creditCardNumber = "655101446779008221";

        System.out.println("手机号码:" + phoneNumber + "      脱敏：" + maskPhoneNumber(phoneNumber));
        System.out.println("身份证码:" + idCard + "      脱敏：" + maskIdCardNumber(idCard));
        System.out.println("电子邮箱:" + email + "      脱敏：" + maskEmailAddress(email));
        System.out.println("银行卡号:" + cardNumber + "      脱敏：" + maskBankCardNumber(cardNumber));
        System.out.println("信用卡号:" + creditCardNumber + "      脱敏：" + maskCreditCardNumber(creditCardNumber));
    }
}
