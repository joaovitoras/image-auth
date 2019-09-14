package view;
import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import model.Person;
import util.ImgDiffPercentage;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Color;

import javax.swing.Box;
import javax.swing.BoxLayout;

public class Main {
  private JFrame frame, webcamPopup;
  private JPanel layout, menu, content, webcamPopupLayout;
  private JButton btnLogin, btnRegister, btnTakePicture;
  private JLabel lblStatus;
  private Webcam webcam;
  private WebcamPanel webcamPanel;
  private JTextField tfPersonName;
  private ArrayList<Person> persons = new ArrayList<>();
  private String action;
  private Person currentPerson;

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Main window = new Main();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Main() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    setupMainFrame();
    setupLayout();
    setupMenu();
    setupContent();
    setupWebcamPopup();

    frame.setLocationRelativeTo(null);
  }

  private void setupMainFrame() {
    frame = new JFrame();
    frame.setBounds(100, 100, 450, 300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
  }

  private void setupLayout() {
    layout = new JPanel();
    layout.setLayout(null);
    frame.getContentPane().add(layout);
  }

  private void setupMenu() {
    menu = new JPanel();
    menu.setBounds(0, 0, 128, 278);
    menu.setLayout(new GridLayout(0, 1, 0, 0));

    setupActions();
    layout.add(menu);
  }

  private void setupContent() {
    lblStatus = new JLabel("Sistema bloqueado");
    content = new JPanel();
    content.setBounds(129, 0, 321, 278);
    content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
    content.add(Box.createHorizontalGlue());
    content.add(lblStatus);
    content.add(Box.createHorizontalGlue());
    content.setBackground(Color.YELLOW);
    layout.add(content);
  }

  private void setupWebcamPopup() {
    webcamPopupLayout = new JPanel();
    webcamPopupLayout.setBorder(new EmptyBorder(5, 5, 5, 5));
    webcamPopupLayout.setLayout(null);

    webcamPopup = new JFrame("Cadastrar foto");
    webcamPopup.setResizable(false);
    webcamPopup.setBounds(100, 100, 256, 259);
    webcamPopup.setContentPane(webcamPopupLayout);
    webcamPopup.setLocationRelativeTo(null);

    tfPersonName = new JTextField();
    tfPersonName.setBounds(6, 197, 172, 35);
    tfPersonName.setColumns(10);
    webcamPopupLayout.add(tfPersonName);

    setupTakePicture();
    setupWebcamPanel();
  }

  private void setupActions() {
    setupLogin();
    setupRegister();
  }

  private void setupLogin() {
    btnLogin = new JButton("Entrar");
    btnLogin.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        action = "login";
        currentPerson = null;
        tfPersonName.setVisible(false);
        webcam.open();
        webcamPanel.start();
        webcamPopup.setVisible(true);
      }
    });
    menu.add(btnLogin);
  }

  private void setupRegister() {
    btnRegister = new JButton("Cadastrar Foto");
    btnRegister.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        action = "create";
        tfPersonName.setVisible(true);
        webcam.open();
        webcamPanel.start();
        webcamPopup.setVisible(true);
      }
    });
    menu.add(btnRegister);
  }

  private void setupTakePicture() {
    btnTakePicture = new JButton("Tirar");
    btnTakePicture.setBounds(180, 198, 70, 34);
    webcamPopupLayout.add(btnTakePicture);

    btnTakePicture.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (action.equals("create"))
          createPerson();
        else if (action.equals("login"))
          login();

        webcamPanel.stop();
        webcam.close();
        webcamPopup.setVisible(false);
        action = null;
      }
    });
  }

  private void setupWebcamPanel() {
    webcam = Webcam.getDefault();
    webcam.setViewSize(WebcamResolution.VGA.getSize());

    webcamPanel = new WebcamPanel(webcam, false);
    webcamPanel.setMirrored(true);
    webcamPanel.setBounds(0, 0, 256, 192);


    webcamPopupLayout.add(webcamPanel);
  }

  private void createPerson() {
    persons.add(new Person(tfPersonName.getText(), webcam.getImage()));
    tfPersonName.setText("");
  }

  private void login() {
    BufferedImage image = webcam.getImage();
    persons.forEach((person) -> imgDiffPercentage(person, image));

    if (currentPerson != null) {
      lblStatus.setText("Bem vindo " + currentPerson.getName());
      content.setBackground(Color.GREEN);
    } else {
      lblStatus.setText("Você não está autorizado!");
      content.setBackground(Color.RED);
    }
  }

  private void imgDiffPercentage(Person person, BufferedImage image) {
    try {
      ImgDiffPercentage idp = new ImgDiffPercentage(image, person.getImg());
      if (idp.getDifferencePercent() < 10.0) {
        currentPerson = person;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
