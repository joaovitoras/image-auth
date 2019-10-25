package model;
import java.awt.image.BufferedImage;

public class Person {
  private String name;
  private BufferedImage img;
  private int permission;

  public Person(String name, int permission, BufferedImage img) {
    this.name = name;
    this.img = img;
    this.permission = permission;
  }

  public String getName() {
    return name;
  }

  public BufferedImage getImg() {
    return img;
  }


  public int getPermission() {
 	return this.permission;
  }
}
