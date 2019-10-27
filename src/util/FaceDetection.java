package util;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetection {
  private String xmlFile = "/usr/share/opencv4/haarcascades/haarcascade_frontalcatface.xml";
  private CascadeClassifier classifier = new CascadeClassifier(xmlFile);
  private MatOfRect faceDetections = new MatOfRect();
  private Mat imageMat;
  private Rect rectCrop = null;
  private int count;
  
  public FaceDetection(Mat imageMat) {
	  this.imageMat = imageMat;
  }
  
  public void detect() {
      classifier.detectMultiScale(imageMat, faceDetections);
      this.count = faceDetections.toArray().length;
      System.out.println(String.format("Detected %s faces", count));

	  for (Rect rect : faceDetections.toArray()) {
	     this.rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
	  }	  
  }
  
  public Boolean hasDetected() {
	  return this.count == 1;
  }
  
  public Mat getImage() {
	  Mat croppedImage = new Mat(imageMat, rectCrop);
	  Size sz = new Size(300, 300);
	  Imgproc.resize( croppedImage, croppedImage, sz );

	  return croppedImage;
  }
}
