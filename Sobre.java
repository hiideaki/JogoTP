import javax.swing.*;
import java.awt.*;

class Sobre extends JFrame {
  JEditorPane editPane = new JEditorPane();
  JScrollPane scrollPane = new JScrollPane(editPane);
  java.net.URL URL = getClass().getResource("html/about.html");

  Sobre() {
    editPane.setEditable(false);
    scrollPane.setPreferredSize(new Dimension(600, 600));
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.getVerticalScrollBar().setUnitIncrement(4);
    if (URL != null) {
      try {
        editPane.setPage(URL);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("Arquivo não encontrado: html/about.html");
    }

    add(scrollPane  );

    setVisible(true);
    pack();
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }

}
