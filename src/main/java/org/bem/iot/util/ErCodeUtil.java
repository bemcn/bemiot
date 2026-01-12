package org.bem.iot.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ErCodeUtil {
    /**
     * 生成二维码图片并保存到指定路径
     * @param data     二维码内容（文本/URL/WiFi配置等）
     * @param width    二维码宽度（像素）
     * @param height   二维码高度（像素）
     * @param filePath 保存路径（需包含.png后缀）
     * @return 生成的二维码文件路径，失败时返回null
     */
    public static boolean createErCode(String data, int width, int height, String filePath) {
        try {
            //1.设置二维码生成参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 支持中文等特殊字符
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 30%容错率
            hints.put(EncodeHintType.MARGIN, 1); // 二维码边框空白宽度

            //2.生成二维码矩阵数据
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height, hints);

            //3.转换为图像对象
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {

                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            //4.保存为PNG文件
            File file = new File(filePath);
            if (file.exists()) {
                return true;
            } else {
                ImageIO.write(image, "png", file);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
