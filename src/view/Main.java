package view;
import javax.swing.JFrame;

import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.JPanel;

import org.opencv.core.Core;
import model.Person;
import util.ImgDiffPercentage;
import util.PermissionManager;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

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
  private JButton btnNewButton_2;
  private JButton btnNewButton_1;
  private JButton btnNewButton;

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
    content.setBounds(153, 0, 289, 37);
    content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
    content.add(Box.createHorizontalGlue());
    content.add(lblStatus);
    content.add(Box.createHorizontalGlue());
    content.setBackground(Color.YELLOW);
    layout.add(content);
    
    JPanel panel = new JPanel();
    panel.setBounds(153, 36, 289, 231);
    layout.add(panel);
    panel.setLayout(new GridLayout(0, 1, 0, 0));
    
    btnNewButton_1 = new JButton("Acessar Nivel 1");
    btnNewButton_1.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent arg0) {
    		checkPermission(1);
    	}
    });
    btnNewButton_1.setVisible(false);
    panel.add(btnNewButton_1);
    
    btnNewButton_2 = new JButton("Acessar Nivel 2");
    btnNewButton_2.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent arg0) {
    		checkPermission(2);
    	}
    });
    btnNewButton_2.setVisible(false);
    panel.add(btnNewButton_2);
    
    btnNewButton = new JButton("Acessar Nivel 3");
    btnNewButton.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent arg0) {
    		checkPermission(3);
    	}
    });
    btnNewButton.setVisible(false);

    panel.add(btnNewButton);
    hideButtons();
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
			private static final long serialVersionUID = 1L;

			public void shotCallback(String name, int permission, BufferedImage image) {
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
			private static final long serialVersionUID = 1L;

			public void shotCallback(String name, int permission, BufferedImage image) {
                createPerson(name, permission, image);
            }
        };
      }
    });
    menu.add(btnRegister);
  }

  private void createPerson(String name, int permission, BufferedImage image) {
    persons.add(new Person(name, permission, image));
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
      showButtons();
    } else {
      lblStatus.setText("Você não está autorizado!");
      content.setBackground(Color.RED);
      hideButtons();
    }
  }

  private boolean findPerson(Person person, BufferedImage image) {
    try {
      ImgDiffPercentage idp = new ImgDiffPercentage(image, person.getImg());

      if (idp.getDifferencePercent() < 8.0) {
    	  File saved = new File("saved.jpg");
    	  File current = new File("current.jpg");
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
  

	private void checkPermission(int permission) {
		if (PermissionManager.hasAccess(currentPerson, permission)) {
			JOptionPane.showMessageDialog(null, "Você esta no nível " + permission, "Sucesso!", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "Você não ter permissão de acessar este nível", "Permissão Negada!", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void hideButtons() {
	    btnNewButton_2.setVisible(false);
	    btnNewButton_1.setVisible(false);
	    btnNewButton.setVisible(false);
	}
	
	private void showButtons() {
	    btnNewButton_2.setVisible(true);
	    btnNewButton_1.setVisible(true);
	    btnNewButton.setVisible(true);
	}
}
