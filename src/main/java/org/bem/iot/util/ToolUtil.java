package org.bem.iot.util;

public class ToolUtil {
    /**
     * 获取有效的值
     * @param strValue 字符串值
     * @return 输出字符串值转换后的float有效值
     */
    public static float getValidValue(String strValue) {
        try {
            float floatValue = Float.parseFloat(strValue);

            // 获取小数部分
            String str = String.valueOf(floatValue);
            int dotIndex = str.indexOf('.');

            if(dotIndex == -1) {
                return floatValue;
            } else {
                String decimalPart = str.substring(dotIndex + 1);
                int bitNumber = decimalPart.length();  // 总小数位数
                int fristBit = 0;   // 小数位后第一个大于0的位数

                // 找到小数位后第一个大于0的位数
                for (int i = 0; i < decimalPart.length(); i++) {
                    if (decimalPart.charAt(i) != '0') {
                        fristBit = i + 1;
                        break;
                    }
                }

                if(fristBit == 0) {
                    return floatValue;
                } else {
                    if(fristBit <= 4) {
                        if (bitNumber <= 4) {
                            return floatValue;
                        } else {
                            return Math.round(floatValue * 10000.0) / 10000.0f;
                        }
                    } else if (bitNumber <= 6) {
                        if (fristBit <= 6) {
                            return floatValue;
                        } else {
                            return Math.round(floatValue * 1000000.0) / 1000000.0f;
                        }
                    } else {
                        return -99999999.0f;
                    }
                }
            }
        } catch (Exception e) {
            return -99999999.0f;
        }
    }
}
