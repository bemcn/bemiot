package org.bem.iot.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ConvertUtil {
    /**
     * 字节数组转16进制字符串
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 16进制字符串转字节数组
     * @param hex 16进制字符串
     * @return 字节数组
     */
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new byte[0];
        }

        // 处理奇数长度字符串
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            String subStr = hex.substring(i, i + 2);
            bytes[i/2] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    /**
     * 字节数组转16进制字符串
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 比较两个版本大小
     * @param newVersion 新版本
     * @param oldVersion 旧版本
     * @return 返回 true: 新版本大于旧版本；false: 新版本小于旧版本
     */
    public static boolean versionNotCompare(String newVersion, String oldVersion) {
        if(newVersion.equals(oldVersion)) {
            return true;
        } else {
            newVersion = newVersion.replace("v", "").replace("V", "");
            oldVersion = oldVersion.replace("v", "").replace("V", "");
            String[] nVersionArray = newVersion.split("\\.");
            String[] oVersionArray = oldVersion.split("\\.");
            int nLen = nVersionArray.length;
            int oLen = oVersionArray.length;

            int nv1 = Integer.parseInt(nVersionArray[0]);
            int ov1 = Integer.parseInt(oVersionArray[0]);
            if (nv1 > ov1) {
                return false;
            } else if (nv1 < ov1) {
                return true;
            } else {
                int nv2 = Integer.parseInt(nVersionArray[1]);
                int ov2 = Integer.parseInt(oVersionArray[1]);
                if (nv2 > ov2) {
                    return false;
                } else if (nv2 < ov2) {
                    return true;
                } else {
                    if (nLen < 3 && oLen < 3) {
                        return true;
                    } else {
                        int nv3 = 0;
                        int ov3 = 0;
                        if(nLen >= 3) {
                            nv3 = Integer.parseInt(nVersionArray[2]);
                        }
                        if(oLen >= 3) {
                            ov3 = Integer.parseInt(oVersionArray[2]);
                        }
                        if (nv3 > ov3) {
                            return false;
                        } else if (nv3 < ov3) {
                            return true;
                        } else {
                            if (nLen < 4 && oLen < 4) {
                                return true;
                            } else {
                                int nv4 = 0;
                                int ov4 = 0;
                                if(nLen >= 4) {
                                    nv4 = Integer.parseInt(nVersionArray[3]);
                                }
                                if(oLen >= 4) {
                                    ov4 = Integer.parseInt(oVersionArray[3]);
                                }
                                return nv4 <= ov4;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 容量大小转换器
     * @param capacity 容量
     * @param isRound 是否四舍五入
     * @param bit 小数位数
     * @return 转换后的容量
     */
    public static String formatCapacity(long capacity, boolean isRound, int bit) {
        double tbSize = 1048576d * 1024d * 1024d;
        double gbSize = 1048576d * 1024d;

        StringBuilder bitExpress = new StringBuilder("#");
        if(bit > 0) {
            bitExpress.append(".");
            bitExpress.append("0".repeat(bit));
        }
        DecimalFormat df = new DecimalFormat(bitExpress.toString());
        if(isRound) {
            df.setRoundingMode(RoundingMode.HALF_UP);
        }

        double capNumber;
        String formattedNumber;
        if(capacity >= tbSize) {
            capNumber = capacity / tbSize;
            formattedNumber = df.format(capNumber) + "TB";
        } else if(capacity >= gbSize) {
            capNumber = capacity / gbSize;
            formattedNumber = df.format(capNumber) + "GB";
        } else if(capacity >= 1048576d) {
            capNumber = capacity / 1048576d;
            formattedNumber = df.format(capNumber) + "MB";
        } else if(capacity >= 1024d) {
            capNumber = capacity / 1024d;
            formattedNumber = df.format(capNumber) + "KB";
        } else {
            formattedNumber = capacity + "B";
        }
        return formattedNumber;
    }

    /**
     * 频率Hz转换
     * @param freq 频率
     * @return 转换后频率
     */
    public static String formatVendorFreq(long freq) {
        String vendorFreq;
        if(freq >= 1000000000L) {
            vendorFreq = (freq / 100000000L) + "GHz";
        } else if(freq >= 10000000L) {
            vendorFreq = (freq / 1000000L) + "MHz";
        } else if(freq >= 10000L) {
            vendorFreq = (freq / 1000L) + "KHz";
        } else {
            vendorFreq = freq + "Hz";
        }
        return vendorFreq;
    }

    /**
     * 网速转换
     * @param speed 网速(bps)
     * @return 转换后频率
     */
    public static String formatSpeed(long speed) {
        long tbps = 1024L * 1024L * 1024L * 1024L;
        long gbps = 1024L * 1024L * 1024L;
        long mbps = 1024L * 1024L;
        long kbps = 1024L;
        double speedUnt;
        String speedValue;
        DecimalFormat df = new DecimalFormat("#.00");
        if(speed >= tbps) {
            speedUnt = speed / (double) tbps;
            speedValue = df.format(speedUnt) + "Tbps";
        } else if(speed >= gbps) {
            speedUnt = speed / (double) gbps;
            speedValue = df.format(speedUnt) + "Gbps";
        } else if(speed >= mbps) {
            speedUnt = speed / (double) mbps;
            speedValue = df.format(speedUnt) + "Mbps";
        } else if(speed >= kbps) {
            speedUnt = speed / (double) kbps;
            speedValue = df.format(speedUnt) + "Kbps";
        } else {
            speedValue = speed + "bps";
        }
        return speedValue;
    }

    /**
     * CIDR转换为网络掩码
     * @param cidr CIDR
     * @return 网络掩码
     */
    public static String cidrToNetmask(int cidr) {
        if (cidr < 0 || cidr > 32) {
            throw new IllegalArgumentException("CIDR must be between 0 and 32");
        }

        int netmask = -(1 << (32 - cidr)); // 计算网络掩码的整型值
        return formatNetmask(netmask); // 将整型值转换为点分十进制格式的字符串
    }
    private static String formatNetmask(int netmask) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int shift = 24 - i * 8; // 从最高位开始向左移位
            int byteValue = (netmask >>> shift) & 0xFF; // 获取当前字节的值，并通过&操作符确保其为8位
            sb.append(byteValue).append(".");
        }
        return sb.substring(0, sb.length() - 1); // 去掉最后一个多余的点号
    }

}
