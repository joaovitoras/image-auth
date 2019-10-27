package util;
import java.awt.image.BufferedImage;
import java.io.IOException;
 
public class ImgDiffPercentage {
  private BufferedImage img1;
  private BufferedImage img2;
  
  public ImgDiffPercentage(BufferedImage img1, BufferedImage img2) throws IOException {
    this.img1 = img1;
    this.img2 = img2;
  }
  
  public double getDifferencePercent() {
    int width = img1.getWidth();
    int height = img1.getHeight();
    int width2 = img2.getWidth();
    int height2 = img2.getHeight();
    if (width != width2 || height != height2) {
      throw new IllegalArgumentException(String.format("As imagens devem ter a mesma dimensão: (%d,%d) vs. (%d,%d)", width, height, width2, height2));
    }

    long diff = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        diff += pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
      }
    }
    long maxDiff = 3L * 255 * width * height;
    System.out.println(100.0 * diff / maxDiff);
    return 100.0 * diff / maxDiff;
  }

  private static int pixelDiff(int rgb1, int rgb2) {
    int r1 = (rgb1 >> 16) & 0xff;
    int g1 = (rgb1 >>  8) & 0xff;
    int b1 =  rgb1        & 0xff;
    int r2 = (rgb2 >> 16) & 0xff;
    int g2 = (rgb2 >>  8) & 0xff;
    int b2 =  rgb2        & 0xff;
    return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
  }

  public BufferedImage getImg1() {
    return img1;
  }

  public BufferedImage getImg2() {
    return img2;
  }
}