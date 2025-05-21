package com.pyk.bysj.books.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class CaptchaUtil {

  // 生成随机验证码（4位数字）
  public static String generateCode() {
    return String.format("%04d", new Random().nextInt(9999));
  }

  // 生成验证码图片
  public static byte[] generateImage(String code) throws IOException {
    int width = 100;
    int height = 40;

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();

    // 设置背景
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);

    // 绘制干扰线
    Random random = new Random();
    for (int i = 0; i < 5; i++) {
      g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
      g.drawLine(random.nextInt(width), random.nextInt(height),
              random.nextInt(width), random.nextInt(height));
    }

    // 绘制验证码
    g.setFont(new Font("Arial", Font.BOLD, 30));
    for (int i = 0; i < code.length(); i++) {
      g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
      g.drawString(String.valueOf(code.charAt(i)), 20 * i + 10, 30);
    }

    g.dispose();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, "jpg", baos);
    return baos.toByteArray();
  }
}