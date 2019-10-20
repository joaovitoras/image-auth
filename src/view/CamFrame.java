package view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import util.FaceDetection;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class CamFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane, panel;
	private DaemonThread webCamDrawThread = null;
    private VideoCapture webSource = null;
    private Mat mat = new Mat();
    private MatOfByte mem = new MatOfByte();
    private JTextField textField;
    private BufferedImage img;
	private Boolean showName;
    
	public CamFrame() {
		this(true);
	}

	public CamFrame(Boolean showName) {
		this.showName = showName;
		setupMainFrame();
		setupCamPanel();
		setupScreenshot();
		startWebCam();
    }
	
	private void startWebCam() {
		webSource = new VideoCapture(0);
		webCamDrawThread = new DaemonThread();
        Thread t = new Thread(webCamDrawThread);
        t.setDaemon(true);
        webCamDrawThread.runnable = true;
        t.start();
	}

	private void setupScreenshot() {
		JButton btnTirar = new JButton("Tirar");
		btnTirar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		        FaceDetection faceDetection = new FaceDetection(mat);
		        faceDetection.detect();
		        
		        if (faceDetection.hasDetected()) {
		        	mat = faceDetection.getImage();
		        	webCamDrawThread.runnable = false;
		            webSource.release();
		            setVisible(false);
		            shotCallback(textField.getText(), getImage());
		            dispose();
		        } else {
		        	JOptionPane.showMessageDialog(null, "Imagem inválida", "Erro", JOptionPane.ERROR_MESSAGE);
		        }	
			}
		});
		btnTirar.setBounds(555, 390, 117, 37);
		contentPane.add(btnTirar);
		textField = new JTextField();
		textField.setBounds(0, 390, 555, 37);
		textField.setColumns(10);
		textField.setVisible(showName);
		contentPane.add(textField);
		setVisible(true);
	}
	
	public void shotCallback(String name, BufferedImage bufferedImage) {
	}

	private void setupCamPanel() {
		panel = new JPanel();
		panel.setBounds(0, 0, 672, 386);
		contentPane.add(panel);
	}

	private void setupMainFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 680, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
	}
	
    public void getSpace() {
        int type = 0;
        if (mat.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (mat.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int w = mat.cols();
        int h = mat.rows();
        if (img == null || img.getWidth() != w || img.getHeight() != h || img.getType() != type) {
            img = new BufferedImage(w, h, type);
        }
    }

    BufferedImage getImage() {
        getSpace();
        WritableRaster raster = img.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);
        return img;
    }


	class DaemonThread implements Runnable
    {
	    protected volatile boolean runnable = false;
	
	    @Override
	    public  void run()
	    {
	        synchronized(this)
	        {
	            while(runnable)
	            {
	                if(webSource.grab())
	                {
				    	try 
				    	{
				    		webSource.retrieve(mat);
		                    Imgcodecs.imencode(".bmp", mat, mem);
						    Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
			
						    BufferedImage buff = (BufferedImage) im;
						    Graphics g = contentPane.getGraphics();

						    g.drawImage(buff, 0, 0, panel.getWidth(), panel.getHeight() , 0, 0, buff.getWidth(), buff.getHeight(), null);
	
						    if(runnable == false) {
						    	this.wait();
						    }
						 }
						catch(Exception e) {
							e.printStackTrace();
						}
	                }
	            }
	        }
	    }
    }
}
