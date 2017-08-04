import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

class Menu extends JFrame {
  JPanel pnlMenu = new JPanel() {{
		setBorder(BorderFactory.createEmptyBorder(5, 50, 5, 50));
    setBackground(Color.BLACK);
		setLayout(new GridLayout(3, 1, 10, 10));
	}};
  JButton btnIniciar = new JButton("PROCURAR JOGO") {{
    setBackground(new Color(255, 255, 255));
  }};
  JButton btnSobre = new JButton("SOBRE") {{
    setBackground(new Color(255, 255, 255));
  }};
  JButton btnSair = new JButton("SAIR") {{
    setBackground(new Color(255, 255, 255));
  }};
  JLabel lblTitulo = new JLabel("PARANOIA", SwingConstants.CENTER);

  Menu() {
    super("Paranoia");
    try {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Shamber.ttf")));
    } catch (Exception e) {
      e.printStackTrace();
    }

    setLayout(new BorderLayout());
    setResizable(false);
    getContentPane().setBackground(Color.BLACK);
    getRootPane().setBackground(Color.BLACK);
    getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

    lblTitulo.setForeground(Color.WHITE);
    lblTitulo.setFont(new Font("Shamber", Font.BOLD, 100));

    pnlMenu.add(btnIniciar);
    pnlMenu.add(btnSobre);
    pnlMenu.add(btnSair);

    btnIniciar.setBackground(Color.WHITE);
    btnIniciar.setFont(new Font("Shamber", Font.BOLD, 30));
    btnIniciar.setFocusPainted(false);
    btnSobre.setBackground(Color.WHITE);
    btnSobre.setFont(new Font("Shamber", Font.BOLD, 30));
    btnSobre.setFocusPainted(false);
    btnSair.setBackground(Color.WHITE);
    btnSair.setFont(new Font("Shamber", Font.BOLD, 30));
    btnSair.setFocusPainted(false);

    btnIniciar.addActionListener(btnListener);
    btnSobre.addActionListener(btnListener);
    btnSair.addActionListener(btnListener);

    add(lblTitulo, BorderLayout.NORTH);
    add(pnlMenu, BorderLayout.CENTER);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(new Dimension(600, 500));
    setLocationRelativeTo(null);
    setVisible(true);
  }

  WindowAdapter adapter = new WindowAdapter() {
    @Override
    public void windowClosed(WindowEvent e) {
      setVisible(true);
    };
  };

  ActionListener btnListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if(e.getSource() == btnIniciar) {
        Principal princ = new Principal();
        princ.addWindowListener(adapter);
        setVisible(false);
      } else if(e.getSource() == btnSobre) {
        Sobre about = new Sobre();
        about.addWindowListener(adapter);
        setVisible(false);
      } else if(e.getSource() == btnSair) {
        System.exit(0);
      }
    }
  };


  public static void main(String[] args) {
    new Menu();
  }

}
