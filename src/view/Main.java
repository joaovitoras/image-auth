package view;
import javax.swing.JFrame;

import java.awt.GridLayout;
import javax.swing.JPanel;

import org.opencv.core.Core;
import model.Person;
import util.ImgDiffPercentage;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class Main {
  private JFrame frame;
  private JPanel layout, menu, content;
  private JButton btnLogin, btnRegister;
  private JLabel lblStatus;
  private ArrayList<Person> persons = new ArrayList<>();
  private Person currentPerson;

  public static void main(String[] args) {
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

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

    frame.setLocationRelativeTo(null);
  }

  private void setupMainFrame() {
    frame = new JFrame("Sessão");
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
    menu.setBounds(0, 0, 152, 267);
    menu.setLayout(new GridLayout(0, 1, 0, 0));

    setupActions();
    layout.add(menu);
  }

  private void setupContent() {
    lblStatus = new JLabel("Sistema bloqueado");
    content = new JPanel();
    content.setBounds(153, 0, 289, 267);
    content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
    content.add(Box.createHorizontalGlue());
    content.add(lblStatus);
    content.add(Box.createHorizontalGlue());
    content.setBackground(Color.YELLOW);
    layout.add(content);
  }

  private void setupActions() {
    setupLogin();
    setupRegister();
  }

  private void setupLogin() {
    btnLogin = new JButton("Entrar");
    btnLogin.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        new CamFrame(false) {
			public void shotCallback(String name, BufferedImage image) {
                login(image);
            }
        };
      }
    });
    menu.add(btnLogin);
  }

  private void setupRegister() {
    btnRegister = new JButton("Cadastrar Foto");
    btnRegister.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new CamFrame() {
			public void shotCallback(String name, BufferedImage image) {
                createPerson(name, image);
            }
        };
      }
    });
    menu.add(btnRegister);
  }

  private void createPerson(String name, BufferedImage image) {
    persons.add(new Person(name, image));
  }

  private void login(BufferedImage image) {
	for (int i = 0; i < persons.size(); i++) {
		if (findPerson(persons.get(i), image)) {
			break;
		}
	}

    if (currentPerson != null) {
      lblStatus.setText("Bem vindo " + currentPerson.getName());
      content.setBackground(Color.GREEN);
    } else {
      lblStatus.setText("Você não está autorizado!");
      content.setBackground(Color.RED);
    }
  }

  private boolean findPerson(Person person, BufferedImage image) {
    try {
      ImgDiffPercentage idp = new ImgDiffPercentage(image, person.getImg());
      System.out.println(idp.getDifferencePercent() < 5.0);

      if (idp.getDifferencePercent() < 5.0) {
    	  File saved = new File("/home/joaovitoras/Desktop/saved.jpg");
    	  File current = new File("/home/joaovitoras/Desktop/current.jpg");
    	  ImageIO.write(person.getImg(), "jpg", saved);
    	  ImageIO.write(image, "jpg", current);

		  currentPerson = person;
		  return true;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    currentPerson = null;
	return false;
  }
}
