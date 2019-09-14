package model;
import java.awt.image.BufferedImage;

public class Person {
  private String name;
  private BufferedImage img;

  public Person(String name, BufferedImage img) {
    this.setName(name);
    this.setImg(img);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BufferedImage getImg() {
    return img;
  }

  public void setImg(BufferedImage img) {
    this.img = img;
  }
}
